package com.kaggle.allen.wiki;

import java.util.Deque;
import java.util.LinkedList;
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
        parser.setMarkupLanguage(new MediaWikiLanguage());
        PlaintextDocumentBuilder builder = new PlaintextDocumentBuilder();
        parser.setBuilder(builder);
        wikiMarkup = UnicodeUtils.normalizeUnicode(wikiMarkup);
        wikiMarkup = MATH_TAG_PATTERN.matcher(wikiMarkup).replaceAll("");

        parser.parse(wikiMarkup);
        return builder.getResult();
    }

    public static class PlaintextDocumentBuilder extends NoOpDocumentBuilder {

        private StringBuilder writer = new StringBuilder();

        /**
         * These lists store all blocks within a block/span that will not be
         * rendered.
         */
        private Deque<BlockType> skipBlocks = new LinkedList<>();
        private Deque<SpanType> skipSpans = new LinkedList<>();

        /**
         * store all spans that will be rendered
         */
        private LinkedList<SpanType> passingSpans = new LinkedList<>();

        private String result = "";

        @Override
        public void endDocument() {
            String doc = writer.toString();

            doc = doc.replaceAll("(.)[*] +", "$1 ");

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

            // headings
            doc = Pattern.compile("([=]{2,4})[^\\n]*?\\1", Pattern.DOTALL).matcher(doc).replaceAll("");

            // references
            doc = Pattern.compile("<references>.*?</references>", Pattern.DOTALL).matcher(doc).replaceAll("");
            doc = Pattern.compile("<ref[^>/]*>.*?</ref>", Pattern.DOTALL).matcher(doc).replaceAll("");
            doc = Pattern.compile("<ref[^>]*>").matcher(doc).replaceAll("");
            doc = Pattern.compile("</ref[^>]*>").matcher(doc).replaceAll("");

            // empty/unknown inline tags and non inline tags
            doc = Pattern.compile("<([^ >]+)[^>]*>(.*?)</\\1>").matcher(doc).replaceAll("$2");
            doc = Pattern.compile("<([^ >]+)[^>]*/?>").matcher(doc).replaceAll(" ");

            // fix for undetected links
            doc = Pattern.compile("\\[\\[([^\\|]*)|([^\\]]*)]]").matcher(doc).replaceAll("$2");
            doc = Pattern.compile("\\[\\[[^\\[\\]]*]]").matcher(doc).replaceAll("");

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

        @Override
        public void beginBlock(BlockType type, Attributes attributes) {
            switch (type) {
            // passing blocks
            case PARAGRAPH:
            case DEFINITION_ITEM:
            case DEFINITION_TERM:
            case NUMERIC_LIST:
            case DEFINITION_LIST:
            case BULLETED_LIST:
                if (skipBlocks.size() > 0) {
                    skipBlocks.add(type);
                }
                break;
            // blocks that will be skipped
            case TIP:
            case WARNING:
            case INFORMATION:
            case NOTE:
            case PANEL:
            case FOOTNOTE:
            case QUOTE:
            case CODE:
            case LIST_ITEM:
            case TABLE:
            case TABLE_ROW:
            case TABLE_CELL_HEADER:
            case TABLE_CELL_NORMAL:
            case PREFORMATTED:
                skipBlocks.add(type);
                break;
            default:
                break;
            }
        }

        @Override
        public void endBlock() {
            if (!skipBlocks.isEmpty()) {
                skipBlocks.removeLast();
            } else {
                writer.append(" ");
            }
        }

        @Override
        public void beginSpan(SpanType type, Attributes attributes) {
            switch (type) {
            // passing spans
            case EMPHASIS:
            case ITALIC:
            case SPAN:
            case STRONG:
            case BOLD:
            case SUBSCRIPT:
            case SUPERSCRIPT:
            case UNDERLINED:
            case CITATION:
                if (skipSpans.size() > 0) {
                    skipSpans.add(type);
                } else {
                    passingSpans.add(type);
                }
                break;
            // span that will be skipped
            case INSERTED:
            case DELETED:
            case MONOSPACE:
            case CODE:
                skipSpans.add(type);
                break;
            default:
                break;
            }
        }

        @Override
        public void endSpan() {
            if (!skipSpans.isEmpty()) {
                skipSpans.removeLast();
            } else {
                passingSpans.removeLast();
            }
        }

        @Override
        public void beginHeading(int level, Attributes attributes) {
            skipSpans.add(SpanType.SPAN);
        }

        @Override
        public void endHeading() {
            if (!skipSpans.isEmpty()) {
                skipSpans.removeLast();
            }
        }

        @Override
        public void characters(String text) {
            if (skipBlocks.size() > 0) {
                return;
            }
            if (skipSpans.size() > 0) {
                return;
            }
            if (!passingSpans.isEmpty()) {
                SpanType type = passingSpans.getLast();
                switch (type) {
                case SUBSCRIPT:
                    text = "_" + text;
                    break;
                case SUPERSCRIPT:
                    text = "^" + text;
                    break;
                default:
                    break;
                }
            }
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
            // skip
            if (skipBlocks.size() > 0) {
                return;
            }
            if (skipSpans.size() > 0) {
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
            // in
            // parentheses.
            if (text.isEmpty()) {
                text = link.replaceAll("\\(.*?\\)", "");
            }

            // LINKS
            writer.append("[[" + text + "]]");
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

    }
}
