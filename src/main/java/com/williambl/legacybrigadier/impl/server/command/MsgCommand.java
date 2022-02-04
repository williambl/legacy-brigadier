package com.williambl.legacybrigadier.impl.server.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.williambl.legacybrigadier.api.argument.playerselector.TargetSelector;
import com.williambl.legacybrigadier.api.command.CommandProvider;
import com.williambl.legacybrigadier.api.command.ExtendedSender;
import com.williambl.legacybrigadier.impl.server.LegacyBrigadierServer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.packet.play.ChatMessagePacket;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.williambl.legacybrigadier.api.argument.playerselector.TargetSelectorArgumentType.getEntities;
import static com.williambl.legacybrigadier.api.argument.playerselector.TargetSelectorArgumentType.entities;
import static com.williambl.legacybrigadier.api.predicate.HasPermission.permission;

@Environment(EnvType.SERVER)
public class MsgCommand implements CommandProvider {

    @Override
    public LiteralArgumentBuilder<ExtendedSender> get() {
        return LiteralArgumentBuilder.<ExtendedSender>literal("msg")
                .requires(permission("command.msg"))
                .then(RequiredArgumentBuilder.<ExtendedSender, TargetSelector>argument("player", entities())
                                .then(RequiredArgumentBuilder.<ExtendedSender, String>argument("message", greedyString())
                                                .executes(this::whisper)
                                )
                );
    }

    public int whisper(CommandContext<ExtendedSender> context) {
        getEntities(context, "player").getEntityNames(context.getSource()).forEach(player -> {
            String message = "§7" + context.getSource().getName() + " whispers " + getString(context, "message");
            LegacyBrigadierServer.LOGGER.info(context.getSource().getName() + " whispers " + message + " to " + player);
            ((context.getSource())).getServer().playerManager.sendPacket(player, new ChatMessagePacket(message));
        });
        return 0;
    }
}
