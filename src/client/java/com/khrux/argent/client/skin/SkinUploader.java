package com.khrux.argent.client.skin;

import com.mojang.logging.LogUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.PlayerModelType;
import org.slf4j.Logger;

public class SkinUploader {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final URI SKINS_URI = URI.create("https://api.minecraftservices.com/minecraft/profile/skins");
	private static final int HTTP_OK = 200;

	public static CompletableFuture<Boolean> upload(final Minecraft minecraft, final Path file, final PlayerModelType model) {
		String accessToken = minecraft.getUser().getAccessToken();
		return CompletableFuture.supplyAsync(() -> send(accessToken, file, model), Util.nonCriticalIoPool());
	}

	private static boolean send(final String accessToken, final Path file, final PlayerModelType model) {
		String boundary = UUID.randomUUID().toString();
		try (HttpClient client = HttpClient.newHttpClient()) {
			HttpRequest request = HttpRequest.newBuilder(SKINS_URI)
				.header("Authorization", "Bearer " + accessToken)
				.header("Content-Type", "multipart/form-data; boundary=" + boundary)
				.POST(HttpRequest.BodyPublishers.ofByteArray(body(boundary, Files.readAllBytes(file), model)))
				.build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() == HTTP_OK) {
				return true;
			}

			LOGGER.warn("Skin upload failed with status {}: {}", response.statusCode(), response.body());
			return false;
		} catch (IOException | InterruptedException e) {
			LOGGER.warn("Failed to upload skin from {}", file, e);
			return false;
		}
	}

	private static byte[] body(final String boundary, final byte[] png, final PlayerModelType model) throws IOException {
		String variant = model == PlayerModelType.SLIM ? "slim" : "classic";
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		write(output, "--" + boundary + "\r\nContent-Disposition: form-data; name=\"variant\"\r\n\r\n" + variant + "\r\n");
		write(output, "--" + boundary + "\r\nContent-Disposition: form-data; name=\"file\"; filename=\"skin.png\"\r\nContent-Type: image/png\r\n\r\n");
		output.write(png);
		write(output, "\r\n--" + boundary + "--\r\n");
		return output.toByteArray();
	}

	private static void write(final ByteArrayOutputStream output, final String text) {
		output.writeBytes(text.getBytes(StandardCharsets.UTF_8));
	}
}
