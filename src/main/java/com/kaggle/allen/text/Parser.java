package com.kaggle.allen.text;

import java.util.List;

/**
 * 
 * @author agrigorev
 *
 */
public class Parser {

    private final CompoundTermsExtractor compoundTermsExtractor;
    private final FilterChain filterChain;

    private final Tokenizer tokenizer = new Tokenizer();

    public Parser(FilterChain filterChain, CompoundTermsExtractor termsExtractor) {
        this.compoundTermsExtractor = termsExtractor;
        this.filterChain = filterChain;
    }

    public List<String> parse(String content) {
        List<String> tokenized = tokenizer.lemmas(content);
        List<String> sentenceWithCompoundTerms = compoundTermsExtractor.findCompoundTerms(tokenized);
        return filterChain.process(sentenceWithCompoundTerms);
    }

    public List<String> parseNoCompoundTerms(String content) {
        List<String> tokenized = tokenizer.lemmas(content);
        return filterChain.process(tokenized);
    }

}
