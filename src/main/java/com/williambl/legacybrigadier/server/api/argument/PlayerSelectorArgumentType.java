package com.williambl.legacybrigadier.server.api.argument;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.williambl.legacybrigadier.server.mixinhooks.CommandSourceHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_39;
import net.minecraft.entity.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.SERVER)
public class PlayerSelectorArgumentType implements ArgumentType<PlayerSelector> {

    private static final Collection<String> EXAMPLES = Arrays.asList("@a", "Notch", "@p");

    private static final SimpleCommandExceptionType NOT_VALID_ID = new SimpleCommandExceptionType(new LiteralMessage("Invalid Player"));

    @SuppressWarnings("unchecked")
    private static List<String> getValidValues(CommandContext<class_39> context) {
        List<String> validValues = new ArrayList<>();
        List<Player> players = ((CommandSourceHooks)context.getSource()).getWorld().players;
        players.forEach(it -> validValues.add(it.name));
        validValues.add("@a");
        validValues.add("@p");
        return validValues;
    }

    public static PlayerSelectorArgumentType player() {
        return new PlayerSelectorArgumentType();
    }

    public static PlayerSelector getPlayer(final CommandContext<?> context, final String name) {
        return context.getArgument(name, PlayerSelector.class);
    }

    @Override
    public PlayerSelector parse(StringReader reader) throws CommandSyntaxException {
        char first = reader.read(); //evil hack to force it to recognise '@' as a valid character
        String selector = first + reader.readUnquotedString();
        System.out.println(selector);
        return new PlayerSelector(selector);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (String validValue : getValidValues((CommandContext<class_39>) context)) {
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
