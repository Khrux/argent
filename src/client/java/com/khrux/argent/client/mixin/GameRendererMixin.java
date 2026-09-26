package com.khrux.argent.client.mixin;

import com.khrux.argent.client.ArgentClient;
import com.khrux.argent.client.renderer.MirrorReflections;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@Inject(method = "render", at = @At("HEAD"))
	private void mirror$drawReflections(final CallbackInfo ci) {
		MirrorReflections reflections = ArgentClient.reflections();
		if (reflections != null) {
			reflections.draw((GameRenderer)(Object)this);
		}
	}
}
