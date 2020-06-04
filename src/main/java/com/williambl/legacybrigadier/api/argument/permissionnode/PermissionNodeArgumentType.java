package com.williambl.legacybrigadier.api.argument.permissionnode;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.williambl.legacybrigadier.api.permission.PermissionNode;
import com.williambl.legacybrigadier.impl.server.utils.StringReaderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.SERVER)
public class PermissionNodeArgumentType implements ArgumentType<PermissionNode> {

    private static final Collection<String> EXAMPLES = Arrays.asList("minecraft.operator", "*", "command.permissions");

    public static PermissionNodeArgumentType permissionNode() {
        return new PermissionNodeArgumentType();
    }

    public static PermissionNode getPermissionNode(final CommandContext<?> context, final String name) {
        return context.getArgument(name, PermissionNode.class);
    }

    @Override
    public PermissionNode parse(StringReader reader) throws CommandSyntaxException {
        String id = StringReaderUtils.readPermissionNode(reader);
        return new PermissionNode(id);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if ("minecraft.operator".startsWith(builder.getRemaining()))
            builder.suggest("minecraft.operator");

        if (builder.getRemaining().endsWith("."))
            builder.suggest(builder.getRemaining() + "*");
        else
            builder.suggest(builder.getRemaining() + ".");

        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

}
