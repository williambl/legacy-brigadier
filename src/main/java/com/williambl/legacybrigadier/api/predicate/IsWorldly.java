package com.williambl.legacybrigadier.api.predicate;

import com.williambl.legacybrigadier.impl.server.mixinhooks.CommandSourceHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.command.CommandSource;

import java.util.function.Predicate;

@Environment(EnvType.SERVER)
public class IsWorldly implements Predicate<CommandSource> {

    private IsWorldly() {}

    /**
     * Create a predicate that requires the command source to have a level.
     * @return the predicate.
     */
    public static IsWorldly isWorldly() {
        return new IsWorldly();
    }

    @Override
    public boolean test(CommandSource commandSource) {
        return ((CommandSourceHooks)commandSource).getWorld() != null;
    }
}
