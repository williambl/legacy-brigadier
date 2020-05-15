package com.williambl.legacybrigadier.server;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_38;

@Environment(EnvType.SERVER)
public class CommandManager {
    public static void handleCommand(class_38 commandInfo) {
        try {
            LegacyBrigadierServer.dispatcher.execute(commandInfo.field_159, commandInfo.field_160);
        } catch (CommandSyntaxException e) {
            commandInfo.field_160.method_1409(e.getMessage());
        }
    }
}
