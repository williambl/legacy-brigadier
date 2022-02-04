package com.williambl.legacybrigadier.api.argument.coordinate;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.williambl.legacybrigadier.impl.server.utils.StringReaderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.SERVER)
public class CoordinateArgumentType implements ArgumentType<Coordinate> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "~ ~ ~", "1 2 3");
    public static final SimpleCommandExceptionType INCOMPLETE_EXCEPTION = new SimpleCommandExceptionType(new LiteralMessage("Incomplete position"));
    public static final SimpleCommandExceptionType LOCALITY_ERROR = new SimpleCommandExceptionType(new LiteralMessage("Illegal mixing of local (^) and non-local coordinates"));

    private final boolean isIntOnly;

    private CoordinateArgumentType(boolean isIntOnly) {
        this.isIntOnly = isIntOnly;
    }

    /**
     * Create a new Coordinate argument.
     * @return a CoordinateArgumentType instance.
     */
    @Nonnull
    public static CoordinateArgumentType coordinate() {
        return new CoordinateArgumentType(false);
    }

    /**
     * Create a new Coordinate argument with integer-only precision.
     * @return a CoordinateArgumentType instance.
     */
    @Nonnull
    public static CoordinateArgumentType intCoordinate() {
        return new CoordinateArgumentType(true);
    }

    /**
     * Get the coordinate from an argument.
     * @param context the command context.
     * @param name the argument name.
     * @return the {@link Coordinate}
     */
    public static Coordinate getCoordinate(final CommandContext<?> context, final String name) {
        return context.getArgument(name, Coordinate.class);
    }

    @Override
    public Coordinate parse(StringReader stringReader) throws CommandSyntaxException {
        final int cursor = stringReader.getCursor();
        final Coordinate.CoordinatePart x = StringReaderUtils.readCoordinatePart(stringReader, this.isIntOnly);

        if (stringReader.canRead() && stringReader.peek() == ' ') {
            stringReader.skip();
            final Coordinate.CoordinatePart y = StringReaderUtils.readCoordinatePart(stringReader, this.isIntOnly);

            if (stringReader.canRead() && stringReader.peek() == ' ') {
                stringReader.skip();
                final Coordinate.CoordinatePart z = StringReaderUtils.readCoordinatePart(stringReader, this.isIntOnly);

                if (!Coordinate.CoordinatePart.allMatchLocality(x, y, z)) {
                    stringReader.setCursor(cursor);
                    throw LOCALITY_ERROR.createWithContext(stringReader);
                }

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


    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String string = builder.getRemaining();
        if (!string.isEmpty() && string.charAt(0) == '^') {
            builder.suggest("^ ^ ^");
        } else {
            builder.suggest("~ ~ ~");
        }
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}

