package com.kaggle.allen;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.math3.stat.correlation.KendallsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.kaggle.allen.lucene.LuceneFeatures;
import com.kaggle.allen.lucene.LuceneFeatures.ScoredDocument;
import com.kaggle.allen.text.Parser;
import com.kaggle.allen.text.ParserFactory;
import com.kaggle.allen.w2v.Word2Vec;

import weka.core.matrix.DoubleVector;

public class CalculateFeatures {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalculateFeatures.class);

    @SuppressWarnings("resource")
    public static void main(String[] args) throws Exception {
        List<Question> allData = Lists.newArrayList(Read.allData());

        Stream<Features> featues = parseAndPivot(allData.stream());
        featues = word2Vec(featues);
        featues = luceneWiki(featues);
        featues = luceneCk12(featues);

        ObjectMapper mapper = new ObjectMapper();

        PrintWriter pw = new PrintWriter("lucene-features.json");
        featues.map(f -> {
            try {
                return mapper.writeValueAsString(f);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }).forEach(pw::println);
        pw.close();
    }

    private static Stream<Features> parseAndPivot(Stream<Question> stream) throws Exception {
        Map<Integer, String> indexToLetter = ImmutableMap.of(0, "A", 1, "B", 2, "C", 3, "D");
        Parser parser = ParserFactory.createParser();

        return stream.flatMap(q -> {
            List<Features> res = Lists.newArrayListWithCapacity(4);

            String question = q.getContent();
            List<String> questionTokens = parser.parse(question);

            List<String> answers = q.getAnswers();

            for (int i = 0; i < answers.size(); i++) {
                Features features = new Features();
                features.setQuestionId(q.getQuestionId());
                features.setRawQuestion(question);
                features.setQuestion(questionTokens);

                String answer = answers.get(i);
                features.setRawAnswer(answer);
                features.setSource(q.getSource());

                List<String> answerTokens = parser.parse(answer);
                features.setAnswer(answerTokens);

                features.setType(q.getType().toString());
                features.setAnswerLetter(indexToLetter.get(i));

                if ("TRAIN".equals(q.getSource())) {
                    features.setLabel(String.valueOf(q.getCorrectAnswer() == i));
                } else {
                    features.setLabel("");
                }

                res.add(features);
            }

            return res.stream();
        });
    }

    private static Stream<Features> word2Vec(Stream<Features> featues) throws Exception {
        Word2Vec word2vec = Word2Vec.load();

        return featues.map(f -> {
            DoubleVector questionVec = normalize(word2vec.vector(String.join(" ", f.getQuestion())));
            f.setWord2vecQuestion(questionVec.getArray());

            DoubleVector answerVec = normalize(word2vec.vector(String.join(" ", f.getAnswer())));
            f.setWord2vecAnswer(answerVec.getArray());

            double cosine = questionVec.innerProduct(answerVec);
            f.setWord2vecCosine(cosine);

            if (Double.isNaN(cosine)) {
                f.setWord2vecMissing(true);
            }

            return f;
        });
    }

    private static DoubleVector normalize(DoubleVector vector) {
        double norm2 = vector.norm2();
        for (int i = 0; i < vector.size(); i++) {
            vector.set(i, vector.get(i) / norm2);
        }
        return vector;
    }

    private static Stream<Features> luceneWiki(Stream<Features> featues) throws IOException {
        IndexSearcher wikiSearcher = LuceneFeatures.searcher(new File("data/index"));

        return featues.map(f -> {
            try {
                Query question = LuceneFeatures.createQuery(f.getQuestion());
                Query answer = LuceneFeatures.createQuery(f.getAnswer());

                List<ScoredDocument> questionDocs = LuceneFeatures.query(wikiSearcher, question, 20);
                f.setCk12WikiQuestionResult(questionDocs);

                List<ScoredDocument> answerDocs = LuceneFeatures.query(wikiSearcher, answer, 20);
                f.setCk12WikiAnswerResult(answerDocs);

                double[] questionRanks = rankingArray(questionDocs);
                double[] answerRanks = rankingArray(answerDocs);

                SpearmansCorrelation spearmansCorrelation = new SpearmansCorrelation();
                double spearman = spearmansCorrelation.correlation(questionRanks, answerRanks);
                f.setCk12WikiSpearmanCorr(spearman);

                KendallsCorrelation kendallsCorrelation = new KendallsCorrelation();
                double kendall = kendallsCorrelation.correlation(questionRanks, answerRanks);
                f.setCk12WikiKendallTauCorr(kendall);

                Query both = LuceneFeatures.createQuery(Iterables.concat(f.getQuestion(), f.getAnswer()));
                ScoreDoc[] bothResults = LuceneFeatures.lightQuery(wikiSearcher, both, Integer.MAX_VALUE);

                f.setCk12WikiBothQADocCount(bothResults.length);

                Query mustHave = LuceneFeatures.createMustHaveQuery(f.getQuestion(), f.getAnswer());
                ScoreDoc[] mustHaveResults = LuceneFeatures.lightQuery(wikiSearcher, mustHave, Integer.MAX_VALUE);

                f.setCk12WikiBothQADocCountAMustHave(mustHaveResults.length);
            } catch (Exception e) {
            }
            return f;
        });

    }

    private static Stream<Features> luceneCk12(Stream<Features> featues) throws IOException {
        IndexSearcher ck12Searcher = LuceneFeatures.searcher(new File("data/ck12-index"));

        return featues.map(f -> {
            try {
                Query question = LuceneFeatures.createQuery(f.getQuestion());
                Query answer = LuceneFeatures.createQuery(f.getAnswer());

                List<ScoredDocument> questionDocs = LuceneFeatures.query(ck12Searcher, question, 20);
                f.setCk12EbookQuestionResult(questionDocs);

                List<ScoredDocument> answerDocs = LuceneFeatures.query(ck12Searcher, answer, 20);
                f.setCk12EbookAnswerResult(answerDocs);

                double[] questionRanks = rankingArray(questionDocs);
                double[] answerRanks = rankingArray(answerDocs);

                SpearmansCorrelation spearmansCorrelation = new SpearmansCorrelation();
                double spearman = spearmansCorrelation.correlation(questionRanks, answerRanks);
                f.setCk12EbookSpearmanCorr(spearman);

                KendallsCorrelation kendallsCorrelation = new KendallsCorrelation();
                double kendall = kendallsCorrelation.correlation(questionRanks, answerRanks);
                f.setCk12EbookKendallTauCorr(kendall);

                Query both = LuceneFeatures.createQuery(Iterables.concat(f.getQuestion(), f.getAnswer()));
                ScoreDoc[] bothResults = LuceneFeatures.lightQuery(ck12Searcher, both, Integer.MAX_VALUE);

                f.setCk12EbookBothQADocCount(bothResults.length);

                Query mustHave = LuceneFeatures.createMustHaveQuery(f.getQuestion(), f.getAnswer());
                ScoreDoc[] mustHaveResults = LuceneFeatures.lightQuery(ck12Searcher, mustHave, Integer.MAX_VALUE);

                f.setCk12EbookBothQADocCountAMustHave(mustHaveResults.length);
            } catch (Exception e) {
            }
            return f;
        });

    }

    private static double[] rankingArray(List<ScoredDocument> docs) {
        return docs.stream().mapToDouble(d -> d.getDocId()).toArray();
    }

}
