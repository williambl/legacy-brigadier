package com.williambl.legacybrigadier.server.api.argument;

import io.github.minecraftcursedlegacy.api.registry.Id;
import io.github.minecraftcursedlegacy.api.registry.Registries;
import net.minecraft.item.ItemType;

public class ItemId {
    private final int numericId;
    private final Id id;

    public ItemId(int numericId) {
        this.numericId = numericId;
        ItemType itemType = Registries.ITEM_TYPE.getBySerialisedId(numericId);
        this.id = itemType == null ? new Id("") : Registries.ITEM_TYPE.getId(itemType);
    }

    public ItemId(String idString) {
        this.id = new Id(idString);
        ItemType itemType = Registries.ITEM_TYPE.getById(id);
        if (itemType != null) {
            this.numericId = itemType.id;
            return;
        }
        try {
            this.numericId = Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Item ID invalid!");
        }
    }

    public int getNumericId() {
        return numericId;
    }
}
