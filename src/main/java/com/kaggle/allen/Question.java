package com.kaggle.allen;

import java.util.List;

public class Question {

    private static final int NO_ANSWER = -1;

    public static enum QuestionType {
        FILLING_GAP, COMPLETION, USUAL;
    }

    private String questionId;
    private QuestionType type;
    private String content;
    private List<String> answers;
    private int correctAnswer;

    public Question(String questionId, QuestionType type, String content, List<String> answers, int correctAnswer) {
        this.questionId = questionId;
        this.type = type;
        this.content = content;
        this.answers = answers;
        this.correctAnswer = correctAnswer;
    }

    public Question(String questionId, QuestionType type, String content, List<String> answers) {
        this(questionId, type, content, answers, NO_ANSWER);
    }

    public String getQuestionId() {
        return questionId;
    }

    public QuestionType getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

}
