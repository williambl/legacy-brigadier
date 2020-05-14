package com.williambl.legacybrigadier.mixin;

import com.williambl.legacybrigadier.client.LegacyBrigadierClient;
import com.williambl.legacybrigadier.client.mixinhooks.ChatScreenHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ChatScreen;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.charset.StandardCharsets;

@Mixin(ChatScreen.class)
@Environment(EnvType.CLIENT)
public abstract class ChatScreenMixin implements ChatScreenHooks {
    @Accessor("field_786")
    public abstract String getMessage();

    @Accessor("field_786")
    public abstract void setMessage(String newMessage);

    @Inject(method = "keyPressed(CI)V", at = @At("TAIL"))
    void completeWithTab(char c, int i, CallbackInfo ci) {
        if (i == Keyboard.KEY_TAB) {
            LegacyBrigadierClient.CHANNEL.send(getMessage().replaceFirst("/", "").getBytes(StandardCharsets.UTF_8), LegacyBrigadierClient.MINECRAFT);
        }
    }
}
