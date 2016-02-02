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
    private double ck12WikiSpearmanCorr;
    private double ck12WikiKendallTauCorr;
    private int ck12WikiBothQADocCount;
    private int ck12WikiBothQADocCountAMustHave;

    private List<ScoredDocument> ck12EbookQuestionResult;
    private List<ScoredDocument> ck12EbookAnswerResult;
    private double ck12EbookSpearmanCorr;
    private double ck12EbookKendallTauCorr;
    private int ck12EbookBothQADocCount;
    private int ck12EbookBothQADocCountAMustHave;

    private List<ScoredDocument> ngramsCk12WikiQuestionResult;
    private List<ScoredDocument> ngramsCk12WikiAnswerResult;
    private double ngramsCk12WikiSpearmanCorr;
    private double ngramsCk12WikiKendallTauCorr;
    private int ngramsCk12WikiBothQADocCount;
    private int ngramsCk12WikiBothQADocCountAMustHave;

    private List<ScoredDocument> ngramsCk12EbookQuestionResult;
    private List<ScoredDocument> ngramsCk12EbookAnswerResult;
    private double ngramsCk12EbookSpearmanCorr;
    private double ngramsCk12EbookKendallTauCorr;
    private int ngramsCk12EbookBothQADocCount;
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

    public double getCk12WikiSpearmanCorr() {
        return ck12WikiSpearmanCorr;
    }

    public void setCk12WikiSpearmanCorr(double ck12WikiSpearmanCorr) {
        this.ck12WikiSpearmanCorr = ck12WikiSpearmanCorr;
    }

    public double getCk12WikiKendallTauCorr() {
        return ck12WikiKendallTauCorr;
    }

    public void setCk12WikiKendallTauCorr(double ck12WikiKendallTauCorr) {
        this.ck12WikiKendallTauCorr = ck12WikiKendallTauCorr;
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

    public double getCk12EbookSpearmanCorr() {
        return ck12EbookSpearmanCorr;
    }

    public void setCk12EbookSpearmanCorr(double ck12EbookSpearmanCorr) {
        this.ck12EbookSpearmanCorr = ck12EbookSpearmanCorr;
    }

    public double getCk12EbookKendallTauCorr() {
        return ck12EbookKendallTauCorr;
    }

    public void setCk12EbookKendallTauCorr(double ck12EbookKendallTauCorr) {
        this.ck12EbookKendallTauCorr = ck12EbookKendallTauCorr;
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

    public double getNgramsCk12WikiSpearmanCorr() {
        return ngramsCk12WikiSpearmanCorr;
    }

    public void setNgramsCk12WikiSpearmanCorr(double ngramsCk12WikiSpearmanCorr) {
        this.ngramsCk12WikiSpearmanCorr = ngramsCk12WikiSpearmanCorr;
    }

    public double getNgramsCk12WikiKendallTauCorr() {
        return ngramsCk12WikiKendallTauCorr;
    }

    public void setNgramsCk12WikiKendallTauCorr(double ngramsCk12WikiKendallTauCorr) {
        this.ngramsCk12WikiKendallTauCorr = ngramsCk12WikiKendallTauCorr;
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

    public double getNgramsCk12EbookSpearmanCorr() {
        return ngramsCk12EbookSpearmanCorr;
    }

    public void setNgramsCk12EbookSpearmanCorr(double ngramsCk12EbookSpearmanCorr) {
        this.ngramsCk12EbookSpearmanCorr = ngramsCk12EbookSpearmanCorr;
    }

    public double getNgramsCk12EbookKendallTauCorr() {
        return ngramsCk12EbookKendallTauCorr;
    }

    public void setNgramsCk12EbookKendallTauCorr(double ngramsCk12EbookKendallTauCorr) {
        this.ngramsCk12EbookKendallTauCorr = ngramsCk12EbookKendallTauCorr;
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

}
