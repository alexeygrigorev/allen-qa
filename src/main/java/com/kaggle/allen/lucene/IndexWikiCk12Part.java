package com.kaggle.allen.lucene;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
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
import com.kaggle.allen.text.Parser;
import com.kaggle.allen.text.ParserFactory;
import com.kaggle.allen.wiki.WikiTextUtils;

public class IndexWikiCk12Part {

    private static final FieldType TITLE_FIELD = createTitleFieldType();
    private static final FieldType CONTENT_FIELD = createContentFieldType();

    public static final Version LUCENE_VERSION = Version.LUCENE_4_9;

    public static void main(String[] args) throws Exception {
        File index = new File("data/index");
        index.mkdirs();

        FSDirectory directory = FSDirectory.open(index);

        ImmutableMap.Builder<String, Analyzer> analyzers = ImmutableMap.builder();
        analyzers.put("content", new LinebreakAnalyzer(LUCENE_VERSION));
        PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer(LUCENE_VERSION),
                analyzers.build());

        Parser parser = ParserFactory.createParser();
        IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(LUCENE_VERSION, analyzer));

        File wikiDir = new File("data/wiki");
        for (String path : wikiDir.list()) {
            File file = new File(wikiDir, path);
            System.out.println(file);
            add(file, writer, parser);
        }

        writer.commit();
        writer.close();
        directory.close();
    }

    public static void add(File file, IndexWriter writer, Parser parser) throws Exception {
        Document document = new Document();

        String filename = file.getName();
        String title = filename.substring(0, filename.length() - ".wiki".length());
        String rawContent = FileUtils.readFileToString(file);
        String content = WikiTextUtils.extractPlainText(rawContent);
        List<String> parsedConetnt = parser.parse(content);

        document.add(new Field("content", String.join("\n", parsedConetnt), CONTENT_FIELD));
        document.add(new Field("title", title, TITLE_FIELD));

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
