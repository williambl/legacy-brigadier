package com.williambl.legacybrigadier.impl.server.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.williambl.legacybrigadier.api.argument.coordinate.Coordinate;
import com.williambl.legacybrigadier.api.argument.entityid.EntityId;
import com.williambl.legacybrigadier.api.command.CommandProvider;
import com.williambl.legacybrigadier.api.command.ExtendedSender;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityRegistry;
import net.minecraft.level.Level;
import net.minecraft.util.Vec3i;

import static com.williambl.legacybrigadier.api.argument.coordinate.CoordinateArgumentType.coordinate;
import static com.williambl.legacybrigadier.api.argument.coordinate.CoordinateArgumentType.getCoordinate;
import static com.williambl.legacybrigadier.api.argument.entityid.EntityIdArgumentType.entityId;
import static com.williambl.legacybrigadier.api.argument.entityid.EntityIdArgumentType.getEntityId;
import static com.williambl.legacybrigadier.api.predicate.HasPermission.permission;
import static com.williambl.legacybrigadier.api.predicate.IsWorldly.isWorldly;

@Environment(EnvType.SERVER)
public class SummonCommand implements CommandProvider {
    @Override
    public LiteralArgumentBuilder<ExtendedSender> get() {
        return LiteralArgumentBuilder.<ExtendedSender>literal("summon")
                .requires(permission("command.summon"))
                .requires(isWorldly())
                .then(RequiredArgumentBuilder.<ExtendedSender, EntityId>argument("id", entityId())
                        .then(RequiredArgumentBuilder.<ExtendedSender, Coordinate>argument("pos", coordinate())
                                .executes(this::summonEntity)
                        )
                );
    }

    public int summonEntity(CommandContext<ExtendedSender> context) {
        Vec3i pos = getCoordinate(context, "pos").getVec3i(context.getSource());
        Level world = context.getSource().getWorld();
        EntityId id = getEntityId(context, "id");
        Entity entity = EntityRegistry.create(id.getId(), world);
        entity.setPosition(pos.x, pos.y, pos.z);
        world.spawnEntity(entity);
        sendFeedbackAndLog(context.getSource(), "Summoned " + id.getId() + " at " + pos.x + " " + pos.y + " " + pos.z);
        return 0;
    }
}
