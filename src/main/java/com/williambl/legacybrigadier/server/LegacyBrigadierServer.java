package com.williambl.legacybrigadier.server;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.williambl.legacybrigadier.server.api.CommandRegistry;
import com.williambl.legacybrigadier.server.api.argument.*;
import com.williambl.legacybrigadier.server.mixinhooks.CommandSourceHooks;
import com.williambl.legacybrigadier.server.network.LegacyBrigadierPluginChannelServer;
import io.github.minecraftcursedlegacy.api.networking.PluginChannelRegistry;
import io.github.minecraftcursedlegacy.api.registry.Id;
import io.github.minecraftcursedlegacy.api.registry.Registries;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityRegistry;
import net.minecraft.entity.SignEntity;
import net.minecraft.entity.TileEntity;
import net.minecraft.entity.player.Player;
import net.minecraft.entity.player.ServerPlayer;
import net.minecraft.level.Level;
import net.minecraft.packet.play.SendChatMessageC2S;
import net.minecraft.server.command.CommandSource;
import net.minecraft.tile.Tile;
import net.minecraft.tile.material.Material;
import net.minecraft.util.Vec3i;

import java.util.logging.Logger;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.williambl.legacybrigadier.server.api.argument.CoordinateArgumentType.coordinate;
import static com.williambl.legacybrigadier.server.api.argument.CoordinateArgumentType.getCoordinate;
import static com.williambl.legacybrigadier.server.api.argument.EntityIdArgumentType.entityId;
import static com.williambl.legacybrigadier.server.api.argument.EntityIdArgumentType.getEntityId;
import static com.williambl.legacybrigadier.server.api.argument.ItemIdArgumentType.getItemId;
import static com.williambl.legacybrigadier.server.api.argument.ItemIdArgumentType.itemId;
import static com.williambl.legacybrigadier.server.api.argument.PlayerSelectorArgumentType.getPlayer;
import static com.williambl.legacybrigadier.server.api.argument.PlayerSelectorArgumentType.player;
import static com.williambl.legacybrigadier.server.api.argument.TileIdArgumentType.getTileId;
import static com.williambl.legacybrigadier.server.api.argument.TileIdArgumentType.tileId;
import static com.williambl.legacybrigadier.server.api.permission.RequiresPermission.permission;

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
				super.activate(level, x, y, z, player);
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
				LiteralArgumentBuilder.<CommandSource>literal("settile")
						.requires(permission("command.settile"))
						.then(
								RequiredArgumentBuilder.<CommandSource, Coordinate>argument("pos", coordinate())
										.then(
												RequiredArgumentBuilder.<CommandSource, TileId>argument("id", tileId())
														.executes(context -> {
															Vec3i pos = getCoordinate(context, "pos").getVec3i((CommandSourceHooks) context.getSource());
															((CommandSourceHooks) context.getSource()).getWorld().setTile(pos.x, pos.y, pos.z, getTileId(context, "id").getNumericId());
															return 0;
														})
														.then(
																RequiredArgumentBuilder.<CommandSource, Integer>argument("meta", integer())
																		.executes(context -> {
																			Vec3i pos = getCoordinate(context, "pos").getVec3i((CommandSourceHooks) context.getSource());
																			((CommandSourceHooks) context.getSource()).getWorld().method_201(pos.x, pos.y, pos.z, getTileId(context, "id").getNumericId(), getInteger(context, "meta"));
																			return 0;
																		})
														)
										)
						),
				"Set a tile"
		);

		CommandRegistry.register(
				LiteralArgumentBuilder.<CommandSource>literal("summon")
						.requires(permission("command.summon"))
						.then(
								RequiredArgumentBuilder.<CommandSource, EntityId>argument("id", entityId())
										.then(
												RequiredArgumentBuilder.<CommandSource, Coordinate>argument("pos", coordinate())
														.executes(context -> {
															Vec3i pos = getCoordinate(context, "pos").getVec3i((CommandSourceHooks) context.getSource());
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
				LiteralArgumentBuilder.<CommandSource>literal("help")
						.requires(permission("command.help"))
						.executes(context -> {
									LegacyBrigadierServer.dispatcher
											.getSmartUsage(LegacyBrigadierServer.dispatcher.getRoot(), context.getSource())
											.forEach((cmd, usage) -> context.getSource().sendFeedback(
													String.format("   %s                        %s", usage, CommandRegistry.getHelp(cmd))
											));
									return 0;
								}
						),
				"Show help"
		);

		CommandRegistry.register(
				LiteralArgumentBuilder.<CommandSource>literal("me")
						.requires(permission("command.me"))
						.then(
								RequiredArgumentBuilder.<CommandSource, String>argument("message", greedyString())
										.executes(context -> {
											String message = "* " + context.getSource().getName() + " " + getString(context, "message").trim();
											LOGGER.info(message);
											((CommandSourceHooks)context.getSource()).getServer().field_2842.method_559(new SendChatMessageC2S(message));
											return 0;
										})
						),
				"Show a message in chat with the format '* [name] [message]'"
		);

		CommandRegistry.register(
				LiteralArgumentBuilder.<CommandSource>literal("msg")
						.requires(permission("command.msg"))
						.then(
								RequiredArgumentBuilder.<CommandSource, PlayerSelector>argument("player", player())
										.then(
												RequiredArgumentBuilder.<CommandSource, String>argument("message", greedyString())
														.executes(context -> {
															getPlayer(context, "player").getPlayerNames(context.getSource()).forEach(player -> {
																String message = "§7" + context.getSource().getName() + " whispers " + getString(context, "message");
																LOGGER.info(message + " to " + player);
																((CommandSourceHooks)(context.getSource())).getServer().field_2842.method_562(player, new SendChatMessageC2S(message));
															});
															return 0;
														})
										)
						),
				"Whisper something to a player"
		);

		CommandRegistry.register(
				LiteralArgumentBuilder.<CommandSource>literal("give")
						.requires(permission("command.give"))
						.then(
								RequiredArgumentBuilder.<CommandSource, PlayerSelector>argument("player", player())
										.then(
												RequiredArgumentBuilder.<CommandSource, ItemId>argument("item", itemId())
														.executes(context -> {
															getPlayer(context, "player").getPlayers(context.getSource()).forEach(player -> {
																int item = getItemId(context, "item").getNumericId();
																context.getSource().sendFeedback("Giving " + player.name + " some " + item);
																LOGGER.info("Giving " + player.name + " some " + item);
																player.dropItem(item, 1, 0);
															});
															return 0;
														})
														.then(
																RequiredArgumentBuilder.<CommandSource, Integer>argument("count", integer(0, 64))
																		.executes(context -> {
																			getPlayer(context, "player").getPlayers(context.getSource()).forEach(player -> {
																				int item = getItemId(context, "item").getNumericId();
																				int count = getInteger(context, "count");
																				context.getSource().sendFeedback("Giving " + player.name + " " + count + " of " + item);
																				LOGGER.info("Giving " + player.name + " " + count + " of " + item);
																				player.dropItem(item, count, 0);
																			});
																			return 0;
																		})
																		.then(
																				RequiredArgumentBuilder.<CommandSource, Integer>argument("meta", integer(0, 15))
																						.executes(context -> {
																							getPlayer(context, "player").getPlayers(context.getSource()).forEach(player -> {
																								int item = getItemId(context, "item").getNumericId();
																								int count = getInteger(context, "count");
																								int meta = getInteger(context, "meta");
																								context.getSource().sendFeedback("Giving " + player.name + " " + count + " of " + item + ":" + meta);
																								LOGGER.info("Giving " + player.name + " " + count + " of " + item + ":" + meta);
																								player.dropItem(item, count, meta);
																							});
																							return 0;
																						})
																		)
														)
										)
						),
				"Whisper something to a player"
		);

		CommandRegistry.register(
				LiteralArgumentBuilder.<CommandSource>literal("time")
				.requires(permission("command.time"))
				.then(
						LiteralArgumentBuilder.<CommandSource>literal("set")
						.then(RequiredArgumentBuilder.<CommandSource, Integer>argument("time", integer(0))
						.executes(context -> {
							((CommandSourceHooks)context.getSource()).getWorld().setLevelTime(getInteger(context, "time"));
							return 0;
						}))
				)
				.then(
						LiteralArgumentBuilder.<CommandSource>literal("get")
						.executes(context -> {
							context.getSource().sendFeedback(Long.toString(((CommandSourceHooks)context.getSource()).getWorld().getLevelTime()));
							return 0;
						})
				),
				"Set or get the time"
		);
	}
}
