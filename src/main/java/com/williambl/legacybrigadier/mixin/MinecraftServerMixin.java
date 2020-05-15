package com.williambl.legacybrigadier.mixin;

import com.williambl.legacybrigadier.server.CommandManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_38;
import net.minecraft.class_426;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftServer.class)
@Environment(EnvType.SERVER)
public class MinecraftServerMixin {

    @Redirect(method = "method_2164()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_426;method_1411(Lnet/minecraft/class_38;)V"))
    void processCommand(class_426 commandManager, class_38 commandInfo) {
        CommandManager.handleCommand(commandInfo);
    }
}
