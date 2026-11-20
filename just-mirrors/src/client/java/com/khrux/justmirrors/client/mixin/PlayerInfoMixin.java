package com.khrux.justmirrors.client.mixin;

import com.khrux.justmirrors.client.JustMirrorsClient;
import com.khrux.justmirrors.client.skin.SkinPreset;
import com.khrux.justmirrors.client.skin.SkinPresets;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.entity.player.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInfo.class)
public abstract class PlayerInfoMixin {
	@Inject(method = "getSkin", at = @At("RETURN"), cancellable = true)
	private void justmirrors$getSkin(final CallbackInfoReturnable<PlayerSkin> cir) {
		SkinPresets presets = JustMirrorsClient.presets();
		if (presets == null) {
			return;
		}

		UUID player = ((PlayerInfo)(Object)this).getProfile().id();
		if (player.equals(Minecraft.getInstance().getUser().getProfileId())) {
			cir.setReturnValue(presets.selected().skin(cir.getReturnValue()));
			return;
		}

		SkinPreset shared = JustMirrorsClient.sharedSkins().get(player);
		if (shared != null) {
			cir.setReturnValue(shared.skin(cir.getReturnValue()));
		}
	}
}
