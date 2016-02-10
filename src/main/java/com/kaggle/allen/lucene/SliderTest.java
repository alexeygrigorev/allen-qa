package com.kaggle.allen.lucene;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class SliderTest {

    @Test
    public void test() {
        List<List<String>> slide = Slider.slide(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "a", "b", "c", "d",
                "e", "f", "g", "a", "b", "c", "d", "e", "f", "g"), 5, 3);
        slide.forEach(System.out::println);
    }

    @Test
    public void test2() {
        List<List<String>> slide = Slider.slide(Arrays.asList("a", "b", "c"), 5, 3);
        slide.forEach(System.out::println);
    }

}
