package com.kaggle.allen.lucene;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.kaggle.allen.text.Parser;
import com.kaggle.allen.text.ParserFactory;

public class IndexWikiCk12ConceptsEbook {

    private static final FieldType TITLE_FIELD = createTitleFieldType();
    private static final FieldType CONTENT_FIELD = createContentFieldType();

    public static final Version LUCENE_VERSION = Version.LUCENE_4_9;

    public static void main(String[] args) throws Exception {
        File index = new File("data/ck12-index");
        index.mkdirs();

        FSDirectory directory = FSDirectory.open(index);

        ImmutableMap.Builder<String, Analyzer> analyzers = ImmutableMap.builder();
        analyzers.put("content", new LinebreakAnalyzer(LUCENE_VERSION));
        PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer(LUCENE_VERSION),
                analyzers.build());

        Parser parser = ParserFactory.createParser();
        IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(LUCENE_VERSION, analyzer));

        File wikiDir = new File("/home/agrigorev/Downloads/allen/ck12-concepts");
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
        String htmlContent = FileUtils.readFileToString(file);

        org.jsoup.nodes.Document jsoupDoc = Jsoup.parse(htmlContent);
        String title = jsoupDoc.select("title").text();

        List<Ck12Paragraph> paragraphs = paragraphs(jsoupDoc);

        for (Ck12Paragraph p : paragraphs) {
            Document document = new Document();

            List<String> parsedContent = parser.parse(p.text());

            document.add(new Field("content", String.join("\n", parsedContent), CONTENT_FIELD));
            document.add(new Field("document", title, TITLE_FIELD));
            document.add(new Field("title", p.title, TITLE_FIELD));

            writer.addDocument(document);
        }
    }

    private static List<Ck12Paragraph> paragraphs(org.jsoup.nodes.Document jsoupDoc) {
        Element body = jsoupDoc.select("body").get(0);

        Ck12Paragraph current = new Ck12Paragraph("", "DUMMY");
        List<Ck12Paragraph> all = Lists.newArrayList();

        for (Element child : body.children()) {
            if ("h1".equals(child.tagName())) {
                all.add(current);
                current = new Ck12Paragraph(child.text().trim(), child.id().trim());
            }

            current.add(child);
        }

        all.add(current);
        return all.stream().filter(c -> !c.elements.isEmpty()).filter(c -> !c.title.equals("References"))
                .collect(Collectors.toList());

    }

    private static class Ck12Paragraph {
        private String title;
        private String id;

        private List<Element> elements = Lists.newArrayList();

        public Ck12Paragraph(String title, String id) {
            this.title = title;
            this.id = id;
        }

        public void add(Element child) {
            elements.add(child);
        }

        public String text() {
            return elements.stream().map(e -> e.text()).collect(Collectors.joining(" "));
        }
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
