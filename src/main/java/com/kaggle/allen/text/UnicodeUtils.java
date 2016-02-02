package com.kaggle.allen.text;

import java.util.Map;

import org.apache.commons.lang3.CharUtils;

import com.google.common.collect.ImmutableMap;

/**
 * @author agrigorev
 */
public class UnicodeUtils {

    private static Map<Integer, String> UNICODE_NORMALIZATION_MAP = buildUnicodeNormalizationMap();

    public static String normalizeUnicode(String input) {
        StringBuilder result = new StringBuilder(input.length());

        input.codePoints().forEach(code -> {
            if (UNICODE_NORMALIZATION_MAP.containsKey(code)) {
                result.append(UNICODE_NORMALIZATION_MAP.get(code));
            } else {
                result.append(codePointToString(code));
            }
        });
        return result.toString();
    }

    private static String codePointToString(int codePoint) {
        if (Character.isBmpCodePoint(codePoint)) {
            return CharUtils.toString((char) codePoint);
        }

        char[] chars = Character.toChars(codePoint);
        return new String(chars);
    }
    
    private static Map<Integer, String> buildUnicodeNormalizationMap() {
        // see
        // http://lexsrv3.nlm.nih.gov/LexSysGroup/Projects/lvg/current/docs/designDoc/UDF/unicode/DefaultTables/symbolTable.html
        // and http://stackoverflow.com/a/17280168/861423
        ImmutableMap.Builder<Integer, String> builder = ImmutableMap.builder();

        // punctuation normalization
        builder.put(0x00A0, " ");
        builder.put(0x00AB, "\"");
        builder.put(0x00AD, "-");
        builder.put(0x00B4, "'");
        builder.put(0x00BB, "\"");
        builder.put(0x00F7, "/");
        builder.put(0x01C0, "|");
        builder.put(0x01C3, "!");
        builder.put(0x02B9, "'");
        builder.put(0x02BA, "\"");
        builder.put(0x02BC, "'");
        builder.put(0x02C4, "^");
        builder.put(0x02C6, "^");
        builder.put(0x02C8, "'");
        builder.put(0x02CB, "`");
        builder.put(0x02CD, "_");
        builder.put(0x02DC, "~");
        builder.put(0x0300, "`");
        builder.put(0x0301, "'");
        builder.put(0x0302, "^");
        builder.put(0x0303, "~");
        builder.put(0x030B, "\"");
        builder.put(0x030E, "\"");
        builder.put(0x0331, "_");
        builder.put(0x0332, "_");
        builder.put(0x0338, "/");
        builder.put(0x0589, ":");
        builder.put(0x05C0, "|");
        builder.put(0x05C3, ":");
        builder.put(0x066A, "%");
        builder.put(0x066D, "*");
        builder.put(0x200B, " ");
        builder.put(0x2010, "-");
        builder.put(0x2011, "-");
        builder.put(0x2012, "-");
        builder.put(0x2013, "-");
        builder.put(0x2014, "-");
        builder.put(0x2015, "-");
        builder.put(0x2016, "|");
        builder.put(0x2017, "_");
        builder.put(0x2018, "'");
        builder.put(0x2019, "'");
        builder.put(0x201A, ",");
        builder.put(0x201B, "'");
        builder.put(0x201C, "\"");
        builder.put(0x201D, "\"");
        builder.put(0x201E, "\"");
        builder.put(0x201F, "\"");
        builder.put(0x2026, "...");
        builder.put(0x2032, "'");
        builder.put(0x2033, "\"");
        builder.put(0x2034, "'");
        builder.put(0x2035, "`");
        builder.put(0x2036, "\"");
        builder.put(0x2037, "'");
        builder.put(0x2038, "^");
        builder.put(0x2039, "<");
        builder.put(0x203A, ">");
        builder.put(0x203D, "?");
        builder.put(0x2044, "/");
        builder.put(0x204E, "*");
        builder.put(0x2052, "%");
        builder.put(0x2053, "~");
        builder.put(0x2060, " ");
        builder.put(0x20E5, "\\");
        builder.put(0x2212, "-");
        builder.put(0x2215, "/");
        builder.put(0x2216, "\\");
        builder.put(0x2217, "*");
        builder.put(0x2223, "|");
        builder.put(0x2236, ":");
        builder.put(0x223C, "~");
        builder.put(0x2264, "<");
        builder.put(0x2265, ">");
        builder.put(0x2266, "<");
        builder.put(0x2267, ">");
        builder.put(0x2303, "^");
        builder.put(0x2329, "<");
        builder.put(0x232A, ">");
        builder.put(0x266F, "#");
        builder.put(0x2731, "*");
        builder.put(0x2758, "|");
        builder.put(0x2762, "!");
        builder.put(0x27E6, "[");
        builder.put(0x27E8, "<");
        builder.put(0x27E9, ">");
        builder.put(0x2983, "{");
        builder.put(0x2984, "}");
        builder.put(0x3003, "\"");
        builder.put(0x3008, "<");
        builder.put(0x3009, ">");
        builder.put(0x301B, "]");
        builder.put(0x301C, "~");
        builder.put(0x301D, "\"");
        builder.put(0x301E, "\"");
        builder.put(0xE100, "!");
        builder.put(0xFEFF, " ");

        return builder.build();
    }
}
