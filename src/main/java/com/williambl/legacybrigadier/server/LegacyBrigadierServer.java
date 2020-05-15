package com.williambl.legacybrigadier.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.williambl.legacybrigadier.server.api.CommandRegistry;
import com.williambl.legacybrigadier.server.api.argument.EntityId;
import com.williambl.legacybrigadier.server.api.argument.PlayerSelector;
import com.williambl.legacybrigadier.server.api.argument.TileId;
import com.williambl.legacybrigadier.server.api.permission.PermissionNode;
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
import net.minecraft.packet.play.SendChatMessageC2S;
import net.minecraft.util.Vec3i;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.williambl.legacybrigadier.server.api.argument.CoordinateArgumentType.coordinate;
import static com.williambl.legacybrigadier.server.api.argument.CoordinateArgumentType.getCoordinate;
import static com.williambl.legacybrigadier.server.api.argument.EntityIdArgumentType.entityId;
import static com.williambl.legacybrigadier.server.api.argument.EntityIdArgumentType.getEntityId;
import static com.williambl.legacybrigadier.server.api.argument.PlayerSelectorArgumentType.getPlayer;
import static com.williambl.legacybrigadier.server.api.argument.PlayerSelectorArgumentType.player;
import static com.williambl.legacybrigadier.server.api.argument.TileIdArgumentType.getTileId;
import static com.williambl.legacybrigadier.server.api.argument.TileIdArgumentType.tileId;
import static com.williambl.legacybrigadier.server.api.permission.RequiresPermission.permission;

@Environment(EnvType.SERVER)
public class LegacyBrigadierServer implements DedicatedServerModInitializer {

	public static CommandDispatcher<class_39> dispatcher = new CommandDispatcher<>();

	public static final LegacyBrigadierPluginChannelServer CHANNEL = new LegacyBrigadierPluginChannelServer();

	public static final Logger LOGGER = Logger.getLogger("Minecraft");

	private static Gson GSON = new GsonBuilder().create();
	public static Map<String, List<PermissionNode>> permissionsMap = new HashMap<>();
	static {
		File permissionsFile = new File("config/legacybrigadier/permissions.json");
		try {
			TypeToken<Map<String, List<String>>> typeToken = new TypeToken<Map<String, List<String>>>() {};
			Map<String, List<String>> map = GSON.fromJson(new FileReader(permissionsFile), typeToken.getType());

			map.forEach((player, perms) ->
					permissionsMap.put(player, perms.stream().map(PermissionNode::new).collect(Collectors.toList()))
			);
		} catch (FileNotFoundException e) {
			try {
				if (permissionsFile.createNewFile()) {
					System.out.println("Created perms file.");
				} else {
					System.out.println("Couldn't create perms file!");
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			e.printStackTrace();
		}
	}

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
						.requires(permission("command.settile"))
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

		CommandRegistry.register(
				LiteralArgumentBuilder.<class_39>literal("help")
						.executes(context -> {
									LegacyBrigadierServer.dispatcher
											.getSmartUsage(LegacyBrigadierServer.dispatcher.getRoot(), context.getSource())
											.forEach((cmd, usage) -> context.getSource().method_1409(
													String.format("   %s                        %s", usage, CommandRegistry.getHelp(cmd))
											));
									return 0;
								}
						),
				"Show help"
		);

		CommandRegistry.register(
				LiteralArgumentBuilder.<class_39>literal("me")
						.then(
								RequiredArgumentBuilder.<class_39, String>argument("message", greedyString())
										.executes(context -> {
											String message = "* " + context.getSource().method_1410() + " " + getString(context, "message").trim();
											LOGGER.info(message);
											((CommandSourceHooks)context.getSource()).getServer().field_2842.method_559(new SendChatMessageC2S(message));
											return 0;
										})
						),
				"Show a message in chat with the format '* [name] [message]'"
		);

		CommandRegistry.register(
				LiteralArgumentBuilder.<class_39>literal("msg")
				.then(
						RequiredArgumentBuilder.<class_39, PlayerSelector>argument("player", player())
						.then(
								RequiredArgumentBuilder.<class_39, String>argument("message", greedyString())
								.executes(context -> {
									getPlayer(context, "player").getPlayers(context.getSource()).forEach(player -> {
										String message = "§7" + context.getSource().method_1410() + " whispers " + getString(context, "message");
										LOGGER.info(message + " to " + player);
										((CommandSourceHooks)(context.getSource())).getServer().field_2842.method_562(player, new SendChatMessageC2S(message));
									});
									return 0;
										})
						)
				),
				"Whisper something to a player"
		);
	}
}
