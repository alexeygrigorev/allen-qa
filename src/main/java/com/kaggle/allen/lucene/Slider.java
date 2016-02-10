package com.kaggle.allen.lucene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Slider {

    public static <E> List<List<E>> slide(List<E> input, int window, int step) {
        if (input.isEmpty()) {
            return Collections.emptyList();
        }

        List<List<E>> result = new ArrayList<>();

        int begin = 0;
        int size = input.size();
        int end = Math.min(size, begin + window);

        result.add(input.subList(begin, end));

        while (end < size) {
            begin = begin + step;
            end = Math.min(size, end + step);
            result.add(input.subList(begin, end));
        }

        return result;
    }

}
