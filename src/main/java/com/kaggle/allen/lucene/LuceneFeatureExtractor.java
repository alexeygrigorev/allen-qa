package com.kaggle.allen.lucene;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import com.google.common.collect.Lists;
import com.kaggle.allen.Question;
import com.kaggle.allen.Read;
import com.kaggle.allen.text.Parser;
import com.kaggle.allen.text.ParserFactory;

public class LuceneFeatureExtractor {

    public static void main(String[] args) throws Exception {
        IndexSearcher wikiSearcher = searcher(new File("data/index"));
        IndexSearcher ck12Searcher = searcher(new File("data/ck12-index"));

        Iterator<Question> training = Read.trainingData();
        Parser parser = ParserFactory.createParser();

        AtomicInteger cnt = new AtomicInteger(0);
        training.forEachRemaining(q -> {
            try {
                search(wikiSearcher, parser, q);
                search(ck12Searcher, parser, q);
                cnt.incrementAndGet();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

    }

    public static void search(IndexSearcher searcher, Parser parser, Question q) throws IOException {
        String question = q.getContent();
        List<ScoredDocument> questionResults = queryIndex(searcher, parser, question, 10);

        List<List<ScoredDocument>> justAnswers = Lists.newArrayList();
        List<List<ScoredDocument>> questionAndAnswers = Lists.newArrayList();

        for (String answer : q.getAnswers()) {
            List<ScoredDocument> docsQuestionAnswers = queryIndex(searcher, parser, question + " " + answer, 10);
            questionAndAnswers.add(docsQuestionAnswers);

            List<ScoredDocument> docsAnswer = queryIndex(searcher, parser, answer, 10);
            justAnswers.add(docsAnswer);
        }

        System.out.println(
                questionResults + "\t" + justAnswers + "\t" + questionAndAnswers + "\t" + q.getCorrectAnswer());

    }

    public static IndexSearcher searcher(File wikiIndex) throws IOException {
        FSDirectory index = FSDirectory.open(wikiIndex);
        DirectoryReader reader = DirectoryReader.open(index);
        return new IndexSearcher(reader);
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
