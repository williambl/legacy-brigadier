package com.williambl.legacybrigadier.impl.server.mixinhooks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.player.ServerPlayer;

@Environment(EnvType.SERVER)
public interface ServerPlayPacketHandlerHooks {
    ServerPlayer getPlayer();
    MinecraftServer getServer();
}
