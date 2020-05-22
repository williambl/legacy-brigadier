package com.williambl.legacybrigadier.impl.server.utils;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.williambl.legacybrigadier.api.argument.coordinate.Coordinate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.SERVER)
public final class StringReaderUtils {

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

    public static Coordinate.CoordinatePart readCoordinatePart(StringReader reader) throws CommandSyntaxException {
        boolean isRelative = false;
        if (reader.peek() == '~') {
            isRelative = true;
            reader.skip();
            if (reader.peek() == ' ')
                return new Coordinate.CoordinatePart(0, true);
        }
        int coordinate = reader.readInt();

        return new Coordinate.CoordinatePart(coordinate, isRelative);
    }
}
