package com.kaggle.allen.text;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;

/**
 * 
 * @author agrigorev
 *
 */
public class ParserFactory {

    public static Parser createParser() throws Exception {
        CharSource stopWordsSource = stopWordsCharSource();
        Set<String> stopWords = ImmutableSet.copyOf(stopWordsSource.readLines());

        List<String> compoundTermSource = readCompoundTermDictionary().readLines();
        List<List<String>> compoundTermList = Lists.transform(compoundTermSource, s -> Arrays.asList(s.split("\\s+")));

        Set<List<String>> compoundTerms = ImmutableSet.copyOf(compoundTermList);
        CompoundTermsExtractor termsExtractor = new CompoundTermsExtractor(compoundTerms);

        FilterChain filterChain = new FilterChain(stopWords);
        return new Parser(filterChain, termsExtractor);
    }

    private static CharSource readCompoundTermDictionary() throws IOException {
        CharSource questions = resourceToCharSource("compound-terms-questions.txt");
        CharSource glossary = resourceToCharSource("compound-terms-glossary.txt");
        CharSource nounPhrases = resourceToCharSource("compound-terms-noun-phrases.txt");
        return CharSource.concat(questions, glossary, nounPhrases);
    }

    private static CharSource stopWordsCharSource() {
        CharSource enStopwords = resourceToCharSource("stopwords-en.txt");
        return CharSource.concat(enStopwords);
    }

    private static CharSource resourceToCharSource(String path) {
        final URL resource = Resources.getResource(path);
        return Resources.asCharSource(resource, Charsets.UTF_8);
    }

}
