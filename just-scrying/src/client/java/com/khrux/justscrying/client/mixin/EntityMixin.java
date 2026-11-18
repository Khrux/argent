package com.khrux.justscrying.client.mixin;

import com.khrux.justscrying.client.JustScryingClient;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
	@Inject(method = "isLocalClientAuthoritative", at = @At("HEAD"), cancellable = true)
	private void justscrying$isLocalClientAuthoritative(final CallbackInfoReturnable<Boolean> cir) {
		if (JustScryingClient.scryingView().isPet((Entity)(Object)this)) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "turn", at = @At("HEAD"), cancellable = true)
	private void justscrying$turn(final double xo, final double yo, final CallbackInfo ci) {
		if (JustScryingClient.scryingView().isActive() && JustScryingClient.scryingView().turn((Entity)(Object)this, xo, yo)) {
			ci.cancel();
		}
	}
}
