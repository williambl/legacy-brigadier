package com.williambl.legacybrigadier.impl.server.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.williambl.legacybrigadier.api.command.CommandRegistry;
import com.williambl.legacybrigadier.impl.server.LegacyBrigadierServer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.command.CommandSource;

import java.util.function.Supplier;

import static com.williambl.legacybrigadier.api.predicate.HasPermission.permission;

@Environment(EnvType.SERVER)
public class HelpCommand implements Supplier<LiteralArgumentBuilder<CommandSource>> {

    @Override
    public LiteralArgumentBuilder<CommandSource> get() {
        return LiteralArgumentBuilder.<CommandSource>literal("help")
                .requires(permission("command.help"))
                .executes(this::showHelp);
    }

    public int showHelp(CommandContext<CommandSource> context) {
        LegacyBrigadierServer.dispatcher
                .getSmartUsage(LegacyBrigadierServer.dispatcher.getRoot(), context.getSource())
                .forEach((cmd, usage) -> context.getSource().sendFeedback(
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
