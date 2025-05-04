package dev.elrol.arrow.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public final class BlockPlaceCallback {

    public static final Event<Pre> PRE = EventFactory.createArrayBacked(Pre.class, (listeners) -> (world, player, pos, state, stack, entity) -> {
        for(Pre listener : listeners) {
            boolean result = listener.beforeBlockPlace(world, player, pos, state, stack, entity);
            if(!result) return false;
        }
        return true;
    });

    public static final Event<Post> POST = EventFactory.createArrayBacked(Post.class, (listeners) -> (world, player, pos, state, stack, entity) -> {
        for(Post listener : listeners) {
            listener.afterBlockPlace(world, player, pos, state, stack, entity);
        }
    });

    @FunctionalInterface
    public interface Pre {
        boolean beforeBlockPlace(ServerWorld world, ServerPlayerEntity player, BlockPos pos, BlockState state, ItemStack stack, BlockEntity entity);
    }

    @FunctionalInterface
    public interface Post {
        void afterBlockPlace(ServerWorld world, ServerPlayerEntity player, BlockPos pos, BlockState state, ItemStack stack, BlockEntity entity);
    }
}
