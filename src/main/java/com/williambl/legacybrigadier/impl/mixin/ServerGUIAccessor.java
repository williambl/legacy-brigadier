package com.williambl.legacybrigadier.impl.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerGUI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerGUI.class)
public interface ServerGUIAccessor {
    @Accessor("server")
    MinecraftServer getServer();
}
