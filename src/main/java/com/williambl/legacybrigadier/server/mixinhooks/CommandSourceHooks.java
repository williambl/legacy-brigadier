package com.williambl.legacybrigadier.server.mixinhooks;

import com.williambl.legacybrigadier.mixin.ServerPlayerPacketHandlerMixin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11;
import net.minecraft.entity.player.ServerPlayer;
import net.minecraft.level.Level;
import net.minecraft.util.Vec3i;

@Environment(EnvType.SERVER)
public interface CommandSourceHooks {
    default Level getWorld() {
        if (this instanceof class_11)
            return ((ServerPlayerPacketHandlerMixin)this).getPlayer().level;
        return null;
    }

    default Vec3i getPosition() {
        if (this instanceof class_11) {
            ServerPlayer serverPlayer = ((ServerPlayerPacketHandlerMixin)this).getPlayer();
            return new Vec3i((int)serverPlayer.x, (int)serverPlayer.y, (int)serverPlayer.z);
        }

        return null;
    }
}
