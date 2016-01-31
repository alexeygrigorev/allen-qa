package com.kaggle.allen.w2v;

import java.io.File;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;
import com.searchmetrics.featureextraction.WordSet;
import com.searchmetrics.featureextraction.WordSet.Similarity;
import com.searchmetrics.featureextraction.WordSet.Word2VecCosineSimilarity;
import com.searchmetrics.word2vec.Word2VecDict;

import weka.core.matrix.DoubleVector;

public class Word2Vec {

    private static final Logger LOGGER = LoggerFactory.getLogger(Word2Vec.class);

    private Word2VecDict dict;
    private Similarity similarity;

    public static Word2Vec load() throws Exception {
        Word2VecDict dict = loadDict();
        Word2VecCosineSimilarity sim = new Word2VecCosineSimilarity(dict);
        return new Word2Vec(dict, sim);
    }

    public Word2Vec(Word2VecDict dict, Similarity similarity) {
        this.dict = dict;
        this.similarity = similarity;
    }

    public DoubleVector vector(String text) {
        WordSet set = wordSet(text);
        DoubleVector repr[] = new DoubleVector[set.getWords().length];
        int i = 0;
        DoubleVector sum = new DoubleVector(dict.getDimensions());
        for (String w : set.getWords()) {
            repr[i] = dict.getVector(w.toLowerCase());
            if (repr[i] != null) {
                sum.plusEquals(repr[i]);
            }
            i++;
        }

        set.setWord2VecRepresentation(repr);
        double n2 = sum.norm2();
        if (n2 != 0.0) {
            set.setWord2VecNormalizedSumVector(sum.times(1.0 / n2));
        } else {
            set.setWord2VecNormalizedSumVector(sum);
        }

        return sum;
    }

    public double similarity(String text1, String text2) {
        double result = similarity.getSimilarity(wordSet(text1), wordSet(text2));
        return Math.max(result, 0.0);
    }

    private WordSet wordSet(String word) {
        try {
            return new WordSet(word, " ");
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    private static Word2VecDict loadDict() throws Exception {
        PropertiesConfiguration config = loadConfig();
        String path = config.getString("file.word2vec.en");
        LOGGER.info("loading a word2vec model from {}", path);
        return new Word2VecDict(path);
    }

    private static PropertiesConfiguration loadConfig() throws Exception {
        File confFile = new File("content-scores.properties");
        if (confFile.exists()) {
            return new PropertiesConfiguration(confFile);
        }

        File confFile2 = new File("/etc/content-scores.properties");
        if (confFile2.exists()) {
            return new PropertiesConfiguration(confFile2);
        }

        throw new AssertionError("the config for content scores does not exist");
    }

}
