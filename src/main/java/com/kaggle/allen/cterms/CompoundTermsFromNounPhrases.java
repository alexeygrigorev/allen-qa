package com.kaggle.allen.cterms;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import com.alexeygrigorev.rseq.BeanMatchers;
import com.alexeygrigorev.rseq.Match;
import com.alexeygrigorev.rseq.Pattern;
import com.alexeygrigorev.rseq.XMatcher;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kaggle.allen.text.FilterChain;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class CompoundTermsFromNounPhrases {

    public static void main(String[] args) throws Exception {
        List<String> sentences = readSentences();
        List<String> nounPhrases = extractNounPhrases(sentences);

        Set<String> stopwords = Sets.newHashSet(FileUtils.readLines(new File("src/main/resources/stopwords-en.txt")));
        FilterChain chain = new FilterChain(stopwords);

        Set<String> seens = Sets.newHashSet();

        PrintWriter pw = new PrintWriter(new File("compound-terms-noun-phrases.txt"));

        for (String nounPhrase : nounPhrases) {
            List<String> tokens = Arrays.asList(nounPhrase.split(" "));
            List<String> normalized = chain.process(tokens);
            if (normalized.size() < 2 || normalized.size() > 3) {
                continue;
            }

            String result = String.join(" ", normalized);
            if (seens.contains(result)) {
                continue;
            }

            seens.add(result);
            pw.println(result);
        }

        pw.flush();
        pw.close();
    }

    public static List<String> extractNounPhrases(List<String> sentences) {
        ThreadLocal<StanfordCoreNLP> pipeline = new ThreadLocal<StanfordCoreNLP>() {
            @Override
            protected synchronized StanfordCoreNLP initialValue() {
                Properties props = new Properties();
                props.put("annotators", "tokenize, ssplit, pos, lemma");
                props.put("tokenize.options", "untokenizable=noneDelete," + "strictTreebank3=true,"
                        + "ptb3Escaping=false," + "escapeForwardSlashAsterisk=false");
                props.put("ssplit.newlineIsSentenceBreak", "always");
                return new StanfordCoreNLP(props);
            }
        };

        XMatcher<Word> det = BeanMatchers.eq(Word.class, "pos", "DT");
        XMatcher<Word> adj = BeanMatchers.eq(Word.class, "pos", "JJ");
        XMatcher<Word> noun = BeanMatchers.in(Word.class, "pos", ImmutableSet.of("NN", "NNS"));

        ThreadLocal<Pattern<Word>> pattern = ThreadLocal
                .withInitial(() -> Pattern.create(det.optional(), adj.zeroOrMore(), noun.oneOrMore()));

        return sentences.parallelStream().flatMap(text -> {
            text = text.replace('-', ' ');
            Annotation textAnnotation = new Annotation(text);
            pipeline.get().annotate(textAnnotation);
            List<Word> tokens = Lists.transform(textAnnotation.get(TokensAnnotation.class), Word::new);
            List<Match<Word>> matches = pattern.get().find(tokens);
            return matches.stream();
        }).map(m -> {
            List<String> lemmas = Lists.transform(m.getMatchedSubsequence(), Word::getLemma);
            return String.join(" ", lemmas);
        }).collect(Collectors.toList());
    }

    private static class Word {
        private String pos;
        private String lemma;

        public Word(CoreLabel token) {
            this.pos = token.get(PartOfSpeechAnnotation.class);
            this.lemma = token.get(LemmaAnnotation.class);
        }

        public String getLemma() {
            return lemma;
        }

        @Override
        public String toString() {
            return "Word [pos=" + pos + ", lemma=" + lemma + "]";
        }
    }

    private static List<String> readSentences() throws IOException {
        List<String> sentences = Lists.newArrayList();

        List<String> glossary = FileUtils.readLines(new File("data/glossary"));
        for (String line : glossary) {
            line = line.toLowerCase();
            String[] split = line.split("\t");
            if (split.length > 1) {
                sentences.add(split[1].toLowerCase());
            }
        }

        List<String> training = FileUtils.readLines(new File("/home/agrigorev/Downloads/allen/training_set.tsv"));
        for (String line : training) {
            line = line.toLowerCase();
            String[] split = line.split("\t");

            sentences.add(split[1]);
            sentences.addAll(Arrays.asList(split).subList(3, split.length));
        }

        List<String> validation = FileUtils.readLines(new File("/home/agrigorev/Downloads/allen/validation_set.tsv"));
        for (String line : validation) {
            line = line.toLowerCase();
            String[] split = line.split("\t");

            sentences.add(split[1]);
            sentences.addAll(Arrays.asList(split).subList(2, split.length));
        }

        return sentences;
    }
}
