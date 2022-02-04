package com.williambl.legacybrigadier.api.argument.entityid;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.williambl.legacybrigadier.impl.server.utils.StringReaderUtils;
import io.github.minecraftcursedlegacy.api.registry.Id;
import io.github.minecraftcursedlegacy.api.registry.Registries;
import io.github.minecraftcursedlegacy.impl.registry.EntityType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.SERVER)
public class EntityTypeArgumentType implements ArgumentType<EntityType> {

    private static final Collection<String> EXAMPLES = Arrays.asList("Pig", "Creeper", "Spider");

    private static final SimpleCommandExceptionType NOT_VALID_ID = new SimpleCommandExceptionType(new LiteralMessage("Invalid Entity ID"));

    private static List<Id> validValues;

    private static List<Id> getValidValues() {
        if (validValues != null)
            return validValues;
        validValues = new ArrayList<>();
        validValues.addAll(Registries.ENTITY_TYPE.ids());
        return validValues;
    }

    public static EntityTypeArgumentType entityType() {
        return new EntityTypeArgumentType();
    }

    public static EntityType getEntityType(final CommandContext<?> context, final String name) {
        return context.getArgument(name, EntityType.class);
    }

    @Override
    public EntityType parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        Id id = new Id(StringReaderUtils.readId(reader));
        if (!getValidValues().contains(id)) {
            reader.setCursor(cursor);
            throw NOT_VALID_ID.createWithContext(reader);
        }
        return Registries.ENTITY_TYPE.getById(id);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (Id validValue : getValidValues()) {
            if (validValue.toString().startsWith(builder.getRemaining()))
                builder.suggest(validValue.toString());
        }
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
