package com.williambl.legacybrigadier.server;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.williambl.legacybrigadier.server.api.argument.Coordinate;
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

    private static boolean isAllowedInCoordinate(final char c) {
        return (c >= '0' && c <= '9') || c == '~';
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
        final int start = reader.getCursor();
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
