package com.williambl.legacybrigadier.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.williambl.legacybrigadier.LegacyBrigadier;
import net.minecraft.class_38;
import net.minecraft.class_426;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_426.class)
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
}
