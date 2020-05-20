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
import io.github.minecraftcursedlegacy.api.registry.Id;
import io.github.minecraftcursedlegacy.api.registry.Registries;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Environment(EnvType.SERVER)
public class TileIdArgumentType implements ArgumentType<TileId> {

    private static final Collection<String> EXAMPLES = Arrays.asList("0", "2", "32");

    private static final SimpleCommandExceptionType NOT_VALID_ID = new SimpleCommandExceptionType(new LiteralMessage("Invalid Tile ID"));

    private static Set<String> validValues;
    private static Map<Integer, String> intId2Id;

    private static Set<String> getValidValues() {
        if (validValues != null)
            return validValues;

        Set<Id> ids = Registries.TILE.ids();
        validValues = ids.stream().map(Id::toString).collect(Collectors.toSet());

        return validValues;
    }

    private static Map<Integer, String> getIntId2Id() {
        if (intId2Id != null)
            return intId2Id;
        Set<Integer> intIds = Registries.TILE.serialisedIds();
        intId2Id = intIds.stream().collect(Collectors.toMap(integer -> integer, integer -> {
            Id id = Registries.TILE.getId(Registries.TILE.getBySerialisedId(integer));
            if (id == null)
                return "";
            return id.toString();
        }));
        return intId2Id;
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
        String id = StringReaderUtils.readId(reader);
        if (!getValidValues().contains(id)) {
            reader.setCursor(cursor);
            throw NOT_VALID_ID.createWithContext(reader);
        }
        return new TileId(id);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (String validValue : getValidValues()) {
            if (validValue.startsWith(builder.getRemaining()) || validValue.substring(validValue.indexOf(':')+1, validValue.length()-1).startsWith(builder.getRemaining()))
                builder.suggest(validValue);
        }
        getIntId2Id().forEach((integer, id) -> {
            if (integer.toString().startsWith(builder.getRemaining()))
                builder.suggest(id); //Only ever suggest string ids
        });
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
