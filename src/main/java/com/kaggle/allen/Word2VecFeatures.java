package com.kaggle.allen;

public class Word2VecFeatures {

    private double[] word2vecQuestion;
    private double[] word2vecAnswer;
    private double word2vecCosine;

    public double[] getWord2vecQuestion() {
        return word2vecQuestion;
    }

    public void setWord2vecQuestion(double[] word2vecQuestion) {
        this.word2vecQuestion = word2vecQuestion;
    }

    public double[] getWord2vecAnswer() {
        return word2vecAnswer;
    }

    public void setWord2vecAnswer(double[] word2vecAnswer) {
        this.word2vecAnswer = word2vecAnswer;
    }

    public double getWord2vecCosine() {
        return word2vecCosine;
    }

    public void setWord2vecCosine(double word2vecCosine) {
        this.word2vecCosine = word2vecCosine;
    }

}
