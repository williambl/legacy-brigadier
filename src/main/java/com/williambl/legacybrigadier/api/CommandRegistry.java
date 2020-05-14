package com.williambl.legacybrigadier.api;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.williambl.legacybrigadier.LegacyBrigadier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_39;

@Environment(EnvType.SERVER)
public class CommandRegistry {
    public static void register(LiteralArgumentBuilder<class_39> command) {
        LegacyBrigadier.dispatcher.register(command);
    }
}
