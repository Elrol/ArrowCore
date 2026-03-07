package dev.elrol.arrow.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.item.ItemStack;

import java.math.BigDecimal;
import java.util.UUID;

public interface ItemSoldCallback {

    Event<ItemSoldCallback> EVENT = EventFactory.createArrayBacked(ItemSoldCallback.class, (listeners) -> (uuid, stack, amount, cost, curID) -> {
        for (ItemSoldCallback listener : listeners) {
            listener.sold(uuid, stack, amount, cost, curID);
        }
    });

    void sold(UUID uuid, ItemStack stack, int amount, BigDecimal cost, String curID);

}
