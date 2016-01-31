package com.kaggle.allen.text;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.SetMultimap;

/**
 * 
 * @author agrigorev
 *
 */
public class CompoundTermsExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompoundTermsExtractor.class);
    private static final char SEPARATOR = ' ';

    // length to list of compound terms of that length
    private final SetMultimap<Integer, List<String>> compoundTerms;
    private final int maxLength;

    public CompoundTermsExtractor(Set<List<String>> compoundTerms) {
        this.compoundTerms = wrap(compoundTerms);
        this.maxLength = fildMaxLength(this.compoundTerms);
    }

    private int fildMaxLength(SetMultimap<Integer, List<String>> compoundTerms) {
        return Collections.max(compoundTerms.keySet());
    }

    private static SetMultimap<Integer, List<String>> wrap(Set<List<String>> compoundTerms) {
        ImmutableSetMultimap.Builder<Integer, List<String>> builder = ImmutableSetMultimap.builder();

        for (List<String> compoundTerm : compoundTerms) {
            builder.put(compoundTerm.size(), compoundTerm);
        }

        return builder.build();
    }

    public List<String> findCompoundTerms(List<String> originalSentence) {
        int sentenceSize = originalSentence.size();

        List<String> lowerCasedSentence = Lists.transform(originalSentence, String::toLowerCase);
        List<String> result = Lists.newArrayListWithExpectedSize(sentenceSize);

        int currentBeginPosition = 0;
        while (currentBeginPosition < sentenceSize) {
            // we always want to look at the longest phrases first. So fill the
            // token buffer until it has as many tokens as the longest phrase
            int endPosition = Math.min(currentBeginPosition + maxLength, sentenceSize);

            // check the possible phrases (longest first) and emit it if
            // possible
            while (candidateSize(currentBeginPosition, endPosition) > 0) {
                // just one term
                if (candidateSize(currentBeginPosition, endPosition) == 1) {
                    result.add(originalSentence.get(currentBeginPosition));
                    break;
                }

                List<String> candidate = lowerCasedSentence.subList(currentBeginPosition, endPosition);
                int candidateSize = candidate.size();
                Set<List<String>> set = compoundTerms.get(candidateSize);
                if (set.contains(candidate)) {
                    List<String> compoundTermList = originalSentence.subList(currentBeginPosition, endPosition);
                    String compoundTerm = StringUtils.join(compoundTermList, SEPARATOR);
                    LOGGER.debug("emitting '{}'", compoundTerm);
                    result.add(compoundTerm);

                    currentBeginPosition = endPosition - 1;
                    break;
                }

                endPosition--;
            }

            currentBeginPosition++;
        }

        return result;
    }

    private int candidateSize(int currentBeginPosition, int endPosition) {
        return endPosition - currentBeginPosition;
    }

}
