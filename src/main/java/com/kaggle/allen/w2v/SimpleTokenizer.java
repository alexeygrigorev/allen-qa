/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kaggle.allen.w2v;

/**
 *
 * @author kailondenberg
 */
public interface SimpleTokenizer {

    public String normalize(String text) throws Exception;

    public String[] tokenize(String normalizedText) throws Exception;
}
