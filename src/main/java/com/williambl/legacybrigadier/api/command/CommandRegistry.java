package com.williambl.legacybrigadier.api.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.williambl.legacybrigadier.impl.server.LegacyBrigadierServer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.command.CommandSource;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.SERVER)
public final class CommandRegistry {

    private static final Map<CommandNode<CommandSource>, String> helpMap = new HashMap<>();

    /**
     * Build and register a command with help text.
     * @param command the {@link LiteralArgumentBuilder} of the command to build and register.
     * @param helpText the text that will be displayed in /help for your command.
     * @return the built {@link CommandNode}.
     */
    public static CommandNode<CommandSource> register(LiteralArgumentBuilder<CommandSource> command, String helpText) {
        CommandNode<CommandSource> result = register(command);
        helpMap.put(result, helpText);
        return result;
    }

    /**
     * Build and register a command without help text.
     * @param command the {@link LiteralArgumentBuilder} of the command to build and register.
     * @return the built {@link CommandNode}.
     */
    public static CommandNode<CommandSource> register(LiteralArgumentBuilder<CommandSource> command) {
        return LegacyBrigadierServer.dispatcher.register(command);
    }

    /**
     * Get help text for a given command.
     * @param command the {@link CommandNode} to find help text for.
     * @return the help text, or "No help available" if no help text has been registered.
     */
    @Nonnull
    public static String getHelp(CommandNode<CommandSource> command) {
        if (!helpMap.containsKey(command))
            return "No help available";
        return helpMap.get(command);
    }
}
