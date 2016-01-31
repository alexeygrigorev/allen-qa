package com.kaggle.allen.lucene;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.util.CharTokenizer;
import org.apache.lucene.util.Version;

public class LinebreakAnalyzer extends Analyzer {

    private final Version version;

    public LinebreakAnalyzer(Version version) {
        this.version = version;
    }

    @Override
    protected TokenStreamComponents createComponents(final String fieldName, final Reader reader) {
        LinebreakTokenizer tokenizer = new LinebreakTokenizer(version, reader);
//        FilteringTokenFilter debug = new FilteringTokenFilter(version, tokenizer) {
//            private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
//            @Override
//            protected boolean accept() throws IOException {
//                System.out.println(termAtt);
//                return true;
//            }
//        };
//
//        return new TokenStreamComponents(tokenizer, debug);
        return new TokenStreamComponents(tokenizer);
    }

    private static class LinebreakTokenizer extends CharTokenizer {
        public LinebreakTokenizer(Version matchVersion, Reader in) {
            super(matchVersion, in);
        }

        @Override
        protected boolean isTokenChar(int c) {
            return c != '\n';
        }
    }
}
