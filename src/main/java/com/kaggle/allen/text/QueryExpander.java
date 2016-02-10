package com.kaggle.allen.text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.kaggle.allen.lucene.NgramExtractor;

public class QueryExpander {

    private Map<String, List<String>> glossary;

    public static QueryExpander create() throws IOException {
        NgramExtractor nGramExtractor = ParserFactory.createNGramExtractor();
        return create(nGramExtractor);
    }

    public static QueryExpander create(NgramExtractor nGramExtractor) throws IOException {
        Map<String, List<String>> glossary = readGlossary(nGramExtractor);
        return new QueryExpander(glossary);
    }

    public QueryExpander(Map<String, List<String>> glossary) throws IOException {
        this.glossary = glossary;
    }

    public List<String> expand(List<String> tokens) {
        List<String> result = Lists.newArrayListWithCapacity(tokens.size());

        for (String token : tokens) {
            result.add(token);
            if (glossary.containsKey(token)) {
                result.addAll(glossary.get(token));
            }
        }

        return result;
    }

    private static Map<String, List<String>> readGlossary(NgramExtractor nGramExtractor) throws IOException {
        Map<String, List<String>> glossary = new HashMap<>();

        List<String> lines = FileUtils.readLines(new File("data/glossary.txt"), "UTF-8");

        for (String line : lines) {
            String[] split = line.toLowerCase().split("\t");
            if (split.length < 2) {
                continue;
            }

            String term = split[0];
            String definition = split[1];
            List<String> ngrams = nGramExtractor.ngrams(definition);

            if (!glossary.containsKey(term)) {
                glossary.put(term, new ArrayList<>());
            }

            glossary.get(term).addAll(ngrams);
        }

        return glossary;
    }

}
