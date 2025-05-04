package dev.elrol.arrow.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public interface FinishBrushingCallback {

    Event<FinishBrushingCallback> EVENT  = EventFactory.createArrayBacked(FinishBrushingCallback.class, (listeners) -> (player) -> {
        for(FinishBrushingCallback listener : listeners){
            ActionResult result = listener.finish(player);
            if(result != ActionResult.PASS) {
                return result;
            }
        }
        return ActionResult.PASS;
    });

    ActionResult finish(ServerPlayerEntity player);

}
