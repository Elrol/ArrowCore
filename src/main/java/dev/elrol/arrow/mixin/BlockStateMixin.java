package dev.elrol.arrow.mixin;

import dev.elrol.arrow.api.events.FireSpreadCallback;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class BlockStateMixin {

    @Inject(method = "onBlockAdded", at = @At("HEAD"), cancellable = true)
    private void arrowcore$onBlockAdded(World world, BlockPos pos, BlockState state, boolean notify, CallbackInfo ci) {
        if(state.getBlock() instanceof AbstractFireBlock) {
            if (!FireSpreadCallback.EVENT.invoker().fireSpread(world, pos))
                ci.cancel();
        }
    }

}
