package com.kaggle.allen.text;

import java.util.List;

public class Sentence {
    
    public static enum SentenceSource {
        TITLE, H1, H2, H3, BODY, NO_SOURCE;
    }

    private final int sentenceId;
    private final SentenceSource source;
    private final List<String> originalSentence;
    private final List<String> cleanedSentence;

    public Sentence(int sentenceId, SentenceSource source, List<String> originalSentence, List<String> cleanedSentence) {
        this.sentenceId = sentenceId;
        this.source = source;
        this.originalSentence = originalSentence;
        this.cleanedSentence = cleanedSentence;
    }

    public int getSentenceId() {
        return sentenceId;
    }

    public List<String> getCleanedSentence() {
        return cleanedSentence;
    }

    public List<String> getOriginalSentence() {
        return originalSentence;
    }

    public SentenceSource getSource() {
        return source;
    }
}