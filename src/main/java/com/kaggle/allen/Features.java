package com.kaggle.allen;

import java.util.List;

import com.kaggle.allen.lucene.LuceneFeatures.ScoredDocument;

public class Features {

    private String questionId;
    private String answerLetter;

    private String rawQuestion;
    private String rawAnswer;

    private List<String> question;
    private List<String> answer;
    private List<String> ngramsQuestion;
    private List<String> ngramsAnswer;

    private double[] word2vecQuestion;
    private double[] word2vecAnswer;
    private double word2vecCosine;
    private boolean word2vecMissing = false;

    private List<ScoredDocument> ck12WikiQuestionResult;
    private List<ScoredDocument> ck12WikiAnswerResult;
    private int[] ck12WikiBothQADoc;
    private double[] ck12WikiBothQAScores;
    private int ck12WikiBothQADocCount;
    private int[] ck12WikiBothQADocAMustHave;
    private double[] ck12WikiBothQAScoresMustHave;
    private int ck12WikiBothQADocCountAMustHave;

    private List<ScoredDocument> ck12EbookQuestionResult;
    private List<ScoredDocument> ck12EbookAnswerResult;
    private int[] ck12EbookBothQADoc;
    private double[] ck12EbookBothQAScores;
    private int ck12EbookBothQADocCount;
    private int[] ck12EbookBothQADocAMustHave;
    private double[] ck12EbookBothQAScoresMustHave;
    private int ck12EbookBothQADocCountAMustHave;

    private List<ScoredDocument> ngramsCk12WikiQuestionResult;
    private List<ScoredDocument> ngramsCk12WikiAnswerResult;
    private int[] ngramsCk12WikiBothQADoc;
    private double[] ngramsCk12WikiBothQAScores;
    private int ngramsCk12WikiBothQADocCount;
    private int[] ngramsCk12WikiBothQADocMustHave;
    private double[] ngramsCk12WikiBothQAScoresMustHave;
    private int ngramsCk12WikiBothQADocCountAMustHave;

    private List<ScoredDocument> ngramsCk12EbookQuestionResult;
    private List<ScoredDocument> ngramsCk12EbookAnswerResult;
    private int[] ngramsCk12EbookBothQADoc;
    private double[] ngramsCk12EbookBothQAScores;
    private int ngramsCk12EbookBothQADocCount;
    private int[] ngramsCk12EbookBothQADocMustHave;
    private double[] ngramsCk12EbookBothQAScoresMustHave;
    private int ngramsCk12EbookBothQADocCountAMustHave;

    private String type;
    private String source;
    private String label;

    public String getRawQuestion() {
        return rawQuestion;
    }

    public void setRawQuestion(String rawQuestion) {
        this.rawQuestion = rawQuestion;
    }

    public String getRawAnswer() {
        return rawAnswer;
    }

    public void setRawAnswer(String rawAnswer) {
        this.rawAnswer = rawAnswer;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAnswerLetter() {
        return answerLetter;
    }

    public void setAnswerLetter(String answerLetter) {
        this.answerLetter = answerLetter;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public List<String> getQuestion() {
        return question;
    }

    public void setQuestion(List<String> question) {
        this.question = question;
    }

    public List<String> getAnswer() {
        return answer;
    }

    public void setAnswer(List<String> answer) {
        this.answer = answer;
    }

    public double getWord2vecCosine() {
        return word2vecCosine;
    }

    public void setWord2vecCosine(double word2vecCosine) {
        this.word2vecCosine = word2vecCosine;
    }

    public boolean isWord2vecMissing() {
        return word2vecMissing;
    }

    public void setWord2vecMissing(boolean word2vecMissing) {
        this.word2vecMissing = word2vecMissing;
    }

    public List<ScoredDocument> getCk12WikiQuestionResult() {
        return ck12WikiQuestionResult;
    }

    public void setCk12WikiQuestionResult(List<ScoredDocument> ck12WikiQuestionResult) {
        this.ck12WikiQuestionResult = ck12WikiQuestionResult;
    }

    public List<ScoredDocument> getCk12WikiAnswerResult() {
        return ck12WikiAnswerResult;
    }

    public void setCk12WikiAnswerResult(List<ScoredDocument> ck12WikiAnswerResult) {
        this.ck12WikiAnswerResult = ck12WikiAnswerResult;
    }

    public int getCk12WikiBothQADocCount() {
        return ck12WikiBothQADocCount;
    }

    public void setCk12WikiBothQADocCount(int ck12WikiBothQADocCount) {
        this.ck12WikiBothQADocCount = ck12WikiBothQADocCount;
    }

    public int getCk12WikiBothQADocCountAMustHave() {
        return ck12WikiBothQADocCountAMustHave;
    }

    public void setCk12WikiBothQADocCountAMustHave(int ck12WikiBothQADocCountAMustHave) {
        this.ck12WikiBothQADocCountAMustHave = ck12WikiBothQADocCountAMustHave;
    }

    public List<ScoredDocument> getCk12EbookQuestionResult() {
        return ck12EbookQuestionResult;
    }

    public void setCk12EbookQuestionResult(List<ScoredDocument> ck12EbookQuestionResult) {
        this.ck12EbookQuestionResult = ck12EbookQuestionResult;
    }

    public List<ScoredDocument> getCk12EbookAnswerResult() {
        return ck12EbookAnswerResult;
    }

    public void setCk12EbookAnswerResult(List<ScoredDocument> ck12EbookAnswerResult) {
        this.ck12EbookAnswerResult = ck12EbookAnswerResult;
    }

    public int getCk12EbookBothQADocCount() {
        return ck12EbookBothQADocCount;
    }

    public void setCk12EbookBothQADocCount(int ck12EbookBothQADocCount) {
        this.ck12EbookBothQADocCount = ck12EbookBothQADocCount;
    }

    public int getCk12EbookBothQADocCountAMustHave() {
        return ck12EbookBothQADocCountAMustHave;
    }

    public void setCk12EbookBothQADocCountAMustHave(int ck12EbookBothQADocCountAMustHave) {
        this.ck12EbookBothQADocCountAMustHave = ck12EbookBothQADocCountAMustHave;
    }

    public List<ScoredDocument> getNgramsCk12WikiQuestionResult() {
        return ngramsCk12WikiQuestionResult;
    }

    public void setNgramsCk12WikiQuestionResult(List<ScoredDocument> ngramsCk12WikiQuestionResult) {
        this.ngramsCk12WikiQuestionResult = ngramsCk12WikiQuestionResult;
    }

    public List<ScoredDocument> getNgramsCk12WikiAnswerResult() {
        return ngramsCk12WikiAnswerResult;
    }

    public void setNgramsCk12WikiAnswerResult(List<ScoredDocument> ngramsCk12WikiAnswerResult) {
        this.ngramsCk12WikiAnswerResult = ngramsCk12WikiAnswerResult;
    }

    public int getNgramsCk12WikiBothQADocCount() {
        return ngramsCk12WikiBothQADocCount;
    }

    public void setNgramsCk12WikiBothQADocCount(int ngramsCk12WikiBothQADocCount) {
        this.ngramsCk12WikiBothQADocCount = ngramsCk12WikiBothQADocCount;
    }

    public int getNgramsCk12WikiBothQADocCountAMustHave() {
        return ngramsCk12WikiBothQADocCountAMustHave;
    }

    public void setNgramsCk12WikiBothQADocCountAMustHave(int ngramsCk12WikiBothQADocCountAMustHave) {
        this.ngramsCk12WikiBothQADocCountAMustHave = ngramsCk12WikiBothQADocCountAMustHave;
    }

    public List<ScoredDocument> getNgramsCk12EbookQuestionResult() {
        return ngramsCk12EbookQuestionResult;
    }

    public void setNgramsCk12EbookQuestionResult(List<ScoredDocument> ngramsCk12EbookQuestionResult) {
        this.ngramsCk12EbookQuestionResult = ngramsCk12EbookQuestionResult;
    }

    public List<ScoredDocument> getNgramsCk12EbookAnswerResult() {
        return ngramsCk12EbookAnswerResult;
    }

    public void setNgramsCk12EbookAnswerResult(List<ScoredDocument> ngramsCk12EbookAnswerResult) {
        this.ngramsCk12EbookAnswerResult = ngramsCk12EbookAnswerResult;
    }

    public int getNgramsCk12EbookBothQADocCount() {
        return ngramsCk12EbookBothQADocCount;
    }

    public void setNgramsCk12EbookBothQADocCount(int ngramsCk12EbookBothQADocCount) {
        this.ngramsCk12EbookBothQADocCount = ngramsCk12EbookBothQADocCount;
    }

    public int getNgramsCk12EbookBothQADocCountAMustHave() {
        return ngramsCk12EbookBothQADocCountAMustHave;
    }

    public void setNgramsCk12EbookBothQADocCountAMustHave(int ngramsCk12EbookBothQADocCountAMustHave) {
        this.ngramsCk12EbookBothQADocCountAMustHave = ngramsCk12EbookBothQADocCountAMustHave;
    }

    public List<String> getNgramsQuestion() {
        return ngramsQuestion;
    }

    public void setNgramsQuestion(List<String> ngramsQuestion) {
        this.ngramsQuestion = ngramsQuestion;
    }

    public List<String> getNgramsAnswer() {
        return ngramsAnswer;
    }

    public void setNgramsAnswer(List<String> ngramsAnswer) {
        this.ngramsAnswer = ngramsAnswer;
    }

    public int[] getCk12WikiBothQADoc() {
        return ck12WikiBothQADoc;
    }

    public void setCk12WikiBothQADoc(int[] ck12WikiBothQADoc) {
        this.ck12WikiBothQADoc = ck12WikiBothQADoc;
    }

    public int[] getCk12WikiBothQADocAMustHave() {
        return ck12WikiBothQADocAMustHave;
    }

    public void setCk12WikiBothQADocAMustHave(int[] ck12WikiBothQADocAMustHave) {
        this.ck12WikiBothQADocAMustHave = ck12WikiBothQADocAMustHave;
    }

    public int[] getCk12EbookBothQADoc() {
        return ck12EbookBothQADoc;
    }

    public void setCk12EbookBothQADoc(int[] ck12EbookBothQADoc) {
        this.ck12EbookBothQADoc = ck12EbookBothQADoc;
    }

    public int[] getCk12EbookBothQADocAMustHave() {
        return ck12EbookBothQADocAMustHave;
    }

    public void setCk12EbookBothQADocAMustHave(int[] ck12EbookBothQADocAMustHave) {
        this.ck12EbookBothQADocAMustHave = ck12EbookBothQADocAMustHave;
    }

    public int[] getNgramsCk12WikiBothQADoc() {
        return ngramsCk12WikiBothQADoc;
    }

    public void setNgramsCk12WikiBothQADoc(int[] ngramsCk12WikiBothQADoc) {
        this.ngramsCk12WikiBothQADoc = ngramsCk12WikiBothQADoc;
    }

    public int[] getNgramsCk12WikiBothQADocMustHave() {
        return ngramsCk12WikiBothQADocMustHave;
    }

    public void setNgramsCk12WikiBothQADocMustHave(int[] ngramsCk12WikiBothQADocMustHave) {
        this.ngramsCk12WikiBothQADocMustHave = ngramsCk12WikiBothQADocMustHave;
    }

    public int[] getNgramsCk12EbookBothQADoc() {
        return ngramsCk12EbookBothQADoc;
    }

    public void setNgramsCk12EbookBothQADoc(int[] ngramsCk12EbookBothQADoc) {
        this.ngramsCk12EbookBothQADoc = ngramsCk12EbookBothQADoc;
    }

    public int[] getNgramsCk12EbookBothQADocMustHave() {
        return ngramsCk12EbookBothQADocMustHave;
    }

    public void setNgramsCk12EbookBothQADocMustHave(int[] ngramsCk12EbookBothQADocMustHave) {
        this.ngramsCk12EbookBothQADocMustHave = ngramsCk12EbookBothQADocMustHave;
    }

    public double[] getCk12WikiBothQAScores() {
        return ck12WikiBothQAScores;
    }

    public void setCk12WikiBothQAScores(double[] ck12WikiBothQAScores) {
        this.ck12WikiBothQAScores = ck12WikiBothQAScores;
    }

    public double[] getCk12WikiBothQAScoresMustHave() {
        return ck12WikiBothQAScoresMustHave;
    }

    public void setCk12WikiBothQAScoresMustHave(double[] ck12WikiBothQAScoresMustHave) {
        this.ck12WikiBothQAScoresMustHave = ck12WikiBothQAScoresMustHave;
    }

    public double[] getCk12EbookBothQAScores() {
        return ck12EbookBothQAScores;
    }

    public void setCk12EbookBothQAScores(double[] ck12EbookBothQAScores) {
        this.ck12EbookBothQAScores = ck12EbookBothQAScores;
    }

    public double[] getCk12EbookBothQAScoresMustHave() {
        return ck12EbookBothQAScoresMustHave;
    }

    public void setCk12EbookBothQAScoresMustHave(double[] ck12EbookBothQAScoresMustHave) {
        this.ck12EbookBothQAScoresMustHave = ck12EbookBothQAScoresMustHave;
    }

    public double[] getNgramsCk12WikiBothQAScores() {
        return ngramsCk12WikiBothQAScores;
    }

    public void setNgramsCk12WikiBothQAScores(double[] ngramsCk12WikiBothQAScores) {
        this.ngramsCk12WikiBothQAScores = ngramsCk12WikiBothQAScores;
    }

    public double[] getNgramsCk12WikiBothQAScoresMustHave() {
        return ngramsCk12WikiBothQAScoresMustHave;
    }

    public void setNgramsCk12WikiBothQAScoresMustHave(double[] ngramsCk12WikiBothQAScoresMustHave) {
        this.ngramsCk12WikiBothQAScoresMustHave = ngramsCk12WikiBothQAScoresMustHave;
    }

    public double[] getNgramsCk12EbookBothQAScores() {
        return ngramsCk12EbookBothQAScores;
    }

    public void setNgramsCk12EbookBothQAScores(double[] ngramsCk12EbookBothQAScores) {
        this.ngramsCk12EbookBothQAScores = ngramsCk12EbookBothQAScores;
    }

    public double[] getNgramsCk12EbookBothQAScoresMustHave() {
        return ngramsCk12EbookBothQAScoresMustHave;
    }

    public void setNgramsCk12EbookBothQAScoresMustHave(double[] ngramsCk12EbookBothQAScoresMustHave) {
        this.ngramsCk12EbookBothQAScoresMustHave = ngramsCk12EbookBothQAScoresMustHave;
    }

}
