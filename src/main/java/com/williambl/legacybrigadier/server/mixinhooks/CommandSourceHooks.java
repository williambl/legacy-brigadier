package com.williambl.legacybrigadier.server.mixinhooks;

import com.williambl.legacybrigadier.mixin.ServerGUIAccessor;
import com.williambl.legacybrigadier.server.api.permission.PermissionManager;
import com.williambl.legacybrigadier.server.api.permission.PermissionNode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.ServerPlayer;
import net.minecraft.level.Level;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerGUI;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.network.ServerPlayerPacketHandler;
import net.minecraft.util.Vec3i;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.SERVER)
public interface CommandSourceHooks {

    default Level getWorld() {
        if (this instanceof ServerPlayerPacketHandler)
            return ((ServerPlayerPacketHandlerHooks)this).getPlayer().level;
        return null;
    }

    default Vec3i getPosition() {
        if (this instanceof ServerPlayerPacketHandler) {
            ServerPlayer serverPlayer = ((ServerPlayerPacketHandlerHooks)this).getPlayer();
            return new Vec3i((int)serverPlayer.x, (int)serverPlayer.y, (int)serverPlayer.z);
        }

        return new Vec3i(0, 0, 0);
    }

    default MinecraftServer getServer() {
        if (this instanceof ServerPlayerPacketHandler)
            return ((ServerPlayerPacketHandlerHooks)this).getServer();
        if (this instanceof MinecraftServer)
            return (MinecraftServer) this;
        if (this instanceof ServerGUI)
            return ((ServerGUIAccessor)this).getServer();
        return null;
    }

    default List<PermissionNode> getPermissions() {
        if (this instanceof MinecraftServer || this instanceof ServerGUI)
            return getAllPermissions();
        return PermissionManager.getNodesForCommandSource((CommandSource) this);
    }

    default List<PermissionNode> getAllPermissions() {
        List<PermissionNode> list = new ArrayList<>();
        list.add(PermissionNode.ROOT);
        return list;
    }

    default boolean satisfiesNode(PermissionNode nodeToCheck) {
        return nodeToCheck.isSatisfiedBy(getPermissions());
    }
}
