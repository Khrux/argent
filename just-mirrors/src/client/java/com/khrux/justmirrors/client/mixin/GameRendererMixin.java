package com.khrux.justmirrors.client.mixin;

import com.khrux.justmirrors.client.JustMirrorsClient;
import com.khrux.justmirrors.client.renderer.MirrorReflections;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@Inject(method = "render", at = @At("HEAD"))
	private void justmirrors$drawReflections(final CallbackInfo ci) {
		MirrorReflections reflections = JustMirrorsClient.reflections();
		if (reflections != null) {
			reflections.draw((GameRenderer)(Object)this);
		}
	}
}
