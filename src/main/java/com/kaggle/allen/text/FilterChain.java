package com.kaggle.allen.text;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FilterChain {

    private final Set<String> stopWords;

    public FilterChain(Set<String> stopWords) {
        this.stopWords = stopWords;
    }

    public List<String> process(List<String> tokens) {
        List<String> result = tokens.stream()
                .filter(Filters::startsWithLetterOrDigit)
                .map(String::toLowerCase)
                .map(Filters::removeApostrophies)
                .map(Filters::removePunctuationAtTheEnd)
                .filter(Filters::lengthFilter)
                .filter(s -> !stopWords.contains(s))
                .collect(Collectors.toList());
        return result;
    }

}

