package dev.elrol.arrow.api.events;

import dev.elrol.arrow.menus._ModMenu;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface MenuCloseCallback {
    Event<MenuCloseCallback> EVENT  = EventFactory.createArrayBacked(MenuCloseCallback.class, (listeners) -> (menu) -> {
    for(MenuCloseCallback listener : listeners) {
        ActionResult result = listener.onClose(menu);
        if(result != ActionResult.PASS) {
            return result;
        }
    }
    return ActionResult.PASS;
});

    ActionResult onClose(_ModMenu menu);
}
