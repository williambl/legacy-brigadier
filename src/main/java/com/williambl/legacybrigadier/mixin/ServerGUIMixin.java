package com.williambl.legacybrigadier.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerGUI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerGUI.class)
public interface ServerGUIMixin {
    @Accessor("field_1978")
    MinecraftServer getServer();
}
