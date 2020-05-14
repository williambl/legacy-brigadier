package com.williambl.legacybrigadier.network;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.suggestion.Suggestions;
import com.williambl.legacybrigadier.LegacyBrigadierServer;
import com.williambl.legacybrigadier.mixin.ServerPlayerPacketHandlerMixin;
import io.github.minecraftcursedlegacy.api.networking.PluginChannel;
import io.github.minecraftcursedlegacy.api.registry.Id;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11;
import net.minecraft.class_39;
import net.minecraft.network.PacketHandler;

import java.nio.charset.StandardCharsets;
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
                send(suggestions.getList().get(0).getText().getBytes(StandardCharsets.UTF_8), ((ServerPlayerPacketHandlerMixin)packetHandler).getPlayer());
        }
    }
}
