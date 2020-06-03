package com.williambl.legacybrigadier.impl.server.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.williambl.legacybrigadier.api.argument.itemid.ItemId;
import com.williambl.legacybrigadier.api.argument.playerselector.PlayerSelector;
import com.williambl.legacybrigadier.api.command.CommandProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.command.CommandSource;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.williambl.legacybrigadier.api.argument.itemid.ItemIdArgumentType.getItemId;
import static com.williambl.legacybrigadier.api.argument.itemid.ItemIdArgumentType.itemId;
import static com.williambl.legacybrigadier.api.argument.playerselector.PlayerSelectorArgumentType.getPlayer;
import static com.williambl.legacybrigadier.api.argument.playerselector.PlayerSelectorArgumentType.player;
import static com.williambl.legacybrigadier.api.predicate.HasPermission.permission;

@Environment(EnvType.SERVER)
public class GiveCommand implements CommandProvider {

    @Override
    public LiteralArgumentBuilder<CommandSource> get() {
        return LiteralArgumentBuilder.<CommandSource>literal("give")
                .requires(permission("command.give"))
                .then(RequiredArgumentBuilder.<CommandSource, PlayerSelector>argument("player", player())
                        .then(RequiredArgumentBuilder.<CommandSource, ItemId>argument("item", itemId())
                                .executes(this::giveItem)
                                .then(RequiredArgumentBuilder.<CommandSource, Integer>argument("count", integer(0, 64))
                                        .executes(this::giveItemWithCount)
                                        .then(RequiredArgumentBuilder.<CommandSource, Integer>argument("meta", integer(0, 15))
                                                .executes(this::giveItemWithCountAndMeta)
                                        )
                                )
                        )
                );
    }

    public int giveItem(CommandContext<CommandSource> context) {
        getPlayer(context, "player").getPlayers(context.getSource()).forEach(player -> {
            int item = getItemId(context, "item").getNumericId();
            context.getSource().sendFeedback("Giving " + player.name + " some " + item);
            player.dropItem(item, 1, 0);
        });
        return 0;
    }

    public int giveItemWithCount(CommandContext<CommandSource> context) {
        getPlayer(context, "player").getPlayers(context.getSource()).forEach(player -> {
            int item = getItemId(context, "item").getNumericId();
            int count = getInteger(context, "count");
            sendFeedbackAndLog(context.getSource(), "Giving " + player.name + " " + count + " of " + item);
            player.dropItem(item, count, 0);
        });
        return 0;
    }

    public int giveItemWithCountAndMeta(CommandContext<CommandSource> context) {
        getPlayer(context, "player").getPlayers(context.getSource()).forEach(player -> {
            int item = getItemId(context, "item").getNumericId();
            int count = getInteger(context, "count");
            int meta = getInteger(context, "meta");
            sendFeedbackAndLog(context.getSource(), "Giving " + player.name + " " + count + " of " + item + ":" + meta);
            player.dropItem(item, count, meta);
        });
        return 0;
    }
}
