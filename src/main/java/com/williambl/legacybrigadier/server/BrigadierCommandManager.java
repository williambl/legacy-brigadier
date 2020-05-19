package com.williambl.legacybrigadier.server;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.command.Command;

@Environment(EnvType.SERVER)
public final class BrigadierCommandManager {
    public static void handleCommand(Command command) {
        try {
            LegacyBrigadierServer.dispatcher.execute(command.commandString, command.source);
        } catch (CommandSyntaxException e) {
            command.source.sendFeedback(e.getMessage());
        }
    }
}
