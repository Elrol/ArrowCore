package dev.elrol.arrow.mixin;

import dev.elrol.arrow.api.events.FluidFlowCallback;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowableFluid.class)
public class FlowableFluidMixin {

    @Inject(method = "canFlow", at = @At("HEAD"), cancellable = true)
    private void arrowcore$canFlow(BlockView world, BlockPos fluidPos, BlockState fluidBlockState, Direction flowDirection, BlockPos targetPos, BlockState targetBlockState, FluidState fluidState, Fluid fluid, CallbackInfoReturnable<Boolean> cir) {
        if(!FluidFlowCallback.EVENT.invoker().onFlow(world, targetPos)) cir.setReturnValue(false);
    }

}
