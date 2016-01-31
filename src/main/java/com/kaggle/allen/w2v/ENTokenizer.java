/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kaggle.allen.w2v;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.searchmetrics.transformation.EnglishAnalyzer;

/**
 *
 * @author kailondenberg
 */
public class ENTokenizer implements SimpleTokenizer {
    protected final EnglishAnalyzer analyzer;
    protected final DefaultSimpleTokenizer dftok = new DefaultSimpleTokenizer();

    public ENTokenizer() throws Exception {
        this.analyzer = new EnglishAnalyzer();
    }

    @Override
    public String normalize(String text) throws Exception {
        String txt = text.replaceAll("[^\\x08-\\xFF]", "?");
        return dftok.normalize(analyzer.normalize(txt));
    }

    @Override
    public String[] tokenize(String normalizedText) {
        return dftok.tokenize(normalizedText);
    }

    public static void main(String args[]) {
        try {
            SimpleTokenizer st = new ENTokenizer();
            String input = "Hey, where are you going ? The President of the United States of America wants you, to become his father.";

            System.out.println(st.normalize("are you going"));
            System.out.println(st.normalize(input));
        } catch (Exception ex) {
            Logger.getLogger(ENTokenizer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
