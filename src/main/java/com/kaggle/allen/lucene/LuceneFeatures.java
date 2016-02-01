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

public class LuceneFeatures {

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

    private static void search(IndexSearcher searcher, Parser parser, Question q) throws IOException {
        String question = q.getContent();
        List<ScoredDocument> questionResults = queryIndex(searcher, parser, question);

        List<List<ScoredDocument>> justAnswers = Lists.newArrayList();
        List<List<ScoredDocument>> questionAndAnswers = Lists.newArrayList();

        for (String answer : q.getAnswers()) {
            List<ScoredDocument> docsQuestionAnswers = queryIndex(searcher, parser, question + " " + answer);
            questionAndAnswers.add(docsQuestionAnswers);

            List<ScoredDocument> docsAnswer = queryIndex(searcher, parser, answer);
            justAnswers.add(docsAnswer);
        }

        System.out.println(
                questionResults + "\t" + justAnswers + "\t" + questionAndAnswers + "\t" + q.getCorrectAnswer());

    }

    private static IndexSearcher searcher(File wikiIndex) throws IOException {
        FSDirectory index = FSDirectory.open(wikiIndex);
        DirectoryReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        return searcher;
    }

    private static List<ScoredDocument> queryIndex(IndexSearcher searcher, Parser parser, String content)
            throws IOException {
        List<String> tokens = parser.parse(content);
        Query query = createQuery(tokens);

        TopDocs results = searcher.search(query, 10);
        ScoreDoc[] hits = results.scoreDocs;
        List<ScoredDocument> list = Lists.newArrayList();
        for (int i = 0; i < hits.length; i++) {
            Document doc = searcher.doc(hits[i].doc);
            String title = doc.get("title");
            float score = hits[i].score;
            list.add(new ScoredDocument(title, score));
        }
        return list;
    }

    public static class ScoredDocument {
        private String title;
        private float score;

        public ScoredDocument(String title, float score) {
            this.title = title;
            this.score = score;
        }

        @Override
        public String toString() {
            return title + "," + String.format("%.5f", score);
        }
    }

    private static Query createQuery(List<String> tokens) {
        BooleanQuery aggregate = new BooleanQuery();
        for (String token : tokens) {
            TermQuery termQuery = new TermQuery(new Term("content", token));
            aggregate.add(termQuery, Occur.SHOULD);
        }

        return aggregate;
    }

}
