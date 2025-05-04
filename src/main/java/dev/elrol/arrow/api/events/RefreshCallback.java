package dev.elrol.arrow.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface RefreshCallback {
    Event<RefreshCallback> EVENT  = EventFactory.createArrayBacked(RefreshCallback.class, (listeners) -> () -> {
        for(RefreshCallback listener : listeners){
            ActionResult result = listener.refresh();
            if(result != ActionResult.PASS) {
                return result;
            }
        }
        return ActionResult.PASS;
    });

    ActionResult refresh();
}
