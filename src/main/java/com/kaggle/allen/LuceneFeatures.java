package com.kaggle.allen;

import java.util.List;

import com.kaggle.allen.lucene.LuceneFeatureExtractor.ScoredDocument;

public class LuceneFeatures {

    private List<ScoredDocument> questionResult;
    private List<ScoredDocument> answerResult;
    private int[] bothQADoc;
    private double[] bothQAScores;
    private int bothQADocCount;
    private int[] bothQADocAMustHave;
    private double[] bothQAScoresMustHave;
    private int bothQADocCountMustHave;

    public LuceneFeatures() {
    }

    public List<ScoredDocument> getQuestionResult() {
        return questionResult;
    }

    public void setQuestionResult(List<ScoredDocument> questionResult) {
        this.questionResult = questionResult;
    }

    public List<ScoredDocument> getAnswerResult() {
        return answerResult;
    }

    public void setAnswerResult(List<ScoredDocument> answerResult) {
        this.answerResult = answerResult;
    }

    public int[] getBothQADoc() {
        return bothQADoc;
    }

    public void setBothQADoc(int[] bothQADoc) {
        this.bothQADoc = bothQADoc;
    }

    public double[] getBothQAScores() {
        return bothQAScores;
    }

    public void setBothQAScores(double[] bothQAScores) {
        this.bothQAScores = bothQAScores;
    }

    public int getBothQADocCount() {
        return bothQADocCount;
    }

    public void setBothQADocCount(int bothQADocCount) {
        this.bothQADocCount = bothQADocCount;
    }

    public int[] getBothQADocAMustHave() {
        return bothQADocAMustHave;
    }

    public void setBothQADocAMustHave(int[] bothQADocAMustHave) {
        this.bothQADocAMustHave = bothQADocAMustHave;
    }

    public double[] getBothQAScoresMustHave() {
        return bothQAScoresMustHave;
    }

    public void setBothQAScoresMustHave(double[] bothQAScoresMustHave) {
        this.bothQAScoresMustHave = bothQAScoresMustHave;
    }

    public int getBothQADocCountMustHave() {
        return bothQADocCountMustHave;
    }

    public void setBothQADocCountMustHave(int bothQADocCountMustHave) {
        this.bothQADocCountMustHave = bothQADocCountMustHave;
    }

}
