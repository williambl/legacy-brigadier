package com.williambl.legacybrigadier.server.mixinhooks;

import com.williambl.legacybrigadier.server.LegacyBrigadierServer;
import com.williambl.legacybrigadier.server.api.permission.PermissionNode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11;
import net.minecraft.entity.player.ServerPlayer;
import net.minecraft.level.Level;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerGUI;
import net.minecraft.util.Vec3i;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.SERVER)
public interface CommandSourceHooks {
    default Level getWorld() {
        if (this instanceof class_11)
            return ((ServerPlayerPacketHandlerHooks)this).getPlayer().level;
        return null;
    }

    default Vec3i getPosition() {
        if (this instanceof class_11) {
            ServerPlayer serverPlayer = ((ServerPlayerPacketHandlerHooks)this).getPlayer();
            return new Vec3i((int)serverPlayer.x, (int)serverPlayer.y, (int)serverPlayer.z);
        }

        return null;
    }

    default List<PermissionNode> getPermissions() {
        if (this instanceof MinecraftServer || this instanceof ServerGUI) {
            return getAllPermissions();
        } else if (this instanceof class_11){
            ServerPlayer serverPlayer = ((ServerPlayerPacketHandlerHooks)this).getPlayer();
            return LegacyBrigadierServer.permissionsMap.get(serverPlayer.name);
        }
        return new ArrayList<>();
    }

    default List<PermissionNode> getAllPermissions() {
        List<PermissionNode> list = new ArrayList<>();
        list.add(PermissionNode.ROOT);
        return list;
    }

    default boolean matchesNode(PermissionNode nodeToCheck) {
        List<PermissionNode> perms = getPermissions();
        for (PermissionNode perm : perms) {
            if (perm.matches(nodeToCheck))
                return true;
        }
        return false;
    }
}
