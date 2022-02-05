package com.williambl.legacybrigadier.api.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityRegistry;
import net.minecraft.entity.player.Player;
import net.minecraft.util.maths.MathsHelper;
import net.minecraft.util.maths.Vec3d;

public final class EntityUtils {
    public static String getName(final Entity entity) {
        if (entity instanceof Player) {
            return ((Player) entity).name;
        } else {
            return EntityRegistry.getStringId(entity);
        }
    }

    public static double distanceBetween(Entity entity, Vec3d pos) {
        double var2 = (entity.x - pos.x);
        double var3 = (entity.y - pos.y);
        double var4 = (entity.z - pos.z);
        return MathsHelper.sqrt(var2 * var2 + var3 * var3 + var4 * var4);
    }

    public static Vec3d getPosition(Entity entity) {
        return Vec3d.getOrCreate(entity.x, entity.y, entity.z);
    }
}
