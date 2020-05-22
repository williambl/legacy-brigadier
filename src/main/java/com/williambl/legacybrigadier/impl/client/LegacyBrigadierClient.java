package com.williambl.legacybrigadier.impl.client;

import com.williambl.legacybrigadier.impl.client.network.LegacyBrigadierPluginChannelClient;
import io.github.minecraftcursedlegacy.api.networking.PluginChannelRegistry;
import io.github.minecraftcursedlegacy.api.registry.Id;
import io.github.minecraftcursedlegacy.api.registry.Registries;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.tile.Tile;
import net.minecraft.tile.material.Material;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class LegacyBrigadierClient implements ClientModInitializer {

    public static final LegacyBrigadierPluginChannelClient CHANNEL = new LegacyBrigadierPluginChannelClient();
    public static final List<String> previousMessages = new ArrayList<>();
    public static Minecraft MINECRAFT;

    static {
        try {
        Field field = Minecraft.class.getDeclaredField("instance");
        field.setAccessible(true);
        MINECRAFT = (Minecraft) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.out.println("Couldn't find Minecraft instance :concern:");
            e.printStackTrace();
        }
    }

    @Override
    public void onInitializeClient() {

        Registries.TILE.register(new Id("legacybrigadier", "commandtile"), i -> new Tile(i, Material.STONE) {});

        PluginChannelRegistry.registerPluginChannel(CHANNEL);
    }
}
