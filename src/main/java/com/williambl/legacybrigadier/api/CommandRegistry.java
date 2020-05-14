package com.williambl.legacybrigadier.api;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.williambl.legacybrigadier.LegacyBrigadierServer;
import net.minecraft.class_39;

import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {

    private static Map<CommandNode<class_39>, String> helpMap = new HashMap<>();

    public static CommandNode<class_39> register(LiteralArgumentBuilder<class_39> command, String helpText) {
        CommandNode<class_39> result = register(command);
        helpMap.put(result, helpText);
        return result;
    }

    public static CommandNode<class_39> register(LiteralArgumentBuilder<class_39> command) {
        return LegacyBrigadierServer.dispatcher.register(command);
    }

    public static String getHelp(CommandNode<class_39> command) {
        if (!helpMap.containsKey(command))
            return "No help available";
        return helpMap.get(command);
    }
}
