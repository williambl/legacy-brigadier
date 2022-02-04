package com.williambl.legacybrigadier.api.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.williambl.legacybrigadier.impl.server.LegacyBrigadierServer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.packet.play.ChatMessagePacket;

import java.util.function.Supplier;

@Environment(EnvType.SERVER)
public interface CommandProvider extends Supplier<LiteralArgumentBuilder<ExtendedSender>> {

    default void sendFeedbackAndLog(ExtendedSender source, String message) {
        source.sendCommandFeedback(message);
        LegacyBrigadierServer.LOGGER.info(source.getName() + ": " + message);
    }

    default void sendToChatAndLog(ExtendedSender source, String message) {
        source.getServer().playerManager.sendPacketToAll(new ChatMessagePacket(message));
        LegacyBrigadierServer.LOGGER.info(message);
    }

    default void sendToPlayerAndLog(ExtendedSender source, String playerName, String message) {
        source.getServer().playerManager.sendPacket(playerName, new ChatMessagePacket(message));
        LegacyBrigadierServer.LOGGER.info(message);
    }
}
