package dev.elrol.arrow.mixin;

import com.google.common.collect.Lists;
import dev.elrol.arrow.api.events.PistonEvent;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mixin(PistonHandler.class)
public class PistonHandlerMixin {

    @Final @Shadow private World world;
    @Final @Shadow private BlockPos posFrom;
    @Final @Shadow private boolean retracted;
    @Final @Shadow private Direction motionDirection;
    @Final @Shadow private List<BlockPos> movedBlocks;
    @Final @Shadow private List<BlockPos> brokenBlocks;

    @Inject(method = "calculatePush", at = @At("RETURN"), cancellable = true)
    private void arrowcore$calculatePush(CallbackInfoReturnable<Boolean> cir) {
        if(!cir.getReturnValue()) return;
        Set<BlockPos> blocks = new HashSet<>(movedBlocks);
        movedBlocks.forEach(pos -> {
            blocks.add(pos.offset(motionDirection));
        });

        blocks.addAll(brokenBlocks);
        if(retracted)
            blocks.add(posFrom.offset(motionDirection));

        if(!PistonEvent.EVENT.invoker().activate(world, posFrom, blocks))
            cir.setReturnValue(false);
    }

}
