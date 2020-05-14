package com.williambl.legacybrigadier.mixin;

import net.minecraft.class_11;
import net.minecraft.entity.player.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_11.class)
public interface ServerPlayerPacketHandlerMixin {
    @Accessor(value = "field_920")
    ServerPlayer getPlayer();
}
