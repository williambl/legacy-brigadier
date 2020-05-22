package com.williambl.legacybrigadier.impl.server.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.williambl.legacybrigadier.impl.server.LegacyBrigadierServer;
import com.williambl.legacybrigadier.impl.server.mixinhooks.CommandSourceHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.packet.play.SendChatMessageC2S;
import net.minecraft.server.command.CommandSource;

import java.util.function.Supplier;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.williambl.legacybrigadier.api.predicate.HasPermission.permission;

@Environment(EnvType.SERVER)
public class MeCommand implements Supplier<LiteralArgumentBuilder<CommandSource>> {

    @Override
    public LiteralArgumentBuilder<CommandSource> get() {
        return LiteralArgumentBuilder.<CommandSource>literal("me")
                .requires(permission("command.me"))
                .then(RequiredArgumentBuilder.<CommandSource, String>argument("message", greedyString())
                                .executes(this::sendMeMessage)
                );
    }

    public int sendMeMessage(CommandContext<CommandSource> context) {
        String message = "* " + context.getSource().getName() + " " + getString(context, "message").trim();
        LegacyBrigadierServer.LOGGER.info(message);
        ((CommandSourceHooks)context.getSource()).getServer().field_2842.method_559(new SendChatMessageC2S(message));
        return 0;
    }
}
