package com.williambl.legacybrigadier.impl.server.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.williambl.legacybrigadier.api.command.CommandProvider;
import com.williambl.legacybrigadier.api.command.CommandRegistry;
import com.williambl.legacybrigadier.api.command.ExtendedSender;
import com.williambl.legacybrigadier.impl.server.LegacyBrigadierServer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import static com.williambl.legacybrigadier.api.predicate.HasPermission.permission;

@Environment(EnvType.SERVER)
public class HelpCommand implements CommandProvider {

    @Override
    public LiteralArgumentBuilder<ExtendedSender> get() {
        return LiteralArgumentBuilder.<ExtendedSender>literal("help")
                .requires(permission("command.help"))
                .executes(this::showHelp);
    }

    public int showHelp(CommandContext<ExtendedSender> context) {
        LegacyBrigadierServer.dispatcher
                .getSmartUsage(LegacyBrigadierServer.dispatcher.getRoot(), context.getSource())
                .forEach((cmd, usage) -> context.getSource().sendCommandFeedback(
                        alignHelp(usage, CommandRegistry.getHelp(cmd))
                ));
        return 0;
    }

    private static String alignHelp(final String usage, final String helpMessage) {
        StringBuilder builder = new StringBuilder("   ");
        builder.append(usage);
        int charsUntilHelp = 29; //same as vanilla
        final int spaces = Math.max(1, charsUntilHelp - builder.length());
        for (int i = 0; i < spaces; i++) {
            builder.append(' ');
        }
        builder.append(helpMessage);
        return builder.toString();
    }
}
