package com.khrux.argent.mixin;

import com.khrux.argent.world.entity.animal.Scrying;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
	@Inject(method = "isClientAuthoritative", at = @At("HEAD"), cancellable = true)
	private void argent$isClientAuthoritative(final CallbackInfoReturnable<Boolean> cir) {
		if (Scrying.isLinkedPet((Entity)(Object)this)) {
			cir.setReturnValue(true);
		}
	}
}
