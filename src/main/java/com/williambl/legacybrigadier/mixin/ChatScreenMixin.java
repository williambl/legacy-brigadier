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

    private int currentMessageIndex = -1;
    private String currentMessage = "";

    @Inject(method = "keyPressed(CI)V", at = @At("TAIL"))
    void checkKeys(char c, int i, CallbackInfo ci) {
        switch (i) {

            case Keyboard.KEY_TAB:
                LegacyBrigadierClient.CHANNEL.send(getMessage().replaceFirst("/", "").getBytes(StandardCharsets.UTF_8), LegacyBrigadierClient.MINECRAFT);
                break;

            case Keyboard.KEY_UP:
                if (LegacyBrigadierClient.previousMessages.size() > currentMessageIndex+1) {
                    if (currentMessageIndex == -1)
                        currentMessage = getMessage();
                    setMessage(LegacyBrigadierClient.previousMessages.get(++currentMessageIndex));
                }
                break;

            case Keyboard.KEY_DOWN:
                if (currentMessageIndex == 0) {
                    currentMessageIndex = -1;
                    setMessage(currentMessage);
                } else if (currentMessageIndex > 0) {
                    setMessage(LegacyBrigadierClient.previousMessages.get(--currentMessageIndex));
                }
                break;

        }
    }

    @Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/AbstractClientPlayer;sendChatMessage(Ljava/lang/String;)V"))
    void addMessageToQueue(char c, int i, CallbackInfo ci) {
        LegacyBrigadierClient.previousMessages.add(getMessage().trim());
    }
}
