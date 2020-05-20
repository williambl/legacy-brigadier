package com.williambl.legacybrigadier.server.api.argument;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.williambl.legacybrigadier.server.StringReaderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.SERVER)
public class CoordinateArgumentType implements ArgumentType<Coordinate> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "~ ~ ~", "1 2 3");
    public static final SimpleCommandExceptionType INCOMPLETE_EXCEPTION = new SimpleCommandExceptionType(new LiteralMessage("Incomplete position"));

    public static CoordinateArgumentType coordinate() {
        return new CoordinateArgumentType();
    }

    public static Coordinate getCoordinate(final CommandContext<?> context, final String name) {
        return context.getArgument(name, Coordinate.class);
    }

    public Coordinate parse(StringReader stringReader) throws CommandSyntaxException {
        int cursor = stringReader.getCursor();
        Coordinate.CoordinatePart x = StringReaderUtils.readCoordinatePart(stringReader);
        if (stringReader.canRead() && stringReader.peek() == ' ') {
            stringReader.skip();
            Coordinate.CoordinatePart y = StringReaderUtils.readCoordinatePart(stringReader);
            if (stringReader.canRead() && stringReader.peek() == ' ') {
                stringReader.skip();
                Coordinate.CoordinatePart z = StringReaderUtils.readCoordinatePart(stringReader);
                return new Coordinate(x, y, z);
            } else {
                stringReader.setCursor(cursor);
                throw INCOMPLETE_EXCEPTION.createWithContext(stringReader);
            }
        } else {
            stringReader.setCursor(cursor);
            throw INCOMPLETE_EXCEPTION.createWithContext(stringReader);
        }
    }


    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        builder.suggest("~ ~ ~");
        return builder.buildFuture();
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}

