package com.kaggle.allen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Features {

    private String questionId;
    private String answerLetter;

    private String rawQuestion;
    private String rawAnswer;

    private List<String> question;
    private List<String> answer;
    private List<String> ngramsQuestion;
    private List<String> ngramsAnswer;

    private Word2VecFeatures word2VecFeatures;

    private Map<String, LuceneFeatures> luceneFeatures = new HashMap<>();
    private String type;
    private String source;
    private String label;

    public void addLuceneFeature(String source, LuceneFeatures features) {
        luceneFeatures.put(source, features);
    }

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

    public Word2VecFeatures getWord2VecFeatures() {
        return word2VecFeatures;
    }

    public void setWord2VecFeatures(Word2VecFeatures word2VecFeatures) {
        this.word2VecFeatures = word2VecFeatures;
    }

    public Map<String, LuceneFeatures> getLuceneFeatures() {
        return luceneFeatures;
    }

    public void setLuceneFeatures(Map<String, LuceneFeatures> luceneFeatures) {
        this.luceneFeatures = luceneFeatures;
    }
}
