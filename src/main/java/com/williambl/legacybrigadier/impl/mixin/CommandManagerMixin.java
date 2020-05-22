package com.williambl.legacybrigadier.impl.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.williambl.legacybrigadier.api.command.CommandRegistry;
import com.williambl.legacybrigadier.impl.server.LegacyBrigadierServer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.command.Command;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandManager.class)
@Environment(EnvType.SERVER)
public class CommandManagerMixin {
    @Inject(
            method = "processCommand",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    void runCommandThroughBrigadier(Command commandInfo, CallbackInfo ci) {
        try {
        LegacyBrigadierServer.dispatcher.execute(commandInfo.commandString, commandInfo.source);
        ci.cancel();
        } catch (CommandSyntaxException e) {
            commandInfo.source.sendFeedback(e.getMessage());
        }
    }

    @Inject(
            method = "sendHelp",
            at = @At(value = "TAIL")
    )
    void appendBrigadierHelp(CommandSource commandSource, CallbackInfo ci) {
        LegacyBrigadierServer.dispatcher
                .getSmartUsage(LegacyBrigadierServer.dispatcher.getRoot(), commandSource)
                .forEach((cmd, usage) -> commandSource.sendFeedback(
                        String.format("   %s                        %s", usage, CommandRegistry.getHelp(cmd))
                ));
    }
}
