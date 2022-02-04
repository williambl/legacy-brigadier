package com.williambl.legacybrigadier.impl.server.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.williambl.legacybrigadier.api.argument.itemid.ItemId;
import com.williambl.legacybrigadier.api.argument.playerselector.TargetSelector;
import com.williambl.legacybrigadier.api.command.CommandProvider;
import com.williambl.legacybrigadier.api.command.ExtendedSender;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.williambl.legacybrigadier.api.argument.itemid.ItemIdArgumentType.getItemId;
import static com.williambl.legacybrigadier.api.argument.itemid.ItemIdArgumentType.itemId;
import static com.williambl.legacybrigadier.api.argument.playerselector.TargetSelectorArgumentType.getEntities;
import static com.williambl.legacybrigadier.api.argument.playerselector.TargetSelectorArgumentType.entities;
import static com.williambl.legacybrigadier.api.predicate.HasPermission.permission;

@Environment(EnvType.SERVER)
public class GiveCommand implements CommandProvider {

    @Override
    public LiteralArgumentBuilder<ExtendedSender> get() {
        return LiteralArgumentBuilder.<ExtendedSender>literal("give")
                .requires(permission("command.give"))
                .then(RequiredArgumentBuilder.<ExtendedSender, TargetSelector<?>>argument("player", entities())
                        .then(RequiredArgumentBuilder.<ExtendedSender, ItemId>argument("item", itemId())
                                .executes(this::giveItem)
                                .then(RequiredArgumentBuilder.<ExtendedSender, Integer>argument("count", integer(0, 64))
                                        .executes(this::giveItemWithCount)
                                        .then(RequiredArgumentBuilder.<ExtendedSender, Integer>argument("meta", integer(0, 15))
                                                .executes(this::giveItemWithCountAndMeta)
                                        )
                                )
                        )
                );
    }

    public int giveItem(CommandContext<ExtendedSender> context) {
        getEntities(context, "player").getEntities(context.getSource()).forEach(player -> {
            int item = getItemId(context, "item").getNumericId();
            context.getSource().sendCommandFeedback("Giving " + player.toString() + " some " + item);
            player.dropItem(item, 1, 0);
        });
        return 0;
    }

    public int giveItemWithCount(CommandContext<ExtendedSender> context) {
        getEntities(context, "player").getEntities(context.getSource()).forEach(player -> {
            int item = getItemId(context, "item").getNumericId();
            int count = getInteger(context, "count");
            sendFeedbackAndLog(context.getSource(), "Giving " + player.toString() + " " + count + " of " + item);
            player.dropItem(item, count, 0);
        });
        return 0;
    }

    public int giveItemWithCountAndMeta(CommandContext<ExtendedSender> context) {
        getEntities(context, "player").getEntities(context.getSource()).forEach(player -> {
            int item = getItemId(context, "item").getNumericId();
            int count = getInteger(context, "count");
            int meta = getInteger(context, "meta");
            sendFeedbackAndLog(context.getSource(), "Giving " + player.toString() + " " + count + " of " + item + ":" + meta);
            player.dropItem(item, count, meta);
        });
        return 0;
    }
}
