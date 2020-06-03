package com.williambl.legacybrigadier.impl.server.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.williambl.legacybrigadier.api.command.CommandProvider;
import com.williambl.legacybrigadier.impl.server.mixinhooks.CommandSourceHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.level.Level;
import net.minecraft.server.command.CommandSource;

import java.util.function.Function;

import static com.mojang.brigadier.arguments.LongArgumentType.getLong;
import static com.mojang.brigadier.arguments.LongArgumentType.longArg;
import static com.williambl.legacybrigadier.api.predicate.HasPermission.permission;
import static com.williambl.legacybrigadier.api.predicate.IsWorldly.isWorldly;

@Environment(EnvType.SERVER)
public class TimeCommand implements CommandProvider {

    @Override
    public LiteralArgumentBuilder<CommandSource> get() {
        return LiteralArgumentBuilder.<CommandSource>literal("time")
                .requires(permission("command.time"))
                .requires(isWorldly())
                .then(
                        LiteralArgumentBuilder.<CommandSource>literal("set")
                                .then(RequiredArgumentBuilder.<CommandSource, Long>argument("time", longArg(0))
                                        .executes(setTime(context -> getLong(context, "time")))
                                ).then(LiteralArgumentBuilder.<CommandSource>literal("day")
                                        .executes(setTime(a -> 0L))
                                ).then(LiteralArgumentBuilder.<CommandSource>literal("noon")
                                        .executes(setTime(a -> 6000L))
                                ).then(LiteralArgumentBuilder.<CommandSource>literal("night")
                                        .executes(setTime(a -> 12000L))
                                ).then(LiteralArgumentBuilder.<CommandSource>literal("midnight")
                                        .executes(setTime(a -> 18000L))
                                )
                )
                .then(
                        LiteralArgumentBuilder.<CommandSource>literal("get")
                                .executes(context -> {
                                    context.getSource().sendFeedback(Long.toString(((CommandSourceHooks)context.getSource()).getWorld().getLevelTime()));
                                    return 0;
                                })
                )
                .then(
                        LiteralArgumentBuilder.<CommandSource>literal("add")
                                .then(RequiredArgumentBuilder.<CommandSource, Long>argument("time", longArg())
                                        .executes(context -> {
                                            Level level = ((CommandSourceHooks)context.getSource()).getWorld();
                                            level.setLevelTime(level.getLevelTime()+getLong(context, "time"));
                                            return 0;
                                        }))
                );
    }

    public Command<CommandSource> setTime(Function<CommandContext<CommandSource>, Long> time) {
        return context -> {
            ((CommandSourceHooks) context.getSource()).getWorld().setLevelTime(time.apply(context));
            return 0;
        };
    }
}
