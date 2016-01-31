package com.kaggle.allen.text;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class Tokenizer {

    private final ThreadLocal<StanfordCoreNLP> pipeline;

    public Tokenizer() {
        this.pipeline = createNlpPipeline();
    }

    public List<String> lemmas(String text) {
        text = text.replace('-', ' ');
        List<List<String>> sentenceTokenize = sentences(text);
        return Lists.newArrayList(Iterables.concat(sentenceTokenize));
    }

    public String normalized(String text) {
        List<String> lemmas = lemmas(text);
        return String.join(" ", lemmas);
    }

    public List<List<String>> sentences(String text) {
        if (StringUtils.isBlank(text)) {
            return Collections.emptyList();
        }

        Annotation document = new Annotation(text);
        pipeline.get().annotate(document);

        List<List<String>> sentences = Lists.newArrayList();
        List<CoreMap> sentencesMap = document.get(SentencesAnnotation.class);

        for (CoreMap sentenceMap : sentencesMap) {
            List<String> sentence = Lists.newArrayList();

            List<CoreLabel> allTokens = sentenceMap.get(TokensAnnotation.class);
            for (CoreLabel token : allTokens) {
                String lemma = token.get(LemmaAnnotation.class);
                sentence.add(lemma);
            }

            if (!sentence.isEmpty()) {
                sentences.add(sentence);
            }
        }

        return sentences;
    }

    private ThreadLocal<StanfordCoreNLP> createNlpPipeline() {
        return new ThreadLocal<StanfordCoreNLP>() {
            @Override
            protected synchronized StanfordCoreNLP initialValue() {
                Properties props = new Properties();
                props.put("annotators", "tokenize, ssplit, pos,lemma");
                props.put("tokenize.options", "untokenizable=noneDelete," + "strictTreebank3=true,"
                        + "ptb3Escaping=false," + "escapeForwardSlashAsterisk=false");
                props.put("ssplit.newlineIsSentenceBreak", "always");
                return new StanfordCoreNLP(props);
            }
        };
    }
}
