package com.kaggle.allen;

public class LuceneFeatures {

    private int[] qDocs;
    private double[] qScores;
    private int[] aDocs;
    private double[] aScores;
    private int[] bothQADoc;
    private double[] bothQAScores;
    private int bothQADocCount;
    private int[] bothQADocAMustHave;
    private double[] bothQAScoresMustHave;
    private int bothQADocCountMustHave;

    public LuceneFeatures() {
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

    public int[] getqDocs() {
        return qDocs;
    }

    public void setqDocs(int[] qDocs) {
        this.qDocs = qDocs;
    }

    public double[] getqScores() {
        return qScores;
    }

    public void setqScores(double[] qScores) {
        this.qScores = qScores;
    }

    public int[] getaDocs() {
        return aDocs;
    }

    public void setaDocs(int[] aDocs) {
        this.aDocs = aDocs;
    }

    public double[] getaScores() {
        return aScores;
    }

    public void setaScores(double[] aScores) {
        this.aScores = aScores;
    }

}
