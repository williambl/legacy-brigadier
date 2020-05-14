package com.williambl.legacybrigadier.mixin;

import com.williambl.legacybrigadier.CommandSourceHooks;
import net.minecraft.class_39;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(class_39.class)
public interface CommandSourceMixin extends CommandSourceHooks {}
