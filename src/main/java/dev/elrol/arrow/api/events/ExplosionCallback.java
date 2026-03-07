package dev.elrol.arrow.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ExplosionCallback {

    Event<ExplosionCallback> EVENT = EventFactory.createArrayBacked(ExplosionCallback.class, listeners -> (world, entity, affectedBlocks) -> {
        for (ExplosionCallback listener : listeners) {
            listener.boom(world, entity, affectedBlocks);
        }
    });

    void boom(ServerWorld world, @Nullable Entity entity, List<BlockPos> affectedBlocks);

}
