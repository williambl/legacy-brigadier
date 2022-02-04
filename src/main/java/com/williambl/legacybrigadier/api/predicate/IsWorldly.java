package com.williambl.legacybrigadier.api.predicate;

import com.williambl.legacybrigadier.api.command.ExtendedSender;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.function.Predicate;

@Environment(EnvType.SERVER)
public class IsWorldly implements Predicate<ExtendedSender> {

    private IsWorldly() {}

    /**
     * Create a predicate that requires the command source to have a level.
     * @return the predicate.
     */
    public static IsWorldly isWorldly() {
        return new IsWorldly();
    }

    @Override
    public boolean test(ExtendedSender commandSource) {
        return commandSource.getWorld() != null;
    }
}
