package com.williambl.legacybrigadier.server.api.argument;

import io.github.minecraftcursedlegacy.api.registry.Id;
import io.github.minecraftcursedlegacy.api.registry.Registries;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.tile.Tile;

@Environment(EnvType.SERVER)
public class TileId {
    private final int numericId;
    private final Id id;

    TileId(int numericId) {
        this.numericId = numericId;
        Tile itemType = Registries.TILE.getBySerialisedId(numericId);
        this.id = itemType == null ? new Id("") : Registries.TILE.getId(itemType);
    }

    TileId(String idString) {
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

    /**
     * Get the numeric id of the item.
     * @return the numeric id of the item.
     */
    public int getNumericId() {
        return numericId;
    }
}
