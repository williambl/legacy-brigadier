package com.williambl.legacybrigadier.server.api.argument;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.tile.Tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TileIdArgumentType implements ArgumentType<TileId> {

    private static final Collection<String> EXAMPLES = Arrays.asList("0", "2", "32");

    private static final SimpleCommandExceptionType NOT_VALID_ID = new SimpleCommandExceptionType(new LiteralMessage("Invalid Tile ID"));

    private static String[] validStringValues;
    private static List<Integer> validValues;

    private static String[] getValidStringValues() {
        if (validStringValues != null)
            return validStringValues;
        List<Integer> validIntValues = getValidValues();
        validStringValues = new String[validIntValues.size()];
        for (int i = 0; i < validIntValues.size(); i++) {
            validStringValues[i] = validIntValues.get(i).toString();
        }
        return validStringValues;
    }

    private static List<Integer> getValidValues() {
        if (validValues != null)
            return validValues;
        validValues = new ArrayList<>();
        for (int i = 0; i < Tile.BY_ID.length; i++) {
            if (Tile.BY_ID[i] != null)
                validValues.add(i);
        }
        return validValues;
    }

    private boolean isValueValid(int value) {
        for (Integer validValue : getValidValues()) {
            if (validValue == value)
                return true;
        }
        return false;
    }

    public static TileIdArgumentType tileId() {
        return new TileIdArgumentType();
    }

    public static TileId getTileId(final CommandContext<?> context, final String name) {
        return context.getArgument(name, TileId.class);
    }

    @Override
    public TileId parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        int id = reader.readInt();
        if (!isValueValid(id)) {
            reader.setCursor(cursor);
            throw NOT_VALID_ID.createWithContext(reader);
        }
        return new TileId(id);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (String validValue : getValidStringValues()) {
            if (validValue.startsWith(builder.getRemaining()))
                builder.suggest(validValue);
        }
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
