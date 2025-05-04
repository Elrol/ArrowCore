package dev.elrol.arrow.mixin;

import dev.elrol.arrow.api.events.BlockPlaceCallback;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemMixin {

    @Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;Lnet/minecraft/block/BlockState;)Z", at = @At("HEAD"), cancellable = true)
    private void restrict(ItemPlacementContext context, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity playerEntity = context.getPlayer();

        if(playerEntity instanceof ServerPlayerEntity player) {
            ServerWorld world = player.getServerWorld();
            BlockPos pos = context.getBlockPos();
            ItemStack stack = context.getStack();

            boolean allowPlace = BlockPlaceCallback.PRE.invoker().beforeBlockPlace(world, player, pos, state, stack, world.getBlockEntity(pos));
            if(allowPlace) {
                cir.setReturnValue(context.getWorld().setBlockState(pos, state, 11));
                BlockPlaceCallback.POST.invoker().afterBlockPlace(world, player, pos, state, stack, world.getBlockEntity(pos));
            } else {
                cir.setReturnValue(false);
            }
        } else {
            cir.setReturnValue(true);
        }
    }
}