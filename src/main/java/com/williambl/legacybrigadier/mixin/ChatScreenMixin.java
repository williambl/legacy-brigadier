package com.williambl.legacybrigadier.mixin;

import com.williambl.legacybrigadier.client.LegacyBrigadierClient;
import com.williambl.legacybrigadier.client.mixinhooks.ChatScreenHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ChatScreen;
import org.lwjgl.input.Keyboard;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Mixin(ChatScreen.class)
@Environment(EnvType.CLIENT)
public abstract class ChatScreenMixin implements ChatScreenHooks {
    @Accessor("field_786")
    public abstract String getMessage();

    @Accessor("field_786")
    public abstract void setMessage(String newMessage);

    private int currentMessageIndex = -1;
    private String currentMessage = "";

    private List<String> completions;
    private int currentCompletion = 0;

    @Override
    public void setCompletions(List<String> completions) {
        this.completions = completions;
    }

    @Inject(method = "keyPressed(CI)V", at = @At("TAIL"))
    void checkKeys(char c, int i, CallbackInfo ci) {
        switch (i) {

            case Keyboard.KEY_TAB:
                if (completions != null) {
                    setMessage("/"+completions.get((++currentCompletion) % completions.size()));
                }
                LegacyBrigadierClient.CHANNEL.send(getMessage().replaceFirst("/", "").getBytes(StandardCharsets.UTF_8), LegacyBrigadierClient.MINECRAFT);
                break;

            case Keyboard.KEY_UP:
                if (LegacyBrigadierClient.previousMessages.size() > currentMessageIndex+1) {
                    if (currentMessageIndex == -1)
                        currentMessage = getMessage();
                    setMessage(LegacyBrigadierClient.previousMessages.get(++currentMessageIndex));
                    invalidateSuggestions();
                }
                break;

            case Keyboard.KEY_DOWN:
                if (currentMessageIndex == 0) {
                    currentMessageIndex = -1;
                    setMessage(currentMessage);
                } else if (currentMessageIndex > 0) {
                    setMessage(LegacyBrigadierClient.previousMessages.get(--currentMessageIndex));
                    invalidateSuggestions();
                }
                break;

        }
    }

    void invalidateSuggestions() {
        setCompletions(null);
        currentCompletion = 0;
    }

    @Inject(method = "keyPressed", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/gui/screen/ChatScreen;field_786:Ljava/lang/String;"))
    void invalidateWhenKeyPressed(char c, int i, CallbackInfo ci) {
        invalidateSuggestions();
    }

    @Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/AbstractClientPlayer;sendChatMessage(Ljava/lang/String;)V"))
    void addMessageToQueue(char c, int i, CallbackInfo ci) {
        LegacyBrigadierClient.previousMessages.add(0, getMessage().trim());
    }
}
