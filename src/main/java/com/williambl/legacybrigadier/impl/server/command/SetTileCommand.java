package com.williambl.legacybrigadier.impl.server.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.williambl.legacybrigadier.api.argument.coordinate.Coordinate;
import com.williambl.legacybrigadier.api.argument.tileid.TileId;
import com.williambl.legacybrigadier.api.command.CommandProvider;
import com.williambl.legacybrigadier.api.command.ExtendedSender;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Vec3i;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.williambl.legacybrigadier.api.argument.coordinate.CoordinateArgumentType.*;
import static com.williambl.legacybrigadier.api.argument.tileid.TileIdArgumentType.getTileId;
import static com.williambl.legacybrigadier.api.argument.tileid.TileIdArgumentType.tileId;
import static com.williambl.legacybrigadier.api.predicate.HasPermission.permission;
import static com.williambl.legacybrigadier.api.predicate.IsWorldly.isWorldly;

@Environment(EnvType.SERVER)
public class SetTileCommand implements CommandProvider {
    @Override
    public LiteralArgumentBuilder<ExtendedSender> get() {
        return LiteralArgumentBuilder.<ExtendedSender>literal("settile")
                .requires(permission("command.settile"))
                .requires(isWorldly())
                .then(RequiredArgumentBuilder.<ExtendedSender, Coordinate>argument("pos", intCoordinate())
                        .then(RequiredArgumentBuilder.<ExtendedSender, TileId>argument("id", tileId())
                                .executes(this::placeBlock)
                                .then(RequiredArgumentBuilder.<ExtendedSender, Integer>argument("meta", integer())
                                        .executes(this::placeBlockWithMeta)
                                )
                        )
                );
    }

    public int placeBlock(CommandContext<ExtendedSender> context) {
        Vec3i pos = getCoordinate(context, "pos").getVec3i(context.getSource());
        TileId tile = getTileId(context, "id");
        (context.getSource()).getWorld().setTile(pos.x, pos.y, pos.z, tile.getNumericId());
        sendFeedbackAndLog(context.getSource(), "Set block at" + pos.x + " " + pos.y + " " + pos.z + " to " + tile.getNumericId());
        return 0;
    }

    public int placeBlockWithMeta(CommandContext<ExtendedSender> context) {
        Vec3i pos = getCoordinate(context, "pos").getVec3i(context.getSource());
        TileId tile = getTileId(context, "id");
        int meta = getInteger(context, "meta");
        (context.getSource()).getWorld().method_201(pos.x, pos.y, pos.z, tile.getNumericId(), meta);
        sendFeedbackAndLog(context.getSource(), "Set block at" + pos.x + " " + pos.y + " " + pos.z + " to " + tile.getNumericId() + ":" + meta);
        return 0;
    }
}
