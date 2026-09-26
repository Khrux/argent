package com.khrux.argent.client.skin;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import com.mojang.util.UndashedUuid;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.core.ClientAsset;
import net.minecraft.server.players.ProfileResolver;
import net.minecraft.util.StringUtil;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.PlayerModelType;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class SkinImporter {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final int SKIN_SIZE = 64;
	private static final int HTTP_OK = 200;

	public static CompletableFuture<Boolean> importInput(final Minecraft minecraft, final SkinPreset preset, final String input) {
		String trimmed = input.trim();
		if (trimmed.startsWith("https://") || trimmed.startsWith("http://")) {
			return download(trimmed).thenApplyAsync(image -> apply(preset, image, preset.model()), minecraft);
		}

		return CompletableFuture.supplyAsync(() -> resolveProfile(minecraft, trimmed), Util.nonCriticalIoPool())
			.thenCompose(profile -> profile.isPresent() ? importProfile(minecraft, preset, profile.get()) : CompletableFuture.completedFuture(false));
	}

	private static Optional<GameProfile> resolveProfile(final Minecraft minecraft, final String input) {
		ProfileResolver resolver = minecraft.services().profileResolver();
		if (StringUtil.isValidPlayerName(input)) {
			return resolver.fetchByName(input);
		}

		try {
			return resolver.fetchById(UndashedUuid.fromStringLenient(input));
		} catch (IllegalArgumentException e) {
			return Optional.empty();
		}
	}

	public static CompletableFuture<Boolean> importProfile(final Minecraft minecraft, final SkinPreset preset, final GameProfile profile) {
		return minecraft.getSkinManager().get(profile).thenCompose(skin -> {
			if (skin.isEmpty() || !(skin.get().body() instanceof ClientAsset.DownloadedTexture texture)) {
				return CompletableFuture.completedFuture(false);
			}

			PlayerModelType model = skin.get().model();
			return download(texture.url()).thenApplyAsync(image -> apply(preset, image, model), minecraft);
		});
	}

	public static CompletableFuture<Boolean> importFile(final Minecraft minecraft, final SkinPreset preset, final Path path) {
		return CompletableFuture.supplyAsync(() -> read(path), Util.nonCriticalIoPool()).thenApplyAsync(image -> apply(preset, image, preset.model()), minecraft);
	}

	private static @Nullable NativeImage read(final Path path) {
		try (InputStream stream = Files.newInputStream(path)) {
			return NativeImage.read(stream);
		} catch (IOException e) {
			LOGGER.warn("Failed to read skin from {}", path, e);
			return null;
		}
	}

	private static CompletableFuture<@Nullable NativeImage> download(final String url) {
		return CompletableFuture.supplyAsync(() -> {
			try (HttpClient client = HttpClient.newHttpClient()) {
				HttpResponse<InputStream> response = client.send(HttpRequest.newBuilder(URI.create(url)).build(), HttpResponse.BodyHandlers.ofInputStream());
				try (InputStream stream = response.body()) {
					return response.statusCode() == HTTP_OK ? NativeImage.read(stream) : null;
				}
			} catch (IOException | InterruptedException | IllegalArgumentException e) {
				LOGGER.warn("Failed to download skin from {}", url, e);
				return null;
			}
		}, Util.nonCriticalIoPool());
	}

	private static boolean apply(final SkinPreset preset, final @Nullable NativeImage image, final PlayerModelType model) {
		if (image == null) {
			return false;
		}

		if (image.getWidth() != SKIN_SIZE || image.getHeight() != SKIN_SIZE) {
			image.close();
			return false;
		}

		preset.replace(image, model);
		return true;
	}
}
