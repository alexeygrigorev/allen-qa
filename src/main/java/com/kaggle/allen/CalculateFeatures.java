package com.kaggle.allen;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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
import com.kaggle.allen.lucene.NgramExtractor;
import com.kaggle.allen.text.Parser;
import com.kaggle.allen.text.ParserFactory;
import com.kaggle.allen.w2v.Word2Vec;

import weka.core.matrix.DoubleVector;

public class CalculateFeatures {

    private static final int DOCS_BOTH_KEEP = 10;
    private static final int DOCS_TO_SELECT = 20;
    private static final Logger LOGGER = LoggerFactory.getLogger(CalculateFeatures.class);

    @SuppressWarnings("resource")
    public static void main(String[] args) throws Exception {
        List<Question> allData = Lists.newArrayList(Read.allData());

        Stream<Features> featues = parseAndPivot(allData.stream());
//        featues = word2Vec(featues);
        featues = luceneWiki(featues);
        featues = luceneCk12(featues);
        featues = luceneWikiNgrams(featues);
        featues = luceneCk12Ngrams(featues);

        ObjectMapper mapper = new ObjectMapper();

        PrintWriter pw = new PrintWriter("lucene-features-4.json");
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
        NgramExtractor ngramExtractor = ParserFactory.createNGramExtractor();

        return stream.flatMap(q -> {
            List<Features> res = Lists.newArrayListWithCapacity(4);

            String question = q.getContent();
            List<String> questionTokens = parser.parse(question);
            List<String> questionNGrams = ngramExtractor.ngrams(question);

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

                List<String> answerNGrams = ngramExtractor.ngrams(question);
                features.setNgramsQuestion(questionNGrams);
                features.setNgramsAnswer(answerNGrams);

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
        LOGGER.info("w2v");
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
        LOGGER.info("lucene wiki");
        IndexSearcher wikiSearcher = LuceneFeatures.searcher(new File("data/index"));

        return featues.map(f -> {
            try {
                Query question = LuceneFeatures.createQuery(f.getQuestion());
                Query answer = LuceneFeatures.createQuery(f.getAnswer());

                List<ScoredDocument> questionDocs = LuceneFeatures.query(wikiSearcher, question, DOCS_TO_SELECT);
                f.setCk12WikiQuestionResult(questionDocs);

                List<ScoredDocument> answerDocs = LuceneFeatures.query(wikiSearcher, answer, DOCS_TO_SELECT);
                f.setCk12WikiAnswerResult(answerDocs);

                Query both = LuceneFeatures.createQuery(Iterables.concat(f.getQuestion(), f.getAnswer()));
                ScoreDoc[] bothResults = LuceneFeatures.lightQuery(wikiSearcher, both, Integer.MAX_VALUE);
                f.setCk12WikiBothQADocCount(bothResults.length);

                Query mustHave = LuceneFeatures.createMustHaveQuery(f.getQuestion(), f.getAnswer());
                ScoreDoc[] mustHaveResults = LuceneFeatures.lightQuery(wikiSearcher, mustHave, Integer.MAX_VALUE);
                f.setCk12WikiBothQADocCountAMustHave(mustHaveResults.length);

                int[] bothResultsIds = top10Ids(bothResults);
                f.setCk12WikiBothQADoc(bothResultsIds);
                int[] mustHaveIds = top10Ids(mustHaveResults);
                f.setCk12WikiBothQADocAMustHave(mustHaveIds);

                f.setCk12WikiBothQAScores(top10Scores(bothResults));
                f.setCk12WikiBothQAScoresMustHave(top10Scores(mustHaveResults));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return f;
        });
    }

    private static int[] top10Ids(ScoreDoc[] bothResults) {
        return Arrays.stream(bothResults).limit(DOCS_BOTH_KEEP).mapToInt(s -> s.doc).toArray();
    }

    private static double[] top10Scores(ScoreDoc[] bothResults) {
        double[] result = new double[DOCS_BOTH_KEEP];
        for (int i = 0; i < Math.min(DOCS_BOTH_KEEP, bothResults.length); i++) {
            result[i] = bothResults[i].score;
        }
        return result;
    }

    private static Stream<Features> luceneCk12(Stream<Features> featues) throws IOException {
        LOGGER.info("ck12");
        IndexSearcher ck12Searcher = LuceneFeatures.searcher(new File("data/ck12-index"));

        return featues.map(f -> {
            try {
                Query question = LuceneFeatures.createQuery(f.getQuestion());
                Query answer = LuceneFeatures.createQuery(f.getAnswer());

                List<ScoredDocument> questionDocs = LuceneFeatures.query(ck12Searcher, question, DOCS_TO_SELECT);
                f.setCk12EbookQuestionResult(questionDocs);

                List<ScoredDocument> answerDocs = LuceneFeatures.query(ck12Searcher, answer, DOCS_TO_SELECT);
                f.setCk12EbookAnswerResult(answerDocs);

                Query both = LuceneFeatures.createQuery(Iterables.concat(f.getQuestion(), f.getAnswer()));
                ScoreDoc[] bothResults = LuceneFeatures.lightQuery(ck12Searcher, both, Integer.MAX_VALUE);

                f.setCk12EbookBothQADocCount(bothResults.length);

                Query mustHave = LuceneFeatures.createMustHaveQuery(f.getQuestion(), f.getAnswer());
                ScoreDoc[] mustHaveResults = LuceneFeatures.lightQuery(ck12Searcher, mustHave, Integer.MAX_VALUE);

                f.setCk12EbookBothQADocCountAMustHave(mustHaveResults.length);

                int[] bothResultsIds = top10Ids(bothResults);
                f.setCk12EbookBothQADoc(bothResultsIds);
                int[] mustHaveIds = top10Ids(mustHaveResults);
                f.setCk12EbookBothQADocAMustHave(mustHaveIds);

                f.setCk12EbookBothQAScores(top10Scores(bothResults));
                f.setCk12EbookBothQAScoresMustHave(top10Scores(mustHaveResults));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return f;
        });
    }

    private static Stream<Features> luceneWikiNgrams(Stream<Features> featues) throws IOException {
        LOGGER.info("wiki ngram");
        IndexSearcher ngramsCk12Searcher = LuceneFeatures.searcher(new File("data/ngrams-index"));

        return featues.map(f -> {
            try {
                Query question = LuceneFeatures.createQuery(f.getNgramsQuestion());
                Query answer = LuceneFeatures.createQuery(f.getNgramsAnswer());

                List<ScoredDocument> questionDocs = LuceneFeatures.query(ngramsCk12Searcher, question, DOCS_TO_SELECT);
                f.setNgramsCk12WikiQuestionResult(questionDocs);

                List<ScoredDocument> answerDocs = LuceneFeatures.query(ngramsCk12Searcher, answer, DOCS_TO_SELECT);
                f.setNgramsCk12WikiAnswerResult(answerDocs);

                Query both = LuceneFeatures.createQuery(Iterables.concat(f.getQuestion(), f.getAnswer()));
                ScoreDoc[] bothResults = LuceneFeatures.lightQuery(ngramsCk12Searcher, both, Integer.MAX_VALUE);

                f.setNgramsCk12WikiBothQADocCount(bothResults.length);

                Query mustHave = LuceneFeatures.createMustHaveQuery(f.getQuestion(), f.getAnswer());
                ScoreDoc[] mustHaveResults = LuceneFeatures.lightQuery(ngramsCk12Searcher, mustHave, Integer.MAX_VALUE);

                f.setNgramsCk12WikiBothQADocCountAMustHave(mustHaveResults.length);

                int[] bothResultsIds = top10Ids(bothResults);
                f.setNgramsCk12WikiBothQADoc(bothResultsIds);
                int[] mustHaveIds = top10Ids(mustHaveResults);
                f.setNgramsCk12WikiBothQADocMustHave(mustHaveIds);

                f.setNgramsCk12WikiBothQAScores(top10Scores(bothResults));
                f.setNgramsCk12WikiBothQAScoresMustHave(top10Scores(mustHaveResults));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return f;
        });
    }

    private static Stream<Features> luceneCk12Ngrams(Stream<Features> featues) throws IOException {
        LOGGER.info("ck12 ngrams");
        IndexSearcher ck12Searcher = LuceneFeatures.searcher(new File("data/ck12-ngrams-index"));

        return featues.map(f -> {
            try {
                Query question = LuceneFeatures.createQuery(f.getNgramsQuestion());
                Query answer = LuceneFeatures.createQuery(f.getNgramsAnswer());

                List<ScoredDocument> questionDocs = LuceneFeatures.query(ck12Searcher, question, DOCS_TO_SELECT);
                f.setNgramsCk12EbookQuestionResult(questionDocs);

                List<ScoredDocument> answerDocs = LuceneFeatures.query(ck12Searcher, answer, DOCS_TO_SELECT);
                f.setNgramsCk12EbookAnswerResult(answerDocs);

                Query both = LuceneFeatures.createQuery(Iterables.concat(f.getQuestion(), f.getAnswer()));
                ScoreDoc[] bothResults = LuceneFeatures.lightQuery(ck12Searcher, both, Integer.MAX_VALUE);

                f.setNgramsCk12EbookBothQADocCount(bothResults.length);

                Query mustHave = LuceneFeatures.createMustHaveQuery(f.getQuestion(), f.getAnswer());
                ScoreDoc[] mustHaveResults = LuceneFeatures.lightQuery(ck12Searcher, mustHave, Integer.MAX_VALUE);

                f.setNgramsCk12EbookBothQADocCountAMustHave(mustHaveResults.length);

                int[] bothResultsIds = top10Ids(bothResults);
                f.setNgramsCk12EbookBothQADoc(bothResultsIds);
                int[] mustHaveIds = top10Ids(mustHaveResults);
                f.setNgramsCk12EbookBothQADocMustHave(mustHaveIds);

                f.setNgramsCk12EbookBothQAScores(top10Scores(bothResults));
                f.setNgramsCk12EbookBothQAScoresMustHave(top10Scores(mustHaveResults));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return f;
        });
    }

    private static double[] rankingArray(List<ScoredDocument> docs) {
        return docs.stream().mapToDouble(d -> d.getDocId()).toArray();
    }

}
