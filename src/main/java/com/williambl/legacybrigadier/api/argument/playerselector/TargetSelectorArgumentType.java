package com.williambl.legacybrigadier.api.argument.playerselector;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.williambl.legacybrigadier.api.command.ExtendedSender;
import com.williambl.legacybrigadier.impl.server.utils.StringReaderUtils;
import com.williambl.legacybrigadier.impl.server.utils.UncheckedCaster;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.Player;
import net.minecraft.server.command.CommandSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.SERVER)
public class TargetSelectorArgumentType implements ArgumentType<TargetSelector<?>> {
    private static final Collection<String> EXAMPLES = Arrays.asList("@a", "Notch", "@p");
    private static final SimpleCommandExceptionType NOT_A_PLAYER = new SimpleCommandExceptionType(new LiteralMessage("Selector is not player-only"));
    private static final SimpleCommandExceptionType NOT_SINGULAR = new SimpleCommandExceptionType(new LiteralMessage("Selector can select multiple targets"));

    private final boolean playersOnly;
    private final boolean singleOnly;

    public TargetSelectorArgumentType(boolean playersOnly, boolean singleOnly) {
        this.playersOnly = playersOnly;
        this.singleOnly = singleOnly;
    }

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
        return new TargetSelectorArgumentType(false, false);
    }

    public static TargetSelectorArgumentType players() {
        return new TargetSelectorArgumentType(true, false);
    }

    public static TargetSelectorArgumentType entity() {
        return new TargetSelectorArgumentType(false, true);
    }

    public static TargetSelectorArgumentType player() {
        return new TargetSelectorArgumentType(true, true);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entity> TargetSelector<T> getEntities(final CommandContext<?> context, final String name) {
        return context.getArgument(name, TargetSelector.class);
    }

    public static TargetSelector<Player> getPlayers(final CommandContext<?> context, final String name) {
        return getEntities(context, name);
    }

    @Override
    public TargetSelector<?> parse(StringReader reader) throws CommandSyntaxException {
        TargetSelector<?> res = StringReaderUtils.readTargetSelector(reader);

        if (this.playersOnly && !res.isPlayerOnly()) {
            throw NOT_A_PLAYER.create();
        }

        if (this.singleOnly && !res.isSingleOnly()) {
            throw NOT_SINGULAR.create();
        }

        return res;
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
