package com.williambl.legacybrigadier.impl.server.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.williambl.legacybrigadier.api.command.CommandProvider;
import com.williambl.legacybrigadier.api.command.ExtendedSender;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.williambl.legacybrigadier.api.predicate.HasPermission.permission;

@Environment(EnvType.SERVER)
public class MeCommand implements CommandProvider {

    @Override
    public LiteralArgumentBuilder<ExtendedSender> get() {
        return LiteralArgumentBuilder.<ExtendedSender>literal("me")
                .requires(permission("command.me"))
                .then(RequiredArgumentBuilder.<ExtendedSender, String>argument("message", greedyString())
                                .executes(this::sendMeMessage)
                );
    }

    public int sendMeMessage(CommandContext<ExtendedSender> context) {
        String message = "* " + context.getSource().getName() + " " + getString(context, "message").trim();
        sendToChatAndLog(context.getSource(), message);
        return 0;
    }
}
