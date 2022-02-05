package com.williambl.legacybrigadier.impl.server.argument;

import com.williambl.legacybrigadier.api.argument.playerselector.TargetSelector;
import com.williambl.legacybrigadier.api.command.ExtendedSender;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

import java.util.Collections;
import java.util.List;

@Environment(EnvType.SERVER)
public class SelfSelector extends TargetSelector<Entity> {
    public SelfSelector() {
        super(Entity.class, null, 1, SortingMethod.RANDOM);
    }

    @Override
    public List<Entity> getEntities(ExtendedSender sender) {
        return Collections.singletonList(sender.getEntity());
    }
}
