package dev.elrol.arrow.api.registries;

import dev.elrol.arrow.libs.ModUtils;
import net.minecraft.item.Item;

public interface IItemRegistry {

    default Item get(String id) {
        return ModUtils.getItem(getModID() + ":" + id);
    }

    String getModID();

}
