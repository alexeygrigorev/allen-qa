/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kaggle.allen.w2v;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.apache.commons.collections4.trie.PatriciaTrie;

import com.searchmetrics.transformation.EnglishAnalyzer;

/**
 *
 * @author kailondenberg
 */
public class CachingENTokenizer implements SimpleTokenizer {

    protected final EnglishAnalyzer analyzer;
    protected final DefaultSimpleTokenizer dftok = new DefaultSimpleTokenizer();
    final PatriciaTrie<String> tokenCache;
    final Pattern spaceSplitter;
    final Pattern invalidReplacer;

    public CachingENTokenizer() throws Exception {
        this.analyzer = new EnglishAnalyzer();
        tokenCache = new PatriciaTrie<>();
        spaceSplitter = Pattern.compile(" ");
        invalidReplacer = Pattern.compile("[^\\x08-\\xFF]");
    }

    public String[] tokenize(String normalizedText) {
        return spaceSplitter.split(normalizedText);
    }

    public String normalize(String text) throws Exception {
        String norm1 = dftok.normalize(text);
        String parts1[] = dftok.tokenize(norm1);
        StringBuilder res = new StringBuilder();
        int tc = 0;
        for (String p : parts1) {
            String np = normedtoken(p);
            if (np.length() > 0) {
                if (tc++ > 0) {
                    res.append(' ');
                }
                res.append(np);
            }
        }
        return res.toString();
    }

    private final String normedtoken(String tok) throws Exception {

        String res = tokenCache.get(tok);
        if (res == null) {
            res = analyzer.normalize(tok);
            if (res == null) {
                res = "";
            }
            synchronized (tokenCache) {
                tokenCache.put(tok, res);
            }
        }
        return res;
    }

    public static void main(String args[]) {
        try {
            SimpleTokenizer st = new ENTokenizer();
            SimpleTokenizer stf = new CachingENTokenizer();
            String input = "Hey, where are you going ? The President of the United States of America wants you, to become his father.";
            String out1 = st.normalize(input);
            String out2 = stf.normalize(input);
            long start1 = System.currentTimeMillis();
            for (int i=0;i<1000;i++) {
                st.normalize(input);
            }
            long start2 = System.currentTimeMillis();
            for (int i=0;i<1000;i++) {
                stf.normalize(input);
            }
            long stop = System.currentTimeMillis();
            assert (out1.equals(out2));
            System.out.println("Old one took "+(start2-start1) +" ms, new one took "+(stop-start2)+" ms");

        } catch (Exception ex) {
            Logger.getLogger(ENTokenizer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
