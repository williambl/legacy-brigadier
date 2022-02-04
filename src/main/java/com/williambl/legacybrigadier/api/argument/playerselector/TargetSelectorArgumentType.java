package com.williambl.legacybrigadier.api.argument.playerselector;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.williambl.legacybrigadier.api.command.ExtendedSender;
import com.williambl.legacybrigadier.impl.server.utils.StringReaderUtils;
import com.williambl.legacybrigadier.impl.server.utils.UncheckedCaster;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.Player;
import net.minecraft.server.command.CommandSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.SERVER)
public class TargetSelectorArgumentType implements ArgumentType<TargetSelector> {
    private static final Collection<String> EXAMPLES = Arrays.asList("@a", "Notch", "@p");

    private static final SimpleCommandExceptionType NOT_VALID_ID = new SimpleCommandExceptionType(new LiteralMessage("Invalid Player"));

    @SuppressWarnings("unchecked")
    private static List<String> getValidValues(CommandContext<CommandSource> context) {
        List<String> validValues = new ArrayList<>();
        List<Player> players = ((ExtendedSender)context.getSource()).getWorld().players;
        players.forEach(it -> validValues.add(it.name));
        validValues.add("@a");
        validValues.add("@p");
        validValues.add("@e");
        return validValues;
    }

    public static TargetSelectorArgumentType entities() {
        return new TargetSelectorArgumentType();
    }

    public static TargetSelector getEntities(final CommandContext<?> context, final String name) {
        return context.getArgument(name, TargetSelector.class);
    }

    @Override
    public TargetSelector parse(StringReader reader) {
        String selector = StringReaderUtils.readTargetSelector(reader);
        return new TargetSelector(selector);
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
