package com.khrux.justmirrors.config;

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

public class JustMirrorsConfig {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final int MIN_REFLECTION_DISTANCE = 8;
	public static final int MAX_REFLECTION_DISTANCE = 128;
	public static final int REFLECTION_DISTANCE_STEP = 4;
	public static final int DEFAULT_REFLECTION_DISTANCE = 32;
	public static final Codec<JustMirrorsConfig> CODEC = RecordCodecBuilder.create(
		i -> i.group(
				Codec.BOOL.optionalFieldOf("enabled", true).forGetter(config -> config.enabled),
				Codec.BOOL.optionalFieldOf("mirror_screen", true).forGetter(config -> config.mirrorScreen),
				Reflections.CODEC.optionalFieldOf("reflection_mode", Reflections.ALL).forGetter(config -> config.reflections),
				Codec.intRange(MIN_REFLECTION_DISTANCE, MAX_REFLECTION_DISTANCE)
					.optionalFieldOf("reflection_distance", DEFAULT_REFLECTION_DISTANCE)
					.forGetter(config -> config.reflectionDistance)
			)
			.apply(i, JustMirrorsConfig::new)
	);
	private static JustMirrorsConfig instance = new JustMirrorsConfig(true, true, Reflections.ALL, DEFAULT_REFLECTION_DISTANCE);
	public boolean enabled;
	public boolean mirrorScreen;
	public Reflections reflections;
	public int reflectionDistance;

	private JustMirrorsConfig(final boolean enabled, final boolean mirrorScreen, final Reflections reflections, final int reflectionDistance) {
		this.enabled = enabled;
		this.mirrorScreen = mirrorScreen;
		this.reflections = reflections;
		this.reflectionDistance = reflectionDistance;
	}

	public static JustMirrorsConfig get() {
		return instance;
	}

	public boolean isEnabled() {
		return this.enabled || !FabricLoader.getInstance().isModLoaded("argent");
	}

	public void setDefaults() {
		this.mirrorScreen = true;
		this.reflections = Reflections.ALL;
		this.reflectionDistance = DEFAULT_REFLECTION_DISTANCE;
	}

	private static Path path(final String name) {
		return FabricLoader.getInstance().getConfigDir().resolve(name);
	}

	public static void load() {
		Path path = path("justmirrors.json");
		if (Files.isRegularFile(path)) {
			read(path);
		} else {
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
		Path path = path("justmirrors.json");
		try {
			Files.createDirectories(path.getParent());
			Files.writeString(path, CODEC.encodeStart(JsonOps.INSTANCE, this).getOrThrow().toString());
		} catch (IOException e) {
			LOGGER.warn("Failed to save {}", path, e);
		}
	}

	public enum Reflections implements StringRepresentable {
		OFF("off"),
		ENTITIES("entities"),
		ALL("all");

		public static final Codec<Reflections> CODEC = StringRepresentable.fromEnum(Reflections::values);
		private final String name;

		Reflections(final String name) {
			this.name = name;
		}

		public Component getDisplayName() {
			return Component.translatable("options.justmirrors.reflections." + this.name);
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}
	}
}
