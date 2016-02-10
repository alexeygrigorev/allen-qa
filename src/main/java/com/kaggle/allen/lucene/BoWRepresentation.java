package com.kaggle.allen.lucene;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.Weight;

public class BoWRepresentation {

    private static final String CONTENT = "content";

    public static void main(String[] args) throws IOException {
        File index = new File("data/ck12-index");
        IndexSearcher searcher = LuceneFeatureExtractor.searcher(index);

        List<String> tokens = Arrays.asList("food industry", "selective root", "reflex reaction", "science teacher");
//        Query query = LuceneFeatureExtractor.createQuery(tokens);
        
        Weight weight = searcher.createNormalizedWeight(new TermQuery(new Term(CONTENT, "food industry")));
        System.out.println(weight);
        
        // LuceneFeatureExtractor.query(searcher, query, limit)

        // TODO Auto-generated method stub

    }

}
