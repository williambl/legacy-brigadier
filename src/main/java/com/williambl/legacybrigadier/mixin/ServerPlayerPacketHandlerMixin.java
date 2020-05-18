package com.williambl.legacybrigadier.mixin;

import com.williambl.legacybrigadier.server.mixinhooks.ServerPlayerPacketHandlerHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerPacketHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerPacketHandler.class)
@Environment(EnvType.SERVER)
public abstract class ServerPlayerPacketHandlerMixin implements ServerPlayerPacketHandlerHooks {
    @Accessor(value = "field_920")
    public abstract ServerPlayer getPlayer();

    @Accessor(value = "field_919")
    public abstract MinecraftServer getServer();

    @Shadow
    private MinecraftServer field_919;

    @Redirect(method = "handleChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_11;method_836(Ljava/lang/String;)V"))
    void processAllCommands(ServerPlayerPacketHandler handler, String message) {
        field_919.queueCommand(message.substring(1), handler);
    }
}
