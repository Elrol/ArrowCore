package dev.elrol.arrow.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public interface MenuCloseCallback {
    Event<MenuCloseCallback> EVENT  = EventFactory.createArrayBacked(MenuCloseCallback.class, (listeners) -> (player) -> {
    for(MenuCloseCallback listener : listeners){
        ActionResult result = listener.onClose(player);
        if(result != ActionResult.PASS) {
            return result;
        }
    }
    return ActionResult.PASS;
});

    ActionResult onClose(ServerPlayerEntity player);
}
