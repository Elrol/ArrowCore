package dev.elrol.arrow.api.data;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IItemData {

    ItemStack getCustomItem(int qty);
    int getCustomModelData();
    Item getItem();
    String getItemName();
    boolean equals(ItemStack stack);

}
