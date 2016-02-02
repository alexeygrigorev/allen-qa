package com.kaggle.allen.wiki;

import java.util.regex.Pattern;

import org.apache.commons.lang3.text.translate.AggregateTranslator;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import org.apache.commons.lang3.text.translate.EntityArrays;
import org.apache.commons.lang3.text.translate.LookupTranslator;
import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.NoOpDocumentBuilder;
import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;

import com.kaggle.allen.text.UnicodeUtils;

public class WikiTextUtils {

    private static final Pattern MATH_TAG_PATTERN = Pattern.compile("<math.+?</math>", Pattern.DOTALL);

    public static String extractPlainText(String wikiMarkup) {
        MarkupParser parser = new MarkupParser();
        MediaWikiLanguage markupLanaguage = new MediaWikiLanguage();
        markupLanaguage.setEnableMacros(false);
        parser.setMarkupLanguage(markupLanaguage);
        PlaintextDocumentBuilder builder = new PlaintextDocumentBuilder();
        parser.setBuilder(builder);
        wikiMarkup = UnicodeUtils.normalizeUnicode(wikiMarkup);
        wikiMarkup = MATH_TAG_PATTERN.matcher(wikiMarkup).replaceAll("");

        parser.parse(wikiMarkup);
        return builder.getResult();
    }

    public static class PlaintextDocumentBuilder extends NoOpDocumentBuilder {

        private StringBuilder writer = new StringBuilder();
        private String result = "";

        @Override
        public void beginBlock(BlockType type, Attributes attributes) {
            writer.append(" ");
        }

        @Override
        public void endBlock() {
            writer.append("\n");
        }

        @Override
        public void beginSpan(SpanType type, Attributes attributes) {
            writer.append(" ");
        }

        @Override
        public void endSpan() {
            writer.append(" ");
        }

        @Override
        public void beginHeading(int level, Attributes attributes) {
            writer.append(" ");
        }

        @Override
        public void endHeading() {
            writer.append("\n\n");
        }

        @Override
        public void characters(String text) {
            writer.append(text);
        }

        private static final CharSequenceTranslator TRANSLATOR = new AggregateTranslator(
                new LookupTranslator(EntityArrays.ISO8859_1_UNESCAPE()),
                new LookupTranslator(EntityArrays.BASIC_UNESCAPE()),
                new LookupTranslator(EntityArrays.HTML40_EXTENDED_UNESCAPE()));

        @Override
        public void entityReference(String entity) {
            String translatedEntity = TRANSLATOR.translate('&' + entity + ';');
            writer.append(translatedEntity);
        }

        @Override
        public void link(Attributes attributes, String link, String text) {
            if (link.isEmpty() && text.isEmpty()) {
                return;
            }
            String full = (link + text).toLowerCase();
            // special link types
            if (full.contains("category:")) {
                return;
            }
            if (full.contains("image:")) {
                return;
            }
            if (full.contains("file:")) {
                return;
            }
            if (full.contains("thumb")) {
                return;
            }
            if (full.contains("|")) {
                return;
            }

            // urls, because the parser also detects raw links
            if (full.matches("https?:")) {
                return;
            }

            // language links
            if (text.matches("\\w{2}:")) {
                return;
            }

            // when textfield is empty the link will be shown, except anything
            // in parentheses.
            if (text.isEmpty()) {
                text = link.replaceAll("\\(.*?\\)", "");
            }

            // LINKS
            writer.append(" [[" + text + "]] ");
        }

        @Override
        public void acronym(String text, String definition) {
            writer.append(text);
        }

        @Override
        public void lineBreak() {
            writer.append("\n");
        }

        @Override
        public void charactersUnescaped(String literal) {
            writer.append(literal);
        }

        @Override
        public void endDocument() {
            String doc = writer.toString();

            // remove remaining/undetected templates
            doc = Pattern.compile("\\{\\{[^\\{]*?\\}\\}").matcher(doc).replaceAll("");
            doc = Pattern.compile("\\u2016[^\\u2016]*?\\u2016").matcher(doc).replaceAll("");

            // remove dangling lines
            doc = Pattern.compile("(:?\\A|\\n)\\s*[\\*\\|:].*").matcher(doc).replaceAll("");
            doc = Pattern.compile("\\}\\}\\s*").matcher(doc).replaceAll("");

            // remove undetected emphasis tags
            doc = Pattern.compile("'{2,}").matcher(doc).replaceAll("");

            // comments
            doc = Pattern.compile("<!--.*?-->", Pattern.DOTALL).matcher(doc).replaceAll("");

            // references
            doc = Pattern.compile("<references>.*?</references>", Pattern.DOTALL).matcher(doc).replaceAll("");
            doc = Pattern.compile("<ref[^>/]*>.*?</ref>", Pattern.DOTALL).matcher(doc).replaceAll("");
            doc = Pattern.compile("<ref[^>]*>").matcher(doc).replaceAll("");
            doc = Pattern.compile("</ref[^>]*>").matcher(doc).replaceAll("");

            // empty/unknown inline tags and non inline tags
            doc = Pattern.compile("<([^ >]+)[^>]*>(.*?)</\\1>").matcher(doc).replaceAll("$2");
            doc = Pattern.compile("<([^ >]+)[^>]*/?>").matcher(doc).replaceAll(" ");

            // remove language links
            doc = Pattern.compile("\\[\\[[a-z]{2,3}:.*?\\]\\]").matcher(doc).replaceAll("");

            // remove misc quotation symbols
            doc = Pattern.compile("'|\\\"").matcher(doc).replaceAll("");
            // reposition plurals into links
            doc = Pattern.compile("\\]\\](\\w)").matcher(doc).replaceAll("$1\\]\\]");

            doc = doc.trim();

            this.result = doc;
        }

        public String getResult() {
            return result;
        }

    }
}
