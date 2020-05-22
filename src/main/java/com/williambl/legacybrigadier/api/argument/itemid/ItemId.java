package com.williambl.legacybrigadier.api.argument.itemid;

import io.github.minecraftcursedlegacy.api.registry.Id;
import io.github.minecraftcursedlegacy.api.registry.Registries;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemType;

@Environment(EnvType.SERVER)
public class ItemId {
    private final int numericId;
    private final Id id;

    ItemId(int numericId) {
        this.numericId = numericId;
        ItemType itemType = Registries.ITEM_TYPE.getBySerialisedId(numericId);
        this.id = itemType == null ? new Id("") : Registries.ITEM_TYPE.getId(itemType);
    }

    ItemId(String idString) {
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

    /**
     * Get the numeric id of the item.
     * @return the numeric id of the item.
     */
    public int getNumericId() {
        return numericId;
    }
}
