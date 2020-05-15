package com.williambl.legacybrigadier.server;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.williambl.legacybrigadier.server.api.CommandRegistry;
import com.williambl.legacybrigadier.server.api.argument.EntityId;
import com.williambl.legacybrigadier.server.api.argument.TileId;
import com.williambl.legacybrigadier.server.mixinhooks.CommandSourceHooks;
import com.williambl.legacybrigadier.server.network.LegacyBrigadierPluginChannelServer;
import io.github.minecraftcursedlegacy.api.networking.PluginChannelRegistry;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_39;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityRegistry;
import net.minecraft.level.Level;
import net.minecraft.util.Vec3i;

import java.lang.reflect.Field;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.williambl.legacybrigadier.server.api.argument.CoordinateArgumentType.coordinate;
import static com.williambl.legacybrigadier.server.api.argument.CoordinateArgumentType.getCoordinate;
import static com.williambl.legacybrigadier.server.api.argument.EntityIdArgumentType.entityId;
import static com.williambl.legacybrigadier.server.api.argument.EntityIdArgumentType.getEntityId;
import static com.williambl.legacybrigadier.server.api.argument.TileIdArgumentType.getTileId;
import static com.williambl.legacybrigadier.server.api.argument.TileIdArgumentType.tileId;

@Environment(EnvType.SERVER)
public class LegacyBrigadierServer implements DedicatedServerModInitializer {

	public static CommandDispatcher<class_39> dispatcher = new CommandDispatcher<>();

	public static final LegacyBrigadierPluginChannelServer CHANNEL = new LegacyBrigadierPluginChannelServer();

	public static Field ENTITY_MAP;

	static {
		try {
			ENTITY_MAP = EntityRegistry.class.getDeclaredField("STRING_ID_TO_CLASS");
			ENTITY_MAP.setAccessible(true);
		} catch (NoSuchFieldException e) {
			System.out.println("Couldn't find Entity string ID map :concern:");
			e.printStackTrace();
		}
	}

	@Override
	public void onInitializeServer() {

		PluginChannelRegistry.registerPluginChannel(CHANNEL);

		CommandRegistry.register(
				LiteralArgumentBuilder.<class_39>literal("settile")
						.then(
								RequiredArgumentBuilder.<class_39, Vec3i>argument("pos", coordinate())
										.then(
												RequiredArgumentBuilder.<class_39, TileId>argument("id", tileId())
														.executes(context -> {
															Vec3i pos = getCoordinate(context, "pos");
															((CommandSourceHooks) context.getSource()).getWorld().setTile(pos.x, pos.y, pos.z, getTileId(context, "id").getId());
															return 0;
														})
														.then(
																RequiredArgumentBuilder.<class_39, Integer>argument("meta", integer())
																		.executes(context -> {
																			Vec3i pos = getCoordinate(context, "pos");
																			((CommandSourceHooks) context.getSource()).getWorld().method_201(pos.x, pos.y, pos.z, getTileId(context, "id").getId(), getInteger(context, "meta"));
																			return 0;
																		})
														)
										)
						),
				"Set a tile"
		);

		CommandRegistry.register(
				LiteralArgumentBuilder.<class_39>literal("summon")
						.then(
								RequiredArgumentBuilder.<class_39, EntityId>argument("id", entityId())
										.then(
												RequiredArgumentBuilder.<class_39, Vec3i>argument("pos", coordinate())
														.executes(context -> {
															Vec3i pos = getCoordinate(context, "pos");
															Level world = ((CommandSourceHooks)context.getSource()).getWorld();
															Entity entity = EntityRegistry.create(getEntityId(context, "id").getId(), world);
															entity.setPosition(pos.x, pos.y, pos.z);
															world.spawnEntity(entity);
															return 0;
														})
										)
						),
				"Spawn an entity"
		);
	}
}
