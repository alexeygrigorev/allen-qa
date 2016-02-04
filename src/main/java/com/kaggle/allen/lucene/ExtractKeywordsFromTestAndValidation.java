package com.kaggle.allen.lucene;

import java.io.PrintWriter;
import java.util.Iterator;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.kaggle.allen.Question;
import com.kaggle.allen.Read;
import com.kaggle.allen.text.Parser;
import com.kaggle.allen.text.ParserFactory;

public class ExtractKeywordsFromTestAndValidation {

    public static void main(String[] args) throws Exception {
        Parser parser = ParserFactory.createParser();
        Iterator<Question> allData = Read.allData();

        Multiset<String> cnt = HashMultiset.create();

        allData.forEachRemaining(q -> {
            cnt.addAll(parser.parse(q.getContent()));
            for (String a : q.getAnswers()) {
                cnt.addAll(parser.parse(a));
            }
        });

        PrintWriter pw = new PrintWriter("data/important_keywords.txt");
        cnt.entrySet().stream()
            .map(e -> e.getElement())
            .sorted()
            .forEach(pw::println);
        pw.flush();
        pw.close();
    }

}
