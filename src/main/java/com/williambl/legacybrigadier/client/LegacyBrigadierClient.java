package com.williambl.legacybrigadier.client;

import com.williambl.legacybrigadier.client.network.LegacyBrigadierPluginChannelClient;
import io.github.minecraftcursedlegacy.api.networking.PluginChannelRegistry;
import io.github.minecraftcursedlegacy.api.registry.Id;
import io.github.minecraftcursedlegacy.api.registry.Registries;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.tile.Tile;
import net.minecraft.tile.material.Material;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class LegacyBrigadierClient implements ClientModInitializer {

    public static final LegacyBrigadierPluginChannelClient CHANNEL = new LegacyBrigadierPluginChannelClient();
    public static final List<String> previousMessages = new ArrayList<>();

    @Override
    public void onInitializeClient() {

        Registries.TILE.register(new Id("legacybrigadier", "commandtile"), i -> new Tile(i, Material.STONE) {});

        PluginChannelRegistry.registerPluginChannel(CHANNEL);
    }
}
