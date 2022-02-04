package com.williambl.legacybrigadier.impl.client.network;

import com.williambl.legacybrigadier.impl.client.LegacyBrigadierClient;
import com.williambl.legacybrigadier.impl.client.mixinhooks.ChatScreenHooks;
import io.github.minecraftcursedlegacy.api.networking.PluginChannel;
import io.github.minecraftcursedlegacy.api.registry.Id;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketHandler;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Environment(EnvType.CLIENT)
public class LegacyBrigadierPluginChannelClient extends PluginChannel {

    private static final Id IDENTIFIER = new Id("legacybrigadier", "legacybrigadier");

    @Override
    public Id getChannelIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public void onReceive(PacketHandler packetHandler, byte[] bytes) {
        if (packetHandler instanceof ClientPlayNetworkHandler) {
            List<String> completions = bytesToStrings(bytes);
            Screen screen = LegacyBrigadierClient.MINECRAFT.currentScreen;
            if (screen instanceof ChatScreenHooks) {
                ChatScreenHooks chatScreen = (ChatScreenHooks) screen;
                if (completions.size() > 0) {
                    chatScreen.setMessage("/" + completions.get(0));
                    chatScreen.setCompletions(completions);
                }
            }
        }
    }

    private List<String> bytesToStrings(byte[] bytes) {
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == 0b00000000)
                break;

            final int stringStart = i;
            while (i < bytes.length && bytes[i] != 0b00000000) {
                i++;
            }
            final int stringEnd = i;
            strings.add(new String(Arrays.copyOfRange(bytes, stringStart, stringEnd), StandardCharsets.UTF_8));
        }
        return strings;
    }
}
