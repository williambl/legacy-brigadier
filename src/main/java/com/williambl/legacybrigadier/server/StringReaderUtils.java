package com.williambl.legacybrigadier.server;

import com.mojang.brigadier.StringReader;

public class StringReaderUtils {

    private static boolean isAllowedInTargetSelector(final char c) {
        return StringReader.isAllowedInUnquotedString(c) || c == '@';
    }

    private static boolean isAllowedInId(final char c) {
        return StringReader.isAllowedInUnquotedString(c) || c == ':';
    }

    public static String readTargetSelector(StringReader reader) {
        final int start = reader.getCursor();
        while (reader.canRead() && isAllowedInTargetSelector(reader.peek())) {
            reader.skip();
        }
        return reader.getString().substring(start, reader.getCursor());
    }

    public static String readId(StringReader reader) {
        final int start = reader.getCursor();
        while (reader.canRead() && isAllowedInId(reader.peek())) {
            reader.skip();
        }
        return reader.getString().substring(start, reader.getCursor());
    }
}
