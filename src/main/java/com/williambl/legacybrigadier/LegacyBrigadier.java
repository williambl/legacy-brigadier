package com.williambl.legacybrigadier;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.williambl.legacybrigadier.api.CommandRegistry;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_39;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;

@Environment(EnvType.SERVER)
public class LegacyBrigadier implements DedicatedServerModInitializer {

	public static CommandDispatcher<class_39> dispatcher = new CommandDispatcher<>();


	@Override
	public void onInitializeServer() {
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
