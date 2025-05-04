package dev.elrol.arrow.mixin;

import dev.elrol.arrow.api.events.FinishBrushingCallback;
import net.minecraft.block.entity.BrushableBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrushableBlockEntity.class)
public class BrushableBlockEntityMixin {

    @Inject(method = "finishBrushing", at = @At("TAIL"))
    private void injected(PlayerEntity player, CallbackInfo ci) {
        if(player instanceof ServerPlayerEntity serverPlayer) {
            FinishBrushingCallback.EVENT.invoker().finish(serverPlayer);
        }
    }

}