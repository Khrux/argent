package com.khrux.justspawnlanterns.config;

import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import org.slf4j.Logger;

public class JustSpawnLanternsConfig {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final Codec<JustSpawnLanternsConfig> CODEC = RecordCodecBuilder.create(
		i -> i.group(
				Codec.BOOL.optionalFieldOf("enabled", true).forGetter(config -> config.enabled),
				Markers.CODEC.optionalFieldOf("soul_lantern_mode", Markers.HAND).forGetter(config -> config.markers)
			)
			.apply(i, JustSpawnLanternsConfig::new)
	);
	private static JustSpawnLanternsConfig instance = new JustSpawnLanternsConfig(true, Markers.HAND);
	public boolean enabled;
	public Markers markers;

	private JustSpawnLanternsConfig(final boolean enabled, final Markers markers) {
		this.enabled = enabled;
		this.markers = markers;
	}

	public static JustSpawnLanternsConfig get() {
		return instance;
	}

	public boolean isEnabled() {
		return this.enabled || !FabricLoader.getInstance().isModLoaded("argent");
	}

	public boolean showsMarkers() {
		return this.isEnabled() && this.markers != Markers.OFF;
	}

	public void setDefaults() {
		this.markers = Markers.HAND;
	}

	private static Path path(final String name) {
		return FabricLoader.getInstance().getConfigDir().resolve(name);
	}

	public static void load() {
		Path path = path("justspawnlanterns.json");
		Path legacyPath = path("argent.json");
		if (Files.isRegularFile(path)) {
			read(path);
		} else if (Files.isRegularFile(legacyPath)) {
			read(legacyPath);
		}

		if (!Files.isRegularFile(path)) {
			instance.save();
		}
	}

	private static void read(final Path path) {
		try {
			CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(Files.readString(path))).ifSuccess(config -> instance = config);
		} catch (IOException e) {
			LOGGER.warn("Failed to read {}", path, e);
		}
	}

	public void save() {
		Path path = path("justspawnlanterns.json");
		try {
			Files.createDirectories(path.getParent());
			Files.writeString(path, CODEC.encodeStart(JsonOps.INSTANCE, this).getOrThrow().toString());
		} catch (IOException e) {
			LOGGER.warn("Failed to save {}", path, e);
		}
	}

	public enum Markers implements StringRepresentable {
		OFF("off"),
		HAND("hand"),
		INVENTORY("inventory");

		public static final Codec<Markers> CODEC = StringRepresentable.fromEnum(Markers::values);
		private final String name;

		Markers(final String name) {
			this.name = name;
		}

		public Component getDisplayName() {
			return Component.translatable("options.justspawnlanterns.markers." + this.name);
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}
	}
}
