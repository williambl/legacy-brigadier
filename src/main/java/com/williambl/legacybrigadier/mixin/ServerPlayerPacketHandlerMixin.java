package com.williambl.legacybrigadier.mixin;

import com.williambl.legacybrigadier.server.mixinhooks.ServerPlayerPacketHandlerHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11;
import net.minecraft.entity.player.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(class_11.class)
@Environment(EnvType.SERVER)
public abstract class ServerPlayerPacketHandlerMixin implements ServerPlayerPacketHandlerHooks {
    @Accessor(value = "field_920")
    public abstract ServerPlayer getPlayer();

    @Shadow
    private MinecraftServer field_919;

    @Redirect(method = "handleChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_11;method_836(Ljava/lang/String;)V"))
    void processAllCommands(class_11 handler, String message) {
        field_919.method_2162(message.substring(1), handler);
    }
}
