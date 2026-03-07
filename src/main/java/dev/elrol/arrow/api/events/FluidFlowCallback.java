package dev.elrol.arrow.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public interface FluidFlowCallback {

    Event<FluidFlowCallback> EVENT = EventFactory.createArrayBacked(FluidFlowCallback.class, listeners -> (world, pos) -> {
        for (FluidFlowCallback listener : listeners)
            if(!listener.onFlow(world, pos)) return false;
        return true;
    });

    boolean onFlow(BlockView world, BlockPos pos);
}
