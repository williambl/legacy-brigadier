package com.williambl.legacybrigadier.impl.mixin;

import io.github.minecraftcursedlegacy.impl.command.ServerCommandSender;
import net.minecraft.server.command.CommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerCommandSender.class)
public interface ServerCommandSenderAccessor {
    @Accessor
    public CommandSource getSource();
}
