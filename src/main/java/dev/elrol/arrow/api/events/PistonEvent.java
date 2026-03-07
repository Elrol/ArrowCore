package dev.elrol.arrow.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Set;

public interface PistonEvent {

    Event<PistonEvent> EVENT = EventFactory.createArrayBacked(PistonEvent.class, (listeners) -> (world, pistonPos, blocks) -> {
        for(PistonEvent pistonEvent : listeners)
            if(!pistonEvent.activate(world, pistonPos, blocks)) return false;

        return true;
    });

    boolean activate(World world, BlockPos pistonPos, Set<BlockPos> blocks);

}
