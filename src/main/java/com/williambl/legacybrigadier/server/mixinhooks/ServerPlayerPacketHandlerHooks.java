package com.williambl.legacybrigadier.server.mixinhooks;

import net.minecraft.entity.player.ServerPlayer;
import net.minecraft.server.MinecraftServer;

public interface ServerPlayerPacketHandlerHooks {
    ServerPlayer getPlayer();
    MinecraftServer getServer();
}
