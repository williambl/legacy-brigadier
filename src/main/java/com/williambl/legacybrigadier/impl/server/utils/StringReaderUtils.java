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

    private static boolean isAllowedInPermissionNode(final char c) {
        return StringReader.isAllowedInUnquotedString(c) || c == '*';
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

    public static String readPermissionNode(StringReader reader) {
        final int start = reader.getCursor();
        while (reader.canRead() && isAllowedInPermissionNode(reader.peek())) {
            reader.skip();
        }
        return reader.getString().substring(start, reader.getCursor());
    }

    public static Coordinate.CoordinatePart readCoordinatePart(StringReader reader, boolean isIntOnly) throws CommandSyntaxException {
        Coordinate.CoordinateType type = Coordinate.CoordinateType.ABSOLUTE;
        if (reader.peek() == '~') {
            type = Coordinate.CoordinateType.RELATIVE;
            reader.skip();
            if (reader.peek() == ' ' || !reader.canRead())
                return new Coordinate.CoordinatePart(0, type);
        } else if (reader.peek() == '^') {
            type = Coordinate.CoordinateType.LOCAL;
            reader.skip();
            if (reader.peek() == ' ' || !reader.canRead())
                return new Coordinate.CoordinatePart(0, type);
        }

        double coordinate = isIntOnly ? reader.readInt() : reader.readDouble();

        return new Coordinate.CoordinatePart(coordinate, type);
    }
}
