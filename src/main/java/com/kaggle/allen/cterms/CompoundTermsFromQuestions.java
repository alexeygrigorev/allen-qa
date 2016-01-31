package com.kaggle.allen.cterms;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import com.google.common.collect.Sets;
import com.kaggle.allen.text.FilterChain;
import com.kaggle.allen.text.Tokenizer;

public class CompoundTermsFromQuestions {

    public static void main(String[] args) throws Exception {
        Tokenizer tokenizer = new Tokenizer();
        Set<String> stopwords = Sets.newHashSet(FileUtils.readLines(new File("src/main/resources/stopwords-en.txt")));
        FilterChain chain = new FilterChain(stopwords);

        HashSet<String> seens = Sets.newHashSet();

        PrintWriter pw = new PrintWriter(new File("compound-terms-questions.txt"));

        LineIterator li = FileUtils.lineIterator(new File("/home/agrigorev/Downloads/allen/training_set.tsv"));
        li.next(); // header
        while (li.hasNext()) {
            String line = li.next().toLowerCase();

            String[] split = line.split("\t");

            List<String> answers = Arrays.asList(split).subList(3, split.length);
            for (String answer : answers) {
                List<String> normalized = tokenizer.lemmas(answer);
                normalized = chain.process(normalized);
                if (normalized.size() >= 2 && normalized.size() <= 3) {
                    String result = String.join(" ", normalized);
                    if (seens.contains(result)) {
                        continue;
                    }
                    seens.add(result);
                    pw.println(result);
                }
            }
        }

        li = FileUtils.lineIterator(new File("/home/agrigorev/Downloads/allen/validation_set.tsv"));
        li.next(); // header
        while (li.hasNext()) {
            String line = li.next().toLowerCase();

            String[] split = line.split("\t");

            List<String> answers = Arrays.asList(split).subList(2, split.length);
            for (String answer : answers) {
                List<String> normalized = tokenizer.lemmas(answer);
                normalized = chain.process(normalized);
                if (normalized.size() >= 2 && normalized.size() <= 3) {
                    String result = String.join(" ", normalized);
                    if (seens.contains(result)) {
                        continue;
                    }
                    seens.add(result);
                    pw.println(result);
                }
            }
        }

        pw.flush();
        pw.close();
    }

}
