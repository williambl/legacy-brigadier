package com.williambl.legacybrigadier.impl.server.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.williambl.legacybrigadier.api.argument.playerselector.PlayerSelector;
import com.williambl.legacybrigadier.impl.server.LegacyBrigadierServer;
import com.williambl.legacybrigadier.impl.server.mixinhooks.CommandSourceHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.packet.play.SendChatMessageS2C;
import net.minecraft.server.command.CommandSource;

import java.util.function.Supplier;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.williambl.legacybrigadier.api.argument.playerselector.PlayerSelectorArgumentType.getPlayer;
import static com.williambl.legacybrigadier.api.argument.playerselector.PlayerSelectorArgumentType.player;
import static com.williambl.legacybrigadier.api.predicate.HasPermission.permission;

@Environment(EnvType.SERVER)
public class MsgCommand implements Supplier<LiteralArgumentBuilder<CommandSource>> {

    @Override
    public LiteralArgumentBuilder<CommandSource> get() {
        return LiteralArgumentBuilder.<CommandSource>literal("msg")
                .requires(permission("command.msg"))
                .then(RequiredArgumentBuilder.<CommandSource, PlayerSelector>argument("player", player())
                                .then(RequiredArgumentBuilder.<CommandSource, String>argument("message", greedyString())
                                                .executes(this::whisper)
                                )
                );
    }

    public int whisper(CommandContext<CommandSource> context) {
        getPlayer(context, "player").getPlayerNames(context.getSource()).forEach(player -> {
            String message = "§7" + context.getSource().getName() + " whispers " + getString(context, "message");
            LegacyBrigadierServer.LOGGER.info(message + " to " + player);
            ((CommandSourceHooks)(context.getSource())).getServer().field_2842.method_562(player, new SendChatMessageS2C(message));
        });
        return 0;
    }
}
