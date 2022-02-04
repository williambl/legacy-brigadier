package com.williambl.legacybrigadier.impl.mixin;

import com.williambl.legacybrigadier.impl.server.mixinhooks.ServerPlayPacketHandlerHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayPacketHandler;
import net.minecraft.server.player.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerPlayPacketHandler.class)
@Environment(EnvType.SERVER)
public interface ServerPlayPacketHandlerAccessor extends ServerPlayPacketHandlerHooks {
    @Override
    @Accessor(value = "player")
    ServerPlayer getPlayer();

    @Override
    @Accessor(value = "server")
    MinecraftServer getServer();
}
