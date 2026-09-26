package com.khrux.argent.client.mixin;

import com.khrux.argent.client.ArgentClient;
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
	private void mirror$getSkin(final CallbackInfoReturnable<PlayerSkin> cir) {
		UUID player = ((AbstractClientPlayer)(Object)this).getUUID();
		if (ArgentClient.presets() == null || !player.equals(Minecraft.getInstance().getUser().getProfileId()) && ArgentClient.sharedSkins().get(player) == null) {
			return;
		}

		PlayerInfo info = this.getPlayerInfo();
		if (info != null) {
			cir.setReturnValue(info.getSkin());
		}
	}
}
