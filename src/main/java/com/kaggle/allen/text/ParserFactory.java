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
import com.kaggle.allen.lucene.NgramExtractor;

/**
 * 
 * @author agrigorev
 *
 */
public class ParserFactory {

    public static Parser createParser() throws Exception {
        FilterChain filterChain = createFilterChain();

        List<String> compoundTermSource = readCompoundTermDictionary().readLines();
        List<List<String>> compoundTermList = Lists.transform(compoundTermSource, s -> Arrays.asList(s.split("\\s+")));

        Set<List<String>> compoundTerms = ImmutableSet.copyOf(compoundTermList);
        CompoundTermsExtractor termsExtractor = new CompoundTermsExtractor(compoundTerms);

        return new Parser(filterChain, termsExtractor);
    }

    public static NgramExtractor createNGramExtractor() throws IOException {
        FilterChain filterChain = createFilterChain();
        return new NgramExtractor(filterChain);
    }

    public static FilterChain createFilterChain() throws IOException {
        CharSource stopWordsSource = stopWordsCharSource();
        Set<String> stopWords = ImmutableSet.copyOf(stopWordsSource.readLines());
        return new FilterChain(stopWords);
    }

    private static CharSource stopWordsCharSource() {
        CharSource enStopwords = resourceToCharSource("stopwords-en.txt");
        return CharSource.concat(enStopwords);
    }

    private static CharSource resourceToCharSource(String path) {
        final URL resource = Resources.getResource(path);
        return Resources.asCharSource(resource, Charsets.UTF_8);
    }

    private static CharSource readCompoundTermDictionary() throws IOException {
        CharSource questions = resourceToCharSource("compound-terms-questions.txt");
        CharSource glossary = resourceToCharSource("compound-terms-glossary.txt");
        CharSource nounPhrases = resourceToCharSource("compound-terms-noun-phrases.txt");
        return CharSource.concat(questions, glossary, nounPhrases);
    }

}
