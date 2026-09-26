package com.khrux.argent.client.skin;

import com.khrux.argent.Argent;
import com.khrux.argent.network.protocol.PlayerSkinUpdatePayload;
import com.khrux.argent.network.protocol.SkinUpdatePayload;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.world.entity.player.PlayerModelType;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class SharedSkins {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final int SKIN_SIZE = 64;
	private final Map<UUID, SkinPreset> skins = new HashMap<>();
	private byte @Nullable [] sentSkin;
	private @Nullable PlayerModelType sentModel;

	public @Nullable SkinPreset get(final UUID player) {
		return this.skins.get(player);
	}

	public void accept(final PlayerSkinUpdatePayload payload) {
		if (payload.isRemoved()) {
			SkinPreset removed = this.skins.remove(payload.player());
			if (removed != null) {
				removed.close();
			}

			return;
		}

		NativeImage image;
		try {
			image = NativeImage.read(payload.skin());
		} catch (IOException e) {
			LOGGER.warn("Received an unreadable skin for {}", payload.player(), e);
			return;
		}

		if (image.getWidth() != SKIN_SIZE || image.getHeight() != SKIN_SIZE) {
			image.close();
			return;
		}

		this.skins.put(payload.player(), new SkinPreset(Argent.id("shared/" + payload.player()), image, payload.model()));
	}

	public void sendSelected(final SkinPresets presets) {
		if (!ClientPlayNetworking.canSend(SkinUpdatePayload.TYPE)) {
			return;
		}

		PlayerModelType model = presets.selected().model();
		byte[] skin;
		try {
			skin = Files.readAllBytes(presets.imagePath(presets.selectedIndex()));
		} catch (IOException e) {
			LOGGER.warn("Failed to read the selected skin preset", e);
			return;
		}

		if (skin.length > SkinUpdatePayload.MAX_SKIN_SIZE || model == this.sentModel && Arrays.equals(skin, this.sentSkin)) {
			return;
		}

		ClientPlayNetworking.send(new SkinUpdatePayload(skin, model));
		this.sentSkin = skin;
		this.sentModel = model;
	}

	public void clear() {
		this.skins.values().forEach(SkinPreset::close);
		this.skins.clear();
		this.sentSkin = null;
		this.sentModel = null;
	}
}
