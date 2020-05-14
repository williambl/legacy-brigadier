package com.williambl.legacybrigadier;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.williambl.legacybrigadier.api.CommandRegistry;
import com.williambl.legacybrigadier.network.LegacyBrigadierPluginChannelServer;
import io.github.minecraftcursedlegacy.api.networking.PluginChannelRegistry;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_39;

import java.util.concurrent.ExecutionException;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;

@Environment(EnvType.SERVER)
public class LegacyBrigadierServer implements DedicatedServerModInitializer {

	public static CommandDispatcher<class_39> dispatcher = new CommandDispatcher<>();

	public static final LegacyBrigadierPluginChannelServer CHANNEL = new LegacyBrigadierPluginChannelServer();

	@Override
	public void onInitializeServer() {

		PluginChannelRegistry.registerPluginChannel(CHANNEL);

		CommandRegistry.register(
				LiteralArgumentBuilder.<class_39>literal("suggestsettile")
				.executes(context -> {
					ParseResults<class_39> parseResults = LegacyBrigadierServer.dispatcher.parse("setti", context.getSource());
					try {
						context.getSource().method_1409(LegacyBrigadierServer.dispatcher.getCompletionSuggestions(parseResults).get().toString());
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}
					return 1;
				})
		);

		CommandRegistry.register(
				LiteralArgumentBuilder.<class_39>literal("foo")
						.then(
								RequiredArgumentBuilder.<class_39, Integer>argument("bar", integer())
										.executes(c -> {
											c.getSource().method_1409("Bar is " + getInteger(c, "bar"));
											return 1;
										})
						)
						.executes(c -> {
							c.getSource().method_1409("Called foo with no arguments");
							return 1;
						}),
				"Foos a bar"
		);
		CommandRegistry.register(
				LiteralArgumentBuilder.<class_39>literal("settile")
				.then(
						RequiredArgumentBuilder.<class_39, Integer>argument("x", integer())
								.then(
										RequiredArgumentBuilder.<class_39, Integer>argument("y", integer())
												.then(
														RequiredArgumentBuilder.<class_39, Integer>argument("z", integer())
																.then(
																		RequiredArgumentBuilder.<class_39, Integer>argument("id", integer())
																				.executes(context -> {
																					((CommandSourceHooks) context.getSource()).getWorld().setTile(getInteger(context, "x"), getInteger(context, "y"), getInteger(context, "z"), getInteger(context, "id"));
																					return 0;
																				})
																		.then(
																				RequiredArgumentBuilder.<class_39, Integer>argument("meta", integer())
																				.executes(context -> {
																					((CommandSourceHooks) context.getSource()).getWorld().method_201(getInteger(context, "x"), getInteger(context, "y"), getInteger(context, "z"), getInteger(context, "id"), getInteger(context, "meta"));
																					return 0;
																				})
																		)
																)
												)
								)
				),
				"Set a tile"
		);
	}
}
