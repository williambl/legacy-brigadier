package com.williambl.legacybrigadier.client.network;

import com.williambl.legacybrigadier.client.LegacyBrigadierClient;
import com.williambl.legacybrigadier.client.mixinhooks.ChatScreenHooks;
import io.github.minecraftcursedlegacy.api.networking.PluginChannel;
import io.github.minecraftcursedlegacy.api.registry.Id;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketHandler;

import java.nio.charset.StandardCharsets;

@Environment(EnvType.CLIENT)
public class LegacyBrigadierPluginChannelClient extends PluginChannel {

    private static final Id IDENTIFIER = new Id("legacybrigadier", "legacybrigadier");

    @Override
    public Id getChannelIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public void onRecieve(PacketHandler packetHandler, byte[] bytes) {
        if (packetHandler instanceof ClientPlayNetworkHandler) {
            String completion = new String(bytes, StandardCharsets.UTF_8);
            Screen screen = LegacyBrigadierClient.MINECRAFT.currentScreen;
            if (screen instanceof ChatScreenHooks) {
                ChatScreenHooks chatScreen = (ChatScreenHooks) screen;
                chatScreen.setMessage("/"+completion);
            }
        }
    }
}
