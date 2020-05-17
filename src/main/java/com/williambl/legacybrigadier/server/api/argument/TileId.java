package com.williambl.legacybrigadier.server.api.argument;

import io.github.minecraftcursedlegacy.api.registry.Id;
import io.github.minecraftcursedlegacy.api.registry.Registries;
import net.minecraft.tile.Tile;

public class TileId {
    private final int numericId;
    private final Id id;

    public TileId(int numericId) {
        this.numericId = numericId;
        Tile itemType = Registries.TILE.getBySerialisedId(numericId);
        this.id = itemType == null ? new Id("") : Registries.TILE.getId(itemType);
    }

    public TileId(String idString) {
        this.id = new Id(idString);
        Tile tile = Registries.TILE.getById(id);
        if (tile != null) {
            this.numericId = tile.id;
            return;
        }
        try {
            this.numericId = Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Tile ID invalid!");
        }
    }

    public int getNumericId() {
        return numericId;
    }
}
