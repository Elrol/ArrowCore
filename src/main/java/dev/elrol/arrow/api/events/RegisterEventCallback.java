package dev.elrol.arrow.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface RegisterEventCallback {

    Event<RegisterEventCallback> EVENT = EventFactory.createArrayBacked(RegisterEventCallback.class, (listeners) -> () -> {
        for(RegisterEventCallback listener : listeners) {
            ActionResult result = listener.register();
            if(result != ActionResult.PASS) return result;
        }
        return ActionResult.PASS;
    });

    ActionResult register();

}
