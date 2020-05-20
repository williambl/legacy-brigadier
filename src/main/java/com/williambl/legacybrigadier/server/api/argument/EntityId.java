package com.williambl.legacybrigadier.server.api.argument;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.SERVER)
public class EntityId {
    private final String id;

    public EntityId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
