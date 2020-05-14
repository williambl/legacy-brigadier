package com.williambl.legacybrigadier;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
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
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		dispatcher.register(
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
						})
		);
	}
}
