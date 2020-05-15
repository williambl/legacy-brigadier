package com.williambl.legacybrigadier.server;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.williambl.legacybrigadier.server.api.CommandRegistry;
import com.williambl.legacybrigadier.server.mixinhooks.CommandSourceHooks;
import com.williambl.legacybrigadier.server.network.LegacyBrigadierPluginChannelServer;
import io.github.minecraftcursedlegacy.api.networking.PluginChannelRegistry;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_39;
import net.minecraft.util.Vec3i;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.williambl.legacybrigadier.server.api.argument.CoordinateArgumentType.coordinate;
import static com.williambl.legacybrigadier.server.api.argument.CoordinateArgumentType.getCoordinate;
import static com.williambl.legacybrigadier.server.api.argument.TileIdArgumentType.getTileId;
import static com.williambl.legacybrigadier.server.api.argument.TileIdArgumentType.tileId;

@Environment(EnvType.SERVER)
public class LegacyBrigadierServer implements DedicatedServerModInitializer {

	public static CommandDispatcher<class_39> dispatcher = new CommandDispatcher<>();

	public static final LegacyBrigadierPluginChannelServer CHANNEL = new LegacyBrigadierPluginChannelServer();

	@Override
	public void onInitializeServer() {

		PluginChannelRegistry.registerPluginChannel(CHANNEL);

		CommandRegistry.register(
				LiteralArgumentBuilder.<class_39>literal("settile")
						.then(
								RequiredArgumentBuilder.<class_39, Vec3i>argument("pos", coordinate())
										.then(
												RequiredArgumentBuilder.<class_39, Integer>argument("id", tileId())
														.executes(context -> {
															Vec3i pos = getCoordinate(context, "pos");
															((CommandSourceHooks) context.getSource()).getWorld().setTile(pos.x, pos.y, pos.z, getTileId(context, "id"));
															return 0;
														})
														.then(
																RequiredArgumentBuilder.<class_39, Integer>argument("meta", integer())
																		.executes(context -> {
																			Vec3i pos = getCoordinate(context, "pos");
																			((CommandSourceHooks) context.getSource()).getWorld().method_201(pos.x, pos.y, pos.z, getTileId(context, "id"), getInteger(context, "meta"));
																			return 0;
																		})
														)
										)
						),
				"Set a tile"
		);
	}
}
