package com.williambl.legacybrigadier.api.argument.enumarg;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.williambl.legacybrigadier.impl.server.utils.UncheckedCaster;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.command.CommandSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.SERVER)
public class EnumArgumentType<T extends Enum<T>> implements ArgumentType<T> {

    private static final Collection<String> EXAMPLES = Arrays.asList("", "", "");

    private static final SimpleCommandExceptionType NOT_VALID_VALUE = new SimpleCommandExceptionType(new LiteralMessage("Invalid Value"));

    private final T[] elements;

    public EnumArgumentType(Class<T> theClazz) {
        this.elements = theClazz.getEnumConstants();
    }

    private List<String> getValidValues(CommandContext<CommandSource> context) {
        List<String> validValues = new ArrayList<>();
        for (T element : elements) {
            validValues.add(element.name());
        }
        return validValues;
    }

    public static <T extends Enum<T>>EnumArgumentType<T> enumArg(Class<T> clazz) {
        return new EnumArgumentType<>(clazz);
    }

    public static <T> T getValue(final CommandContext<?> context, final String name, Class<T> clazz) {
        return context.getArgument(name, clazz);
    }

    @Override
    public T parse(StringReader reader) throws CommandSyntaxException {
        String string = reader.readUnquotedString();
        for (T element : elements) {
            if (element.name().equals(string))
                return element;
        }
        throw NOT_VALID_VALUE.createWithContext(reader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (String validValue : getValidValues(UncheckedCaster.context(context))) {
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
