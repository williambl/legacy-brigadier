package com.williambl.legacybrigadier.impl.server;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.williambl.legacybrigadier.api.command.CommandRegistry;
import com.williambl.legacybrigadier.impl.server.command.*;
import com.williambl.legacybrigadier.impl.server.network.LegacyBrigadierPluginChannelServer;
import io.github.minecraftcursedlegacy.api.networking.PluginChannelRegistry;
import io.github.minecraftcursedlegacy.api.registry.Id;
import io.github.minecraftcursedlegacy.api.registry.Registries;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.SignEntity;
import net.minecraft.entity.TileEntity;
import net.minecraft.entity.player.Player;
import net.minecraft.entity.player.ServerPlayer;
import net.minecraft.level.Level;
import net.minecraft.server.command.CommandSource;
import net.minecraft.tile.Tile;
import net.minecraft.tile.material.Material;

import java.util.logging.Logger;

@Environment(EnvType.SERVER)
public class LegacyBrigadierServer implements DedicatedServerModInitializer {

	public static final CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher<>();

	public static final LegacyBrigadierPluginChannelServer CHANNEL = new LegacyBrigadierPluginChannelServer();

	public static final Logger LOGGER = Logger.getLogger("Minecraft");


	@Override
	public void onInitializeServer() {

		PluginChannelRegistry.registerPluginChannel(CHANNEL);

		Registries.TILE.register(new Id("legacybrigadier", "commandtile"), i -> new Tile(i, Material.STONE) {
			@Override
			public boolean method_1608(Level level, int x, int y, int z, Player player) {
				super.method_1608(level, x, y, z, player);
				TileEntity entity = level.getTileEntity(x, y+1, z);
				if (!(entity instanceof SignEntity))
					return false;
				StringBuilder command = new StringBuilder();
				for (String line : ((SignEntity) entity).lines) {
					command.append(line);
				}
				try {
					dispatcher.execute(command.toString(), ((ServerPlayer)player).field_255);
				} catch (CommandSyntaxException e) {
					((ServerPlayer)player).field_255.sendFeedback(e.getMessage());
					return true;
				}
				return true;
			}
		});

		CommandRegistry.register(
				new SetTileCommand(), "Set a tile"
		);
		CommandRegistry.register(
				new SummonCommand(), "Spawn an entity"
		);
		CommandRegistry.register(
				new HelpCommand(), "Show help"
		);
		CommandRegistry.register(
				new MeCommand(), "Show a message in chat with the format '* [name] [message]'"
		);
		CommandRegistry.register(
				new MsgCommand(), "Whisper something to a player"
		);
		CommandRegistry.register(
				new GiveCommand(), "Give an item to a player"
		);
		CommandRegistry.register(
				new TimeCommand(), "Set or get the time"
		);
		CommandRegistry.register(
				new PermissionsCommand(), "Query or add permissions to players."
		);
	}
}
