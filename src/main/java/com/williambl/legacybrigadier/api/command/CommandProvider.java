package com.williambl.legacybrigadier.api.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.williambl.legacybrigadier.impl.server.LegacyBrigadierServer;
import com.williambl.legacybrigadier.impl.server.mixinhooks.CommandSourceHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.packet.play.SendChatMessageC2S;
import net.minecraft.server.command.CommandSource;

import java.util.function.Supplier;

@Environment(EnvType.SERVER)
public interface CommandProvider extends Supplier<LiteralArgumentBuilder<CommandSource>> {

    default void sendFeedbackAndLog(CommandSource source, String message) {
        source.sendFeedback(message);
        LegacyBrigadierServer.LOGGER.info(source.getName() + ": " + message);
    }

    default void sendToChatAndLog(CommandSource source, String message) {
        ((CommandSourceHooks)source).getServer().field_2842.method_559(new SendChatMessageC2S(message));
        LegacyBrigadierServer.LOGGER.info(message);
    }

    default void sendToPlayerAndLog(CommandSource source, String playerName, String message) {
        ((CommandSourceHooks)source).getServer().field_2842.method_562(playerName, new SendChatMessageC2S(message));
        LegacyBrigadierServer.LOGGER.info(message);
    }
}
