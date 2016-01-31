package com.kaggle.allen.text;

import com.google.common.base.CharMatcher;

public class Filters {

    public static boolean startsWithLetterOrDigit(String termAtt) {
        if (termAtt.length() == 0) {
            return false;
        }

        final char first = termAtt.charAt(0);
        if (Character.isDigit(first)) {
            return true;
        }

        if (Character.isLetter(first)) {
            return true;
        }

        return false;
    }

    /**
     * @param term
     * @return
     */
    public static String removeApostrophies(String termAtt) {
        final char[] buffer = termAtt.toCharArray();
        int length = termAtt.length();

        for (int i = 0; i < length; i++) {
            if (buffer[i] == '\'' || buffer[i] == '\u2019') {
                length = i;
            }
        }

        return termAtt.substring(0, length);
    }
    
    private static final CharMatcher PUNCTUATION = CharMatcher.JAVA_LETTER_OR_DIGIT.or(CharMatcher.WHITESPACE).negate();

    public static String removePunctuationAtTheEnd(String term) {
        return PUNCTUATION.trimTrailingFrom(term);
    }

    public static boolean lengthFilter(String termAttr) {
        return termAttr.length() >= 2 && termAttr.length() <= 50;
    }

}