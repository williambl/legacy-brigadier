package com.williambl.legacybrigadier.impl.server.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.williambl.legacybrigadier.api.argument.coordinate.Coordinate;
import com.williambl.legacybrigadier.api.command.CommandProvider;
import com.williambl.legacybrigadier.api.command.ExtendedSender;
import io.github.minecraftcursedlegacy.impl.registry.EntityType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityRegistry;
import net.minecraft.level.Level;
import net.minecraft.util.maths.Vec3d;

import static com.williambl.legacybrigadier.api.argument.coordinate.CoordinateArgumentType.coordinate;
import static com.williambl.legacybrigadier.api.argument.coordinate.CoordinateArgumentType.getCoordinate;
import static com.williambl.legacybrigadier.api.argument.entityid.EntityTypeArgumentType.entityType;
import static com.williambl.legacybrigadier.api.argument.entityid.EntityTypeArgumentType.getEntityType;
import static com.williambl.legacybrigadier.api.predicate.HasPermission.permission;
import static com.williambl.legacybrigadier.api.predicate.IsWorldly.isWorldly;

@Environment(EnvType.SERVER)
public class SummonCommand implements CommandProvider {
    @Override
    public LiteralArgumentBuilder<ExtendedSender> get() {
        return LiteralArgumentBuilder.<ExtendedSender>literal("summon")
                .requires(permission("command.summon"))
                .requires(isWorldly())
                .then(RequiredArgumentBuilder.<ExtendedSender, EntityType>argument("id", entityType())
                        .then(RequiredArgumentBuilder.<ExtendedSender, Coordinate>argument("pos", coordinate())
                                .executes(this::summonEntity)
                        )
                );
    }

    public int summonEntity(CommandContext<ExtendedSender> context) {
        Vec3d pos = getCoordinate(context, "pos").getVec3d(context.getSource());
        Level world = context.getSource().getWorld();
        EntityType entityType = getEntityType(context, "id");
        Entity entity = EntityRegistry.create(entityType.getVanillaRegistryStringId(), world);
        entity.setPosition(pos.x, pos.y, pos.z);
        world.spawnEntity(entity);
        sendFeedbackAndLog(context.getSource(), "Summoned " + entityType.getId() + " at " + pos.x + " " + pos.y + " " + pos.z);
        return 0;
    }
}
