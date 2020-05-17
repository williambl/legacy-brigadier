package com.williambl.legacybrigadier.client;

import com.williambl.legacybrigadier.client.network.LegacyBrigadierPluginChannelClient;
import io.github.minecraftcursedlegacy.api.networking.PluginChannelRegistry;
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
    public static Minecraft MINECRAFT;
    public static List<String> previousMessages = new ArrayList<>();

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

    public static final Tile COMMAND_TILE = new Tile(97, Material.STONE) {};

    @Override
    public void onInitializeClient() {
        PluginChannelRegistry.registerPluginChannel(CHANNEL);
    }
}
