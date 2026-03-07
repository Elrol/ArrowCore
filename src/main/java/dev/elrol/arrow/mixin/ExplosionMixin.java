package dev.elrol.arrow.mixin;

import dev.elrol.arrow.api.events.ExplosionCallback;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Explosion.class)
public class ExplosionMixin {

    @Final
    @Shadow
    private Entity entity;

    @Final
    @Shadow
    private World world;

    @Final
    @Shadow
    private List<BlockPos> affectedBlocks;

    @Inject(method = "collectBlocksAndDamageEntities", at = @At("TAIL"))
    private void arrowcore$collectBlocksAndDamageEntities(CallbackInfo ci) {
        if(world instanceof ServerWorld serverWorld)
            ExplosionCallback.EVENT.invoker().boom(serverWorld, entity, affectedBlocks);

    }



}
