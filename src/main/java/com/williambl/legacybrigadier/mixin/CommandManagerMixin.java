package com.williambl.legacybrigadier.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.williambl.legacybrigadier.LegacyBrigadier;
import com.williambl.legacybrigadier.api.CommandRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_38;
import net.minecraft.class_39;
import net.minecraft.class_426;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_426.class)
@Environment(EnvType.SERVER)
public class CommandManagerMixin {
    @Inject(
            method = "method_1411(Lnet/minecraft/class_38;)V",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    void runCommandThroughBrigadier(class_38 commandInfo, CallbackInfo ci) {
        try {
        LegacyBrigadier.dispatcher.execute(commandInfo.field_159, commandInfo.field_160);
        ci.cancel();
        } catch (CommandSyntaxException ignored) {}
    }

    @Inject(
            method = "method_1415",
            at = @At(value = "TAIL")
    )
    void appendBrigadierHelp(class_39 commandSource, CallbackInfo ci) {
        LegacyBrigadier.dispatcher
                .getSmartUsage(LegacyBrigadier.dispatcher.getRoot(), commandSource)
                .forEach((cmd, usage) -> commandSource.method_1409(
                        String.format("   %s                        %s", usage, CommandRegistry.getHelp(cmd))
                ));
    }
}
