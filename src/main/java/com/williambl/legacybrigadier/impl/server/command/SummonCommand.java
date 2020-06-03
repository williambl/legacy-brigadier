package com.williambl.legacybrigadier.impl.server.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.williambl.legacybrigadier.api.argument.coordinate.Coordinate;
import com.williambl.legacybrigadier.api.argument.entityid.EntityId;
import com.williambl.legacybrigadier.impl.server.mixinhooks.CommandSourceHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityRegistry;
import net.minecraft.level.Level;
import net.minecraft.server.command.CommandSource;
import net.minecraft.util.Vec3i;

import java.util.function.Supplier;

import static com.williambl.legacybrigadier.api.argument.coordinate.CoordinateArgumentType.coordinate;
import static com.williambl.legacybrigadier.api.argument.coordinate.CoordinateArgumentType.getCoordinate;
import static com.williambl.legacybrigadier.api.argument.entityid.EntityIdArgumentType.entityId;
import static com.williambl.legacybrigadier.api.argument.entityid.EntityIdArgumentType.getEntityId;
import static com.williambl.legacybrigadier.api.predicate.HasPermission.permission;
import static com.williambl.legacybrigadier.api.predicate.IsWorldly.isWorldly;

@Environment(EnvType.SERVER)
public class SummonCommand implements Supplier<LiteralArgumentBuilder<CommandSource>> {
    @Override
    public LiteralArgumentBuilder<CommandSource> get() {
        return LiteralArgumentBuilder.<CommandSource>literal("summon")
                .requires(permission("command.summon"))
                .requires(isWorldly())
                .then(RequiredArgumentBuilder.<CommandSource, EntityId>argument("id", entityId())
                        .then(RequiredArgumentBuilder.<CommandSource, Coordinate>argument("pos", coordinate())
                                .executes(this::summonEntity)
                        )
                );
    }

    public int summonEntity(CommandContext<CommandSource> context) {
        Vec3i pos = getCoordinate(context, "pos").getVec3i((CommandSourceHooks) context.getSource());
        Level world = ((CommandSourceHooks)context.getSource()).getWorld();
        Entity entity = EntityRegistry.create(getEntityId(context, "id").getId(), world);
        entity.setPosition(pos.x, pos.y, pos.z);
        world.spawnEntity(entity);
        return 0;
    }
}