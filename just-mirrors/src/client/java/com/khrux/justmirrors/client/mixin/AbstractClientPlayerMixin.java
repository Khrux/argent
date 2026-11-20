package com.khrux.justmirrors.client.mixin;

import com.khrux.justmirrors.client.JustMirrorsClient;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin {
	@Shadow
	protected abstract PlayerInfo getPlayerInfo();

	@Inject(method = "getSkin", at = @At("HEAD"), cancellable = true)
	private void justmirrors$getSkin(final CallbackInfoReturnable<PlayerSkin> cir) {
		UUID player = ((AbstractClientPlayer)(Object)this).getUUID();
		if (JustMirrorsClient.presets() == null || !player.equals(Minecraft.getInstance().getUser().getProfileId()) && JustMirrorsClient.sharedSkins().get(player) == null) {
			return;
		}

		PlayerInfo info = this.getPlayerInfo();
		if (info != null) {
			cir.setReturnValue(info.getSkin());
		}
	}
}
