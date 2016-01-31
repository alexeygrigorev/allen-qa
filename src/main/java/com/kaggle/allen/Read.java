package com.kaggle.allen;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.Validate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.kaggle.allen.Question.QuestionType;

public class Read {
    public static Iterator<Question> trainingData() throws IOException {
        Map<String, Integer> letterToIndex = ImmutableMap.of("a", 0, "b", 1, "c", 2, "d", 3);
        LineIterator li = FileUtils.lineIterator(new File("/home/agrigorev/Downloads/allen/training_set.tsv"));
        li.next(); // header

        return Iterators.transform(li, line -> {
            String[] split = line.toLowerCase().split("\t");

            String questionId = split[0];
            String content = split[1];

            boolean isFillingGapQuestion = content.contains("______");
            QuestionType type = isFillingGapQuestion ? QuestionType.FILLING_GAP : QuestionType.USUAL;

            int correctAnswer = letterToIndex.get(split[2]);

            List<String> answers = Arrays.asList(split).subList(3, split.length);
            Validate.isTrue(answers.size() == 4);
            return new Question(questionId, type, content, answers, correctAnswer);
        });
    }

    public static Iterator<Question> validationData() throws IOException {
        LineIterator li = FileUtils.lineIterator(new File("/home/agrigorev/Downloads/allen/validaition_set.tsv"));
        li.next(); // header

        return Iterators.transform(li, line -> {
            String[] split = line.split("\t");

            String questionId = split[0];
            String content = split[1];

            boolean isFillingGapQuestion = content.contains("______");
            QuestionType type = isFillingGapQuestion ? QuestionType.FILLING_GAP : QuestionType.USUAL;

            List<String> answers = Arrays.asList(split).subList(2, split.length);
            Validate.isTrue(answers.size() == 4);
            return new Question(questionId, type, content, answers);
        });
    }

}
