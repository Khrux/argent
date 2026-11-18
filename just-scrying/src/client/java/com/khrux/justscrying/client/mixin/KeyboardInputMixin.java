package com.khrux.justscrying.client.mixin;

import com.khrux.justscrying.client.JustScryingClient;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends ClientInput {
	@Inject(method = "tick", at = @At("TAIL"))
	private void justscrying$tick(final CallbackInfo ci) {
		if (!JustScryingClient.scryingView().isActive()) {
			return;
		}

		JustScryingClient.scryingView().captureInput(this.keyPresses, this.moveVector);
		this.keyPresses = Input.EMPTY;
		this.moveVector = Vec2.ZERO;
	}
}
