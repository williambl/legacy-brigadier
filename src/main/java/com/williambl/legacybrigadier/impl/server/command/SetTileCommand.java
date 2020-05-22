package com.williambl.legacybrigadier.impl.server.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.williambl.legacybrigadier.api.argument.coordinate.Coordinate;
import com.williambl.legacybrigadier.api.argument.tileid.TileId;
import com.williambl.legacybrigadier.impl.server.mixinhooks.CommandSourceHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.command.CommandSource;
import net.minecraft.util.Vec3i;

import java.util.function.Supplier;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.williambl.legacybrigadier.api.argument.coordinate.CoordinateArgumentType.coordinate;
import static com.williambl.legacybrigadier.api.argument.coordinate.CoordinateArgumentType.getCoordinate;
import static com.williambl.legacybrigadier.api.argument.tileid.TileIdArgumentType.getTileId;
import static com.williambl.legacybrigadier.api.argument.tileid.TileIdArgumentType.tileId;
import static com.williambl.legacybrigadier.api.predicate.HasPermission.permission;
import static com.williambl.legacybrigadier.api.predicate.IsWorldly.isWorldly;

@Environment(EnvType.SERVER)
public class SetTileCommand implements Supplier<LiteralArgumentBuilder<CommandSource>> {
    @Override
    public LiteralArgumentBuilder<CommandSource> get() {
        return LiteralArgumentBuilder.<CommandSource>literal("settile")
                .requires(permission("command.settile"))
                .requires(isWorldly())
                .then(RequiredArgumentBuilder.<CommandSource, Coordinate>argument("pos", coordinate())
                        .then(RequiredArgumentBuilder.<CommandSource, TileId>argument("id", tileId())
                                .executes(this::placeBlock)
                                .then(RequiredArgumentBuilder.<CommandSource, Integer>argument("meta", integer())
                                        .executes(this::placeBlockWithContext)
                                )
                        )
                );
    }

    public int placeBlock(CommandContext<CommandSource> context) {
        Vec3i pos = getCoordinate(context, "pos").getVec3i((CommandSourceHooks) context.getSource());
        ((CommandSourceHooks) context.getSource()).getWorld().setTile(pos.x, pos.y, pos.z, getTileId(context, "id").getNumericId());
        return 0;
    }

    public int placeBlockWithContext(CommandContext<CommandSource> context) {
        Vec3i pos = getCoordinate(context, "pos").getVec3i((CommandSourceHooks) context.getSource());
        ((CommandSourceHooks) context.getSource()).getWorld().method_201(pos.x, pos.y, pos.z, getTileId(context, "id").getNumericId(), getInteger(context, "meta"));
        return 0;
    }
}
