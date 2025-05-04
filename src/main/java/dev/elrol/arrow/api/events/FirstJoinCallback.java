package dev.elrol.arrow.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public interface FirstJoinCallback {

    Event<FirstJoinCallback> EVENT  = EventFactory.createArrayBacked(FirstJoinCallback.class, (listeners) -> (player) -> {
        for(FirstJoinCallback listener : listeners){
            ActionResult result = listener.welcome(player);
            if(result != ActionResult.PASS) {
                return result;
            }
        }
        return ActionResult.PASS;
    });

    ActionResult welcome(ServerPlayerEntity player);

}
