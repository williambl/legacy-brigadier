package com.williambl.legacybrigadier.impl.mixin;

import com.williambl.legacybrigadier.api.command.CommandRegistry;
import com.williambl.legacybrigadier.api.command.ExtendedSender;
import com.williambl.legacybrigadier.impl.server.LegacyBrigadierServer;
import io.github.minecraftcursedlegacy.impl.command.ServerCommandSender;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
            method = "sendHelp",
            at = @At(value = "TAIL")
    )
    void appendBrigadierHelp(CommandSource commandSource, CallbackInfo ci) {
        LegacyBrigadierServer.dispatcher
                .getSmartUsage(LegacyBrigadierServer.dispatcher.getRoot(), ExtendedSender.extend(new ServerCommandSender(commandSource)))
                .forEach((cmd, usage) -> commandSource.sendFeedback(
                        String.format("   %s                        %s", usage, CommandRegistry.getHelp(cmd))
                ));
    }
}
