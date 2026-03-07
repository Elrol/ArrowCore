package dev.elrol.arrow.mixin;

import dev.elrol.arrow.api.events.FireSpreadCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.FireBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireBlock.class)
public class FireBlockMixin {

    @Inject(method = "trySpreadingFire", at = @At("HEAD"), cancellable = true)
    private void arrowcore$trySpreadingFire(World world, BlockPos pos, int spreadFactor, Random random, int currentAge, CallbackInfo ci) {
        if(!FireSpreadCallback.EVENT.invoker().fireSpread(world, pos))
            ci.cancel();
    }

    @Inject(method = "scheduledTick", at = @At("HEAD"), cancellable = true)
    private void arrowcore$scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if(!FireSpreadCallback.EVENT.invoker().fireSpread(world, pos))
            ci.cancel();
    }
}
