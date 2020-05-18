package com.williambl.legacybrigadier.mixin;

import com.williambl.legacybrigadier.server.BrigadierCommandManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.Command;
import net.minecraft.server.command.CommandManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftServer.class)
@Environment(EnvType.SERVER)
public class MinecraftServerMixin {

    @Redirect(method = "processQueuedCommands()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/CommandManager;processCommand(Lnet/minecraft/server/command/Command;)V"))
    void processCommand(CommandManager commandManager, Command commandInfo) {
        BrigadierCommandManager.handleCommand(commandInfo);
    }
}
