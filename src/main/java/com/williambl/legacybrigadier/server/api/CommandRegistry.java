package com.williambl.legacybrigadier.server.api;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.williambl.legacybrigadier.server.LegacyBrigadierServer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.command.CommandSource;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.SERVER)
public class CommandRegistry {

    private static Map<CommandNode<CommandSource>, String> helpMap = new HashMap<>();

    public static CommandNode<CommandSource> register(LiteralArgumentBuilder<CommandSource> command, String helpText) {
        CommandNode<CommandSource> result = register(command);
        helpMap.put(result, helpText);
        return result;
    }

    public static CommandNode<CommandSource> register(LiteralArgumentBuilder<CommandSource> command) {
        return LegacyBrigadierServer.dispatcher.register(command);
    }

    public static String getHelp(CommandNode<CommandSource> command) {
        if (!helpMap.containsKey(command))
            return "No help available";
        return helpMap.get(command);
    }
}
