package com.williambl.legacybrigadier.impl.server.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.williambl.legacybrigadier.api.command.CommandProvider;
import com.williambl.legacybrigadier.api.command.ExtendedSender;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.level.Level;

import java.util.function.Function;

import static com.mojang.brigadier.arguments.LongArgumentType.getLong;
import static com.mojang.brigadier.arguments.LongArgumentType.longArg;
import static com.williambl.legacybrigadier.api.predicate.HasPermission.permission;
import static com.williambl.legacybrigadier.api.predicate.IsWorldly.isWorldly;

@Environment(EnvType.SERVER)
public class TimeCommand implements CommandProvider {

    @Override
    public LiteralArgumentBuilder<ExtendedSender> get() {
        return LiteralArgumentBuilder.<ExtendedSender>literal("time")
                .requires(permission("command.time"))
                .requires(isWorldly())
                .then(
                        LiteralArgumentBuilder.<ExtendedSender>literal("set")
                                .then(RequiredArgumentBuilder.<ExtendedSender, Long>argument("time", longArg(0))
                                        .executes(setTime(context -> getLong(context, "time")))
                                ).then(LiteralArgumentBuilder.<ExtendedSender>literal("day")
                                        .executes(setTime(a -> 0L))
                                ).then(LiteralArgumentBuilder.<ExtendedSender>literal("noon")
                                        .executes(setTime(a -> 6000L))
                                ).then(LiteralArgumentBuilder.<ExtendedSender>literal("night")
                                        .executes(setTime(a -> 12000L))
                                ).then(LiteralArgumentBuilder.<ExtendedSender>literal("midnight")
                                        .executes(setTime(a -> 18000L))
                                )
                )
                .then(
                        LiteralArgumentBuilder.<ExtendedSender>literal("get")
                                .executes(context -> {
                                    sendFeedbackAndLog(context.getSource(), Long.toString((context.getSource().getWorld().getLevelTime())));
                                    return 0;
                                })
                )
                .then(
                        LiteralArgumentBuilder.<ExtendedSender>literal("add")
                                .then(RequiredArgumentBuilder.<ExtendedSender, Long>argument("time", longArg())
                                        .executes(context -> {
                                            Level level = (context.getSource()).getWorld();
                                            level.setLevelTime(level.getLevelTime()+getLong(context, "time"));
                                            sendFeedbackAndLog(context.getSource(), "Set time to " + level.getLevelTime());
                                            return 0;
                                        }))
                );
    }

    public Command<ExtendedSender> setTime(Function<CommandContext<ExtendedSender>, Long> time) {
        return context -> {
            long timeValue = time.apply(context);
            ( context.getSource()).getWorld().setLevelTime(timeValue);
            sendFeedbackAndLog(context.getSource(), "Set time to " + timeValue);
            return 0;
        };
    }
}
