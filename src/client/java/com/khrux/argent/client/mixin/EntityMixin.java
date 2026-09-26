package com.khrux.argent.client.mixin;

import com.khrux.argent.client.ArgentClient;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
	@Inject(method = "isLocalClientAuthoritative", at = @At("HEAD"), cancellable = true)
	private void argent$isLocalClientAuthoritative(final CallbackInfoReturnable<Boolean> cir) {
		if (ArgentClient.scryingView().isPet((Entity)(Object)this)) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "turn", at = @At("HEAD"), cancellable = true)
	private void argent$turn(final double xo, final double yo, final CallbackInfo ci) {
		if (ArgentClient.scryingView().isActive() && ArgentClient.scryingView().turn((Entity)(Object)this, xo, yo)) {
			ci.cancel();
		}
	}
}
