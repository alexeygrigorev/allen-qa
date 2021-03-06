package com.kaggle.allen.lucene;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import com.kaggle.allen.text.FilterChain;
import com.kaggle.allen.text.ParserFactory;

public class IndexEntireEnWikiNGrams {

    private static final FieldType TITLE_FIELD = createTitleFieldType();
    private static final FieldType CONTENT_FIELD = createContentFieldType();

    public static final Version LUCENE_VERSION = Version.LUCENE_4_9;

    public static void main(String[] args) throws Exception {
        String pathWiki = args[0];

        File index = new File("data/fullenwiki-index-" + RandomStringUtils.randomAlphabetic(5));

        FileUtils.write(new File("log.log"), "for " + pathWiki + " writing to " + index + "\n", true);

        index.mkdirs();

        FSDirectory directory = FSDirectory.open(index);

        ImmutableMap.Builder<String, Analyzer> analyzers = ImmutableMap.builder();
        analyzers.put("content", new LinebreakAnalyzer(LUCENE_VERSION));
        PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer(LUCENE_VERSION),
                analyzers.build());

        FilterChain filterChain = ParserFactory.createFilterChain();
        NgramExtractor extractor = new NgramExtractor(filterChain);

        IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(LUCENE_VERSION, analyzer));

        LineIterator li = FileUtils.lineIterator(new File(pathWiki));

        UnmodifiableIterator<List<String>> partitions = Iterators.partition(li, 1000);

        AtomicInteger cnt = new AtomicInteger();
        partitions.forEachRemaining(partition -> {
            partition.parallelStream().forEach(line -> {
                try {
                    process(line, extractor, writer, cnt);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

        writer.commit();
        writer.close();
        directory.close();
    }

    private static void process(String line, NgramExtractor extractor, IndexWriter writer, AtomicInteger cnt) throws IOException {
        String[] split = line.split("\t");
        if (split.length < 2) {
            return;
        }

        String title = split[0];
        if (title.toLowerCase().contains("disambiguation")) {
            return;
        }

        String content = split[1].replace("[::newline::]", "\n").trim();
        if (content.startsWith("REDIRECT [[") && content.endsWith("]]")) {
            return;
        }

        add(title, content, writer, extractor);
        int i = cnt.incrementAndGet();
        if (i % 10 == 0) {
            System.out.println(i + " " + title);
        }
    }

    private static void add(String title, String content, IndexWriter writer, NgramExtractor extractor) throws IOException {
        Document document = new Document();

        List<String> parsedConetnt = extractor.ngrams(content);

        List<List<String>> slider = Slider.slide(parsedConetnt, 500, 150);

        int cnt = 0;
        for (List<String> window : slider) {
            document.add(new Field("content", String.join("\n", window), CONTENT_FIELD));
            document.add(new Field("title", title + "_" + cnt, TITLE_FIELD));
            cnt++;
        }

        // should be thread safe
        writer.addDocument(document);
    }

    private static FieldType createTitleFieldType() {
        FieldType field = new FieldType();
        field.setTokenized(false);
        field.setStored(true);
        field.freeze();
        return field;
    }

    private static FieldType createContentFieldType() {
        FieldType field = new FieldType();
        field.setIndexed(true);
        field.setOmitNorms(true);
        field.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
        field.setStored(false);
        field.setTokenized(true);
        field.setStoreTermVectorPayloads(false);
        field.setStoreTermVectorPositions(false);
        field.setStoreTermVectorOffsets(false);
        field.freeze();
        return field;
    }
}
