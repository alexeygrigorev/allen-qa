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

        FSDirectory index = FSDirectory.open(new File("data/index"));
        DirectoryReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);

        Iterator<Question> training = Read.trainingData();
        Parser parser = ParserFactory.createParser();

        AtomicInteger cnt = new AtomicInteger(0);
        training.forEachRemaining(q -> {
            try {
                List<ScoredDocument> question = queryIndex(searcher, parser, q.getContent());
                List<ScoredDocument> a0 = queryIndex(searcher, parser, q.getAnswers().get(0));
                List<ScoredDocument> a1 = queryIndex(searcher, parser, q.getAnswers().get(1));
                List<ScoredDocument> a2 = queryIndex(searcher, parser, q.getAnswers().get(2));
                List<ScoredDocument> a3 = queryIndex(searcher, parser, q.getAnswers().get(3));

                System.out.println(
                        question + "\t" + a0 + "\t" + a1 + "\t" + a2 + "\t" + a3 + "\t" + q.getCorrectAnswer());
                cnt.incrementAndGet();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

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
