package com.williambl.legacybrigadier.server.api.argument;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.williambl.legacybrigadier.server.LegacyBrigadierServer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.SERVER)
public class EntityIdArgumentType implements ArgumentType<EntityId> {

    private static final Collection<String> EXAMPLES = Arrays.asList("Pig", "Creeper", "Spider");

    private static final SimpleCommandExceptionType NOT_VALID_ID = new SimpleCommandExceptionType(new LiteralMessage("Invalid Entity ID"));

    private static List<String> validValues;

    @SuppressWarnings("unchecked")
    private static List<String> getValidValues() {
        if (validValues != null)
            return validValues;
        Map<String, Class<?>> entityMap;
        try {
            entityMap = (Map<String, Class<?>>) LegacyBrigadierServer.ENTITY_MAP.get(null);
        } catch (IllegalAccessException | ClassCastException e) {
            System.out.println("Couldn't access entity string ID map :concern:");
            entityMap = new HashMap<>();
        }
        validValues = new ArrayList<>();
        entityMap.forEach((id, clazz) ->
                validValues.add(id)
        );
        return validValues;
    }

    public static EntityIdArgumentType entityId() {
        return new EntityIdArgumentType();
    }

    public static EntityId getEntityId(final CommandContext<?> context, final String name) {
        return context.getArgument(name, EntityId.class);
    }

    @Override
    public EntityId parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        String id = reader.readUnquotedString();
        if (!validValues.contains(id)) {
            reader.setCursor(cursor);
            throw NOT_VALID_ID.createWithContext(reader);
        }
        return new EntityId(id);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (String validValue : getValidValues()) {
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
