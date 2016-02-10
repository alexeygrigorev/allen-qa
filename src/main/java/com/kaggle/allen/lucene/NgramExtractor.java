package com.kaggle.allen.lucene;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.kaggle.allen.text.FilterChain;
import com.kaggle.allen.text.Tokenizer;

public class NgramExtractor {

    private final Tokenizer tokenizer = new Tokenizer();
    private FilterChain filterChain;

    public NgramExtractor(FilterChain filterChain) {
        this.filterChain = filterChain;
    }

    public List<String> ngrams(List<String> tokens) {
        Iterable<List<String>> ngrams = Iterables.concat(ngrams(tokens, 2), ngrams(tokens, 3), ngrams(tokens, 4));
        List<String> result = Lists.newArrayList(tokens);
        Iterables.transform(ngrams, l -> String.join(" ", l)).forEach(result::add);
        return result;
    }

    public List<String> parse(String content) {
        List<String> tokens = tokenizer.lemmas(content);
        return filterChain.process(tokens);
    }

    public List<String> ngrams(String content) {
        List<String> tokens = tokenizer.lemmas(content);
        tokens = filterChain.process(tokens);

        Iterable<List<String>> ngrams = Iterables.concat(ngrams(tokens, 2), ngrams(tokens, 3), ngrams(tokens, 4));
        List<String> result = Lists.newArrayList(tokens);

        Iterables.transform(ngrams, l -> String.join(" ", l)).forEach(result::add);

        return result;
    }

    public static <E> List<List<E>> ngrams(List<E> input, int n) {
        int size = input.size();
        if (n > size) {
            return Collections.emptyList();
        }

        List<List<E>> result = Lists.newArrayList();

        for (int i = 0; i < size - n + 1; i++) {
            result.add(input.subList(i, i + n));
        }

        return result;
    }
}
