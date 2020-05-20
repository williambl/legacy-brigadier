package com.williambl.legacybrigadier.server.mixinhooks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.ServerPlayer;
import net.minecraft.server.MinecraftServer;

@Environment(EnvType.SERVER)
public interface ServerPlayerPacketHandlerHooks {
    ServerPlayer getPlayer();
    MinecraftServer getServer();
}
