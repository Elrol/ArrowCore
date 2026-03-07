package dev.elrol.arrow.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface FireSpreadCallback {

    Event<FireSpreadCallback> EVENT = EventFactory.createArrayBacked(FireSpreadCallback.class, (listeners) -> (world, pos) -> {
        for (FireSpreadCallback event : listeners) {
            if(!event.fireSpread(world, pos)) return false;
        }
        return true;
    });

    boolean fireSpread(World world, BlockPos pos);

}
