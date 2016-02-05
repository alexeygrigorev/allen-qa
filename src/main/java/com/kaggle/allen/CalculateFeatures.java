package com.kaggle.allen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
import com.kaggle.allen.lucene.LuceneFeatureExtractor;
import com.kaggle.allen.lucene.NgramExtractor;
import com.kaggle.allen.lucene.LuceneFeatureExtractor.ScoredDocument;
import com.kaggle.allen.text.Parser;
import com.kaggle.allen.text.ParserFactory;
import com.kaggle.allen.w2v.Word2Vec;

import weka.core.matrix.DoubleVector;

public class CalculateFeatures {

    private static final int DOCS_BOTH_KEEP = 10;
    private static final int DOCS_TO_SELECT = 20;
    private static final Logger LOGGER = LoggerFactory.getLogger(CalculateFeatures.class);

    public static void main(String[] args) throws Exception {
        List<Question> allData = Lists.newArrayList(Read.allData());

        Stream<Features> featues = parseAndPivot(allData.stream());
        featues = word2Vec(featues);
        featues = lucene(featues, "wiki_full", new File("/home/agrigorev/tmp/fullwiki-lucene/merged"),
                Features::getQuestion, Features::getAnswer);
        featues = lucene(featues, "wiki_ck12", new File("data/index"), Features::getQuestion, Features::getAnswer);
        featues = lucene(featues, "ck12_ebook", new File("data/ck12-index"), Features::getQuestion,
                Features::getAnswer);
        featues = lucene(featues, "wiki_ck12_ngrams", new File("data/ngrams-index"), Features::getNgramsQuestion,
                Features::getNgramsAnswer);
        featues = lucene(featues, "ck12_ebook_ngrams", new File("data/ck12-ngrams-index"), Features::getNgramsQuestion,
                Features::getNgramsAnswer);

        writeAsJson(featues, "lucene-features-5.json");
    }

    @SuppressWarnings("resource")
    private static void writeAsJson(Stream<Features> featues, String resultFilename) throws FileNotFoundException {
        ObjectMapper mapper = new ObjectMapper();

        PrintWriter pw = new PrintWriter(resultFilename);
        featues.map(f -> {
            try {
                return mapper.writeValueAsString(f);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }).forEach(pw::println);
        pw.flush();
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
            DoubleVector answerVec = normalize(word2vec.vector(String.join(" ", f.getAnswer())));
            double cosine = questionVec.innerProduct(answerVec);

            if (!Double.isNaN(cosine)) {
                Word2VecFeatures w2v = new Word2VecFeatures();
                // w2v.setWord2vecAnswer(answerVec.getArray());
                // w2v.setWord2vecQuestion(questionVec.getArray());
                w2v.setWord2vecCosine(cosine);

                f.setWord2VecFeatures(w2v);
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

    private static Stream<Features> lucene(Stream<Features> featues, String name, File index,
            Function<Features, List<String>> questionFunction, Function<Features, List<String>> answerFunction)
                    throws IOException {
        LOGGER.info("lucene wiki");
        IndexSearcher wikiSearcher = LuceneFeatureExtractor.searcher(index);

        return featues.map(f -> {
            try {
                LuceneFeatures luceneFeatures = new LuceneFeatures();

                List<String> questionTokens = questionFunction.apply(f);
                List<String> answerTokens = answerFunction.apply(f);

                Query question = LuceneFeatureExtractor.createQuery(questionTokens);
                Query answer = LuceneFeatureExtractor.createQuery(answerTokens);

                List<ScoredDocument> questionDocs = LuceneFeatureExtractor.query(wikiSearcher, question,
                        DOCS_TO_SELECT);
                luceneFeatures.setQuestionResult(questionDocs);

                List<ScoredDocument> answerDocs = LuceneFeatureExtractor.query(wikiSearcher, answer, DOCS_TO_SELECT);
                luceneFeatures.setAnswerResult(answerDocs);

                Query both = LuceneFeatureExtractor.createQuery(Iterables.concat(questionTokens, answerTokens));
                ScoreDoc[] bothResults = LuceneFeatureExtractor.lightQuery(wikiSearcher, both, Integer.MAX_VALUE);
                luceneFeatures.setBothQADocCount(bothResults.length);

                Query mustHave = LuceneFeatureExtractor.createMustHaveQuery(f.getQuestion(), f.getAnswer());
                ScoreDoc[] mustHaveResults = LuceneFeatureExtractor.lightQuery(wikiSearcher, mustHave,
                        Integer.MAX_VALUE);
                luceneFeatures.setBothQADocCountMustHave(mustHaveResults.length);

                int[] bothResultsIds = top10Ids(bothResults);
                luceneFeatures.setBothQADoc(bothResultsIds);

                int[] mustHaveIds = top10Ids(mustHaveResults);
                luceneFeatures.setBothQADocAMustHave(mustHaveIds);

                luceneFeatures.setBothQAScores(top10Scores(bothResults));
                luceneFeatures.setBothQAScoresMustHave(top10Scores(mustHaveResults));

                f.addLuceneFeature(name, luceneFeatures);
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

    private static double[] rankingArray(List<ScoredDocument> docs) {
        return docs.stream().mapToDouble(d -> d.getDocId()).toArray();
    }

}
