package com.williambl.legacybrigadier.server.network;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.williambl.legacybrigadier.mixin.ServerPlayerPacketHandlerMixin;
import com.williambl.legacybrigadier.server.LegacyBrigadierServer;
import io.github.minecraftcursedlegacy.api.networking.PluginChannel;
import io.github.minecraftcursedlegacy.api.registry.Id;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11;
import net.minecraft.class_39;
import net.minecraft.network.PacketHandler;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Environment(EnvType.SERVER)
public class LegacyBrigadierPluginChannelServer extends PluginChannel {

    private static final Id IDENTIFIER = new Id("legacybrigadier", "legacybrigadier");

    @Override
    public Id getChannelIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public void onRecieve(PacketHandler packetHandler, byte[] bytes) {
        if (packetHandler instanceof class_11) {
            String incompleteCommand = new String(bytes, StandardCharsets.UTF_8);
            ParseResults<class_39> parseResults = LegacyBrigadierServer.dispatcher.parse(incompleteCommand, (class_11) packetHandler);
            Suggestions suggestions;
            try {
                suggestions = LegacyBrigadierServer.dispatcher.getCompletionSuggestions(parseResults).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return;
            }
            if (suggestions.getList().size() > 0)
                send(stringsToBytes(applySuggestions(incompleteCommand, suggestions.getList())), ((ServerPlayerPacketHandlerMixin)packetHandler).getPlayer());
        }
    }

    private String[] applySuggestions(String input, List<Suggestion> suggestions) {
        String[] strings = new String[suggestions.size()];
        for (int i = 0; i < suggestions.size(); i++) {
            strings[i] = suggestions.get(i).apply(input);
        }
        return strings;
    }

    private byte[] stringsToBytes(String[] strings) {
        List<Byte> bytesList = new ArrayList<>();
        for (String string : strings) {
            int length = string.length();
            boolean tooLong = length > 100;
            if (!tooLong) {
                for (byte bt : string.getBytes(StandardCharsets.UTF_8)) {
                    bytesList.add(bt);
                }
                bytesList.add((byte) 0b00000000);
            }
        }
        byte[] bytes = new byte[bytesList.size()];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = bytesList.get(i);
        }
        return bytes;
    }
}
