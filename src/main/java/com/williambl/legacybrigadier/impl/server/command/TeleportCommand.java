package com.williambl.legacybrigadier.impl.server.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.williambl.legacybrigadier.api.argument.coordinate.Coordinate;
import com.williambl.legacybrigadier.api.argument.playerselector.TargetSelector;
import com.williambl.legacybrigadier.api.argument.playerselector.TargetSelectorArgumentType;
import com.williambl.legacybrigadier.api.argument.tileid.TileId;
import com.williambl.legacybrigadier.api.command.CommandProvider;
import com.williambl.legacybrigadier.api.command.ExtendedSender;
import com.williambl.legacybrigadier.api.utils.EntityUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.server.player.ServerPlayer;
import net.minecraft.util.Vec3i;
import net.minecraft.util.maths.Vec3d;

import java.lang.annotation.Target;
import java.util.List;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.williambl.legacybrigadier.api.argument.coordinate.CoordinateArgumentType.*;
import static com.williambl.legacybrigadier.api.argument.playerselector.TargetSelectorArgumentType.*;
import static com.williambl.legacybrigadier.api.argument.tileid.TileIdArgumentType.getTileId;
import static com.williambl.legacybrigadier.api.predicate.HasPermission.permission;
import static com.williambl.legacybrigadier.api.predicate.IsWorldly.isWorldly;

@Environment(EnvType.SERVER)
public class TeleportCommand implements CommandProvider {
    @Override
    public LiteralArgumentBuilder<ExtendedSender> get() {
        return LiteralArgumentBuilder.<ExtendedSender>literal("tp")
                .requires(permission("command.tp"))
                .requires(isWorldly())
                .then(RequiredArgumentBuilder.<ExtendedSender, Coordinate>argument("pos", coordinate())
                        .executes(this::teleportToPosition)
                )
                .then(RequiredArgumentBuilder.<ExtendedSender, TargetSelector<?>>argument("target", entity())
                        .executes(this::teleportToEntity)
                )
                .then(RequiredArgumentBuilder.<ExtendedSender, TargetSelector<?>>argument("teleportees", entities())
                        .then(RequiredArgumentBuilder.<ExtendedSender, Coordinate>argument("pos", coordinate())
                                .executes(this::teleportOtherToPosition)
                        )
                        .then(RequiredArgumentBuilder.<ExtendedSender, TargetSelector<?>>argument("target", entity())
                                .executes(this::teleportOtherToEntity)
                        )
                );
    }

    private int teleportToPosition(CommandContext<ExtendedSender> ctx) {
        ExtendedSender sender = ctx.getSource();
        Vec3d position = getCoordinate(ctx, "pos").getVec3d(sender);
        this.teleport(sender.getEntity(), position, sender.getEntity().yaw, sender.getEntity().pitch, sender);
        return 1;
    }

    private int teleportToEntity(CommandContext<ExtendedSender> ctx) {
        ExtendedSender sender = ctx.getSource();
        Entity target = getEntities(ctx, "target").getEntities(sender).get(0);
        this.teleport(sender.getEntity(), EntityUtils.getPosition(target), target.yaw, target.pitch, sender);
        return 1;
    }

    private int teleportOtherToPosition(CommandContext<ExtendedSender> ctx) {
        ExtendedSender sender = ctx.getSource();
        List<Entity> teleportees = getEntities(ctx, "teleportees").getEntities(sender);
        Vec3d position = getCoordinate(ctx, "pos").getVec3d(sender);
        teleportees.forEach(e -> this.teleport(e, position, e.yaw, e.pitch, sender));
        return teleportees.size();
    }

    private int teleportOtherToEntity(CommandContext<ExtendedSender> ctx) {
        ExtendedSender sender = ctx.getSource();
        List<Entity> teleportees = getEntities(ctx, "teleportees").getEntities(sender);
        Entity target = getEntities(ctx, "target").getEntities(sender).get(0);
        teleportees.forEach(e -> this.teleport(e, EntityUtils.getPosition(target), e.yaw, e.pitch, sender));
        return teleportees.size();
    }

    private void teleport(Entity entity, Vec3d position, float yaw, float pitch, ExtendedSender sender) {
        if (entity instanceof ServerPlayer) {
            ((ServerPlayer)entity).packetHandler.method_832(position.x, position.y, position.z, yaw, pitch);
        } else {
            entity.setPositionAndAngles(position.x, position.y, position.z, yaw, pitch);
        }

        this.sendFeedbackAndLog(sender, String.format("Teleporting %s to %s (%f %f)", EntityUtils.getName(entity), position, yaw, pitch));
    }
}
