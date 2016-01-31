/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kaggle.allen.w2v;

import java.util.regex.Pattern;

/**
 *
 * @author kailondenberg
 */
public class DefaultSimpleTokenizer implements SimpleTokenizer {

    final Pattern splitter;
    final Pattern replacer;

    public DefaultSimpleTokenizer() {
        splitter = Pattern.compile(" ");
        replacer = Pattern.compile("[^a-zöäüß0-9]+");
    }

    @Override
    public String[] tokenize(String text) {
        return text.split(" ");
    }

    @Override
    public String normalize(String text) {
        return replacer.matcher(text.toLowerCase()).replaceAll(" ");
    }

}
