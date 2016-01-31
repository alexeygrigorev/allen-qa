package com.kaggle.allen.w2v;

import java.io.File;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.kaggle.allen.text.Parser;
import com.kaggle.allen.text.ParserFactory;

import weka.core.matrix.DoubleVector;

public class FeatureComputeRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeatureComputeRunner.class);

    public static void main(String[] args) throws Exception {
        Map<String, Integer> letterToIndex = ImmutableMap.of("a", 0, "b", 1, "c", 2, "d", 3);
        Map<Integer, String> indexToLetter = ImmutableMap.of(0, "A", 1, "B", 2, "C", 3, "D");

        Parser parser = ParserFactory.createParser();

        Word2Vec word2vec = Word2Vec.load();

        LineIterator li = FileUtils.lineIterator(new File("/home/agrigorev/Downloads/allen/training_set.tsv"));
        li.next(); // header

        PrintWriter features = new PrintWriter(new File("features.txt"));
        features.print("question_id");
        features.print("\t");
        features.print("answer");
        features.print("\t");
        features.print("question_lemmas");
        features.print("\t");
        features.print("question_vec");
        features.print("\t");
        features.print("answer_lemmas");
        features.print("\t");
        features.print("answer_vec");
        features.print("\t");
        features.print("question_type");
        features.print("\t");
        features.print("question_answer_cos_sim");
        features.print("\t");
        features.print("correct");
        features.println();

        while (li.hasNext()) {
            String line = li.next().toLowerCase();

            String[] split = line.split("\t");

            String questionId = split[0];
            String content = split[1];
            List<String> question = parser.parse(content);

            boolean isFillingGapQuestion = content.contains("______");
            String type = isFillingGapQuestion ? "FILLING_GAP" : "USUAL";

            DoubleVector questionVec = normalize(word2vec.vector(String.join(" ", question)));

            int correctAnswer = letterToIndex.get(split[2]);

            List<String> answers = Arrays.asList(split).subList(3, split.length);
            Validate.isTrue(answers.size() == 4);

            List<String> qas = Lists.newArrayList();

            boolean full = true;
            for (int i = 0; i < 4; i++) {
                StringBuilder row = new StringBuilder();

                String answer = answers.get(i);
                List<String> tokens = parser.parseNoCompoundTerms(answer);
                // if (answer.equals("all of the above")) {
                // tokens = Collections.singletonList("all of the above");
                // } else if (answer.equals("none of the above")) {
                // tokens = Collections.singletonList("none of the above");
                // } else {
                // tokens = parser.parseNoCompoundTerms(answer);
                // }

                if (tokens.isEmpty()) {
                    LOGGER.info("got empty vector for {}, question id {}", answer, questionId);
                    full = false;
                    break;
                }

                DoubleVector answerVec = normalize(word2vec.vector(String.join(" ", tokens)));
                if (Double.isNaN(answerVec.get(0))) {
                    LOGGER.info("don't have w2v data for answer {}, question id {}", answer, questionId);
                    full = false;
                    break;
                }

                row.append(questionId);
                row.append("\t");
                row.append(indexToLetter.get(i));
                row.append("\t");
                row.append(String.join(",", question));
                row.append("\t");
                row.append(vecToStr(questionVec));
                row.append("\t");

                row.append(String.join(",", tokens));
                row.append("\t");
                row.append(vecToStr(answerVec));
                row.append("\t");

                row.append(type);
                row.append("\t");

                double similarity = questionVec.innerProduct(answerVec);
                row.append(String.format("%.5f", similarity));
                row.append("\t");

                String label = i == correctAnswer ? "CORRECT" : "NOT_CORRECT";
                row.append(label);

                qas.add(row.toString());
            }

            if (full) {
                for (String s : qas) {
                    features.println(s);
                }
            }
        }

        features.flush();
        features.close();

        li = FileUtils.lineIterator(new File("/home/agrigorev/Downloads/allen/validation_set.tsv"));
        li.next(); // header

        features = new PrintWriter(new File("validation_features.txt"));
        features.print("question_id");
        features.print("\t");
        features.print("answer");
        features.print("\t");
        features.print("question_lemmas");
        features.print("\t");
        features.print("question_vec");
        features.print("\t");
        features.print("answer_lemmas");
        features.print("\t");
        features.print("answer_vec");
        features.print("\t");
        features.print("question_type");
        features.print("\t");
        features.print("question_answer_cos_sim");
        features.println();

        while (li.hasNext()) {
            String line = li.next().toLowerCase();

            String[] split = line.split("\t");

            String questionId = split[0];
            String content = split[1];
            List<String> question = parser.parse(content);

            boolean isFillingGapQuestion = content.contains("______");
            String type = isFillingGapQuestion ? "FILLING_GAP" : "USUAL";

            DoubleVector questionVec = normalize(word2vec.vector(String.join(" ", question)));

            List<String> answers = Arrays.asList(split).subList(2, split.length);
            Validate.isTrue(answers.size() == 4);

            for (int i = 0; i < 4; i++) {
                StringBuilder row = new StringBuilder();

                String answer = answers.get(i);
                List<String> tokens = parser.parseNoCompoundTerms(answer);

                if (tokens.isEmpty()) {
                    LOGGER.info("got empty vector for {}, question id {}", answer, questionId);
                }

                DoubleVector answerVec = normalize(word2vec.vector(String.join(" ", tokens)));
                if (Double.isNaN(answerVec.get(0))) {
                    LOGGER.info("don't have w2v data for answer {}, question id {}", answer, questionId);
                }

                row.append(questionId);
                row.append("\t");
                row.append(indexToLetter.get(i));
                row.append("\t");
                row.append(String.join(",", question));
                row.append("\t");
                row.append(vecToStr(questionVec));
                row.append("\t");

                row.append(String.join(",", tokens));
                row.append("\t");
                row.append(vecToStr(answerVec));
                row.append("\t");

                row.append(type);
                row.append("\t");

                double similarity = questionVec.innerProduct(answerVec);
                row.append(String.format("%.5f", similarity));

                features.println(row.toString());
            }
        }

        features.flush();
        features.close();
    }

    private static DoubleVector normalize(DoubleVector vector) {
        double norm2 = vector.norm2();
        for (int i = 0; i < vector.size(); i++) {
            vector.set(i, vector.get(i) / norm2);
        }
        return vector;
    }

    private static String vecToStr(DoubleVector questionVec) {
        DecimalFormat format = new DecimalFormat("#.#####");
        return Arrays.stream(questionVec.getArray()).mapToObj(format::format).collect(Collectors.joining(","));
    }

}
