package com.williambl.legacybrigadier.api.argument.entityid;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.SERVER)
public class EntityId {
    private final String id;

    EntityId(String id) {
        this.id = id;
    }

    /**
     * Get the id of the entity.
     * @return the id, as a {@link String}.
     */
    public String getId() {
        return id;
    }
}
