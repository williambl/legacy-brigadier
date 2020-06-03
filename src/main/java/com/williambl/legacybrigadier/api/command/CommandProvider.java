package com.williambl.legacybrigadier.api.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.williambl.legacybrigadier.impl.server.LegacyBrigadierServer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.command.CommandSource;

import java.util.function.Supplier;

@Environment(EnvType.SERVER)
public interface CommandProvider extends Supplier<LiteralArgumentBuilder<CommandSource>> {

    default void sendFeedbackAndLog(CommandSource source, String message) {
        source.sendFeedback(message);
        LegacyBrigadierServer.LOGGER.info(source.getName() + ": " + message);
    }
}
