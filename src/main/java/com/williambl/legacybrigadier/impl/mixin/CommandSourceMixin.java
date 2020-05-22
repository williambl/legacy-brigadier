package com.williambl.legacybrigadier.impl.mixin;

import com.williambl.legacybrigadier.impl.server.mixinhooks.CommandSourceHooks;
import net.minecraft.server.command.CommandSource;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CommandSource.class)
public interface CommandSourceMixin extends CommandSourceHooks {}
