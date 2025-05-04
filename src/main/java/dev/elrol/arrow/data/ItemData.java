package dev.elrol.arrow.data;

import dev.elrol.arrow.api.data.IItemData;
import dev.elrol.arrow.libs.ModTranslations;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ItemData implements IItemData {

    String itemName;
    String item;
    int customModelData;

    public ItemData() {
        this.itemName = "NULL";
        this.item = "minecraft:clock";
        this.customModelData = 0;
    }

    public ItemData(String name, Item item, int data) {
        this.itemName = name;
        this.item = Registries.ITEM.getId(item).toString();
        this.customModelData = data;
    }

    @Override
    public ItemStack getCustomItem(int qty) {
        ItemStack stack = new ItemStack(getItem(), qty);

        stack.set(DataComponentTypes.CUSTOM_NAME, ModTranslations.translate("arrow.item." + itemName + ".name"));
        stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(customModelData));

        return stack;
    }

    @Override
    public int getCustomModelData() {
        return customModelData;
    }

    @Override
    public Item getItem() {
        return Registries.ITEM.get(Identifier.of(item));
    }

    @Override
    public String getItemName() {
        return itemName;
    }

    @Override
    public boolean equals(ItemStack stack) {
        CustomModelDataComponent component = stack.get(DataComponentTypes.CUSTOM_MODEL_DATA);
        return component != null && component.value() == getCustomModelData() && stack.isOf(getItem());
    }
}
