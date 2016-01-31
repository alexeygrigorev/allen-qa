package com.kaggle.allen.cterms;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.Sets;
import com.kaggle.allen.text.FilterChain;
import com.kaggle.allen.text.Tokenizer;

public class CompoundTermsFromGlossary {

    public static void main(String[] args) throws Exception {
        Tokenizer tokenizer = new Tokenizer();

        Set<String> stopwords = Sets.newHashSet(FileUtils.readLines(new File("src/main/resources/stopwords-en.txt")));
        FilterChain chain = new FilterChain(stopwords);

        HashSet<String> seens = Sets.newHashSet();

        PrintWriter pw = new PrintWriter(new File("compound-terms-glossary.txt"));

        List<String> glossary = FileUtils.readLines(new File("/home/agrigorev/Downloads/allen/data/glossary"));

        for (String line : glossary) {
            line = line.toLowerCase();

            String[] split = line.split("\t");
            String term = split[0];
            List<String> normalized = tokenizer.lemmas(term);
            normalized = chain.process(normalized);

            if (normalized.size() >= 2 && normalized.size() <= 3) {
                System.out.println(normalized);
                String result = String.join(" ", normalized);
                if (seens.contains(result)) {
                    continue;
                }
                seens.add(result);
                pw.println(result);
            }
        }

        pw.flush();
        pw.close();
    }
}
