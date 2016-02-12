package com.kaggle.allen.lucene;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.store.FSDirectory;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Ordering;
import com.kaggle.allen.Question;
import com.kaggle.allen.Read;
import com.kaggle.allen.text.Parser;
import com.kaggle.allen.text.ParserFactory;

public class LuceneFeatureExtractor {

    private static final DefaultSimilarity TF_IDF_SIMILARITY = new DefaultSimilarity();

    public static void main(String[] args) throws Exception {
        IndexSearcher ck12Searcher = searcher(new File("data/ck12-index"));

        Parser parser = ParserFactory.createParser();
        Question question = Read.allData().next();
        List<String> terms = parser.parse(question.getContent());
        List<ScoredTerm> weights = calculateWeights(ck12Searcher, terms);

        for (ScoredTerm term : weights) {
            System.out.println(term);
        }
    }

    public static IndexSearcher searcher(File wikiIndex) throws IOException {
        FSDirectory index = FSDirectory.open(wikiIndex);
        DirectoryReader reader = DirectoryReader.open(index);
        return new IndexSearcher(reader);
    }

    public static List<ScoredTerm> calculateWeights(IndexSearcher searcher, List<String> terms) throws IOException {
        List<ScoredTerm> result = Lists.newArrayList();

        CollectionStatistics collectionStats = searcher.collectionStatistics("content");
        IndexReaderContext context = searcher.getTopReaderContext();

        Multiset<String> counts = HashMultiset.create(terms);
        long totalDocCount = collectionStats.docCount();

        for (Multiset.Entry<String> entry : counts.entrySet()) {
            String rawTerm = entry.getElement();
            int count = entry.getCount();

            Term term = new Term("content", rawTerm);
            TermContext termContext = TermContext.build(context, term);

            TermStatistics termStats = searcher.termStatistics(term, termContext);
            long docFreq = termStats.docFreq();
            long totalTermFreq = termStats.totalTermFreq();

            float idf = TF_IDF_SIMILARITY.idf(docFreq, collectionStats.docCount());
            float tf = TF_IDF_SIMILARITY.tf(count);

            double percentageOfDocs = docFreq * 1.0 / totalDocCount;

            ScoredTerm scoredTerm = new ScoredTerm(rawTerm, totalTermFreq, docFreq, tf * idf, percentageOfDocs);
            result.add(scoredTerm);
        }

        Ordering<ScoredTerm> tfTdfOrdering = Ordering.natural().onResultOf(ScoredTerm::getTfIdf).reverse();
        return tfTdfOrdering.immutableSortedCopy(result);
    }

    public static class ScoredTerm {
        private String term;
        private long globalTermFrequency;
        private long documentFrequency;
        private double tfIdf;
        private double percentageOfDocs;

        public ScoredTerm(String term, long globalTermFrequency, long documentFrequency, double tfIdf,
                double percentageOfDocs) {
            this.term = term;
            this.globalTermFrequency = globalTermFrequency;
            this.documentFrequency = documentFrequency;
            this.tfIdf = tfIdf;
            this.percentageOfDocs = percentageOfDocs;
        }

        public String getTerm() {
            return term;
        }

        public long getGlobalTermFrequency() {
            return globalTermFrequency;
        }

        public long getDocumentFrequency() {
            return documentFrequency;
        }

        public double getTfIdf() {
            return tfIdf;
        }

        public double getPercentageOfDocs() {
            return percentageOfDocs;
        }

        @Override
        public String toString() {
            return "ScoredTerm [term=" + term + ", globalTermFrequency=" + globalTermFrequency + ", documentFrequency="
                    + documentFrequency + ", tfIdf=" + tfIdf + ", percentageOfDocs=" + percentageOfDocs + "]";
        }

    }

    public static List<ScoredDocument> queryIndex(IndexSearcher searcher, Parser parser, String content, int limit)
            throws IOException {
        List<String> tokens = parser.parse(content);
        Query query = createQuery(tokens);
        return query(searcher, query, limit);
    }

    public static List<ScoredDocument> query(IndexSearcher searcher, Query query, int limit) throws IOException {
        TopDocs results = searcher.search(query, limit);
        ScoreDoc[] hits = results.scoreDocs;
        List<ScoredDocument> list = Lists.newArrayList();

        for (int i = 0; i < hits.length; i++) {
            int docId = hits[i].doc;
            Document doc = searcher.doc(docId);
            String title = doc.get("title");
            float score = hits[i].score;
            list.add(new ScoredDocument(i, docId, title, score));
        }

        return list;
    }

    public static ScoreDoc[] lightQuery(IndexSearcher searcher, Query query, int limit) throws IOException {
        TopDocs results = searcher.search(query, limit);
        return results.scoreDocs;
    }

    public static class ScoredDocument {
        private String title;
        private float score;
        private int position;
        private int docId;

        public ScoredDocument(int position, int docId, String title, float score) {
            this.position = position;
            this.docId = docId;
            this.title = title;
            this.score = score;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public float getScore() {
            return score;
        }

        public void setScore(float score) {
            this.score = score;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public int getDocId() {
            return docId;
        }

        public void setDocId(int docId) {
            this.docId = docId;
        }

    }

    public static Query createQuery(Iterable<String> tokens) {
        return createQuery(tokens, Occur.SHOULD);
    }

    public static Query createQuery(Iterable<String> tokens, Occur occur) {
        BooleanQuery aggregate = new BooleanQuery();
        for (String token : tokens) {
            TermQuery termQuery = new TermQuery(new Term("content", token));
            aggregate.add(termQuery, occur);
        }

        return aggregate;
    }

    public static Query createMustHaveQuery(List<String> question, List<String> answer) {
        BooleanQuery aggregate = new BooleanQuery();
        for (String token : question) {
            TermQuery termQuery = new TermQuery(new Term("content", token));
            aggregate.add(termQuery, Occur.SHOULD);
        }
        for (String token : answer) {
            TermQuery termQuery = new TermQuery(new Term("content", token));
            aggregate.add(termQuery, Occur.MUST);
        }

        return aggregate;
    }
}
