package com.khrux.justsilver.config;

import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;

public class JustSilverConfig {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final Codec<JustSilverConfig> CODEC = RecordCodecBuilder.create(
		i -> i.group(
				Codec.BOOL.optionalFieldOf("silver", true).forGetter(config -> config.enabled),
				Codec.BOOL.optionalFieldOf("ore_generation", true).forGetter(config -> config.oreGeneration),
				Codec.BOOL.optionalFieldOf("silver_bell_recipe", true).forGetter(config -> config.silverBellRecipe),
				Codec.BOOL.optionalFieldOf("soul_lantern_recipe", true).forGetter(config -> config.soulLanternRecipe)
			)
			.apply(i, JustSilverConfig::new)
	);
	private static JustSilverConfig instance = new JustSilverConfig(true, true, true, true);
	public boolean enabled;
	public boolean oreGeneration;
	public boolean silverBellRecipe;
	public boolean soulLanternRecipe;

	private JustSilverConfig(final boolean enabled, final boolean oreGeneration, final boolean silverBellRecipe, final boolean soulLanternRecipe) {
		this.enabled = enabled;
		this.oreGeneration = oreGeneration;
		this.silverBellRecipe = silverBellRecipe;
		this.soulLanternRecipe = soulLanternRecipe;
	}

	public static JustSilverConfig get() {
		return instance;
	}

	public boolean isEnabled() {
		return this.enabled || !FabricLoader.getInstance().isModLoaded("argent");
	}

	public boolean generatesOre() {
		return this.isEnabled() && this.oreGeneration;
	}

	public void setDefaults() {
		this.oreGeneration = true;
		this.silverBellRecipe = true;
		this.soulLanternRecipe = true;
	}

	private static Path path(final String name) {
		return FabricLoader.getInstance().getConfigDir().resolve(name);
	}

	public static void load() {
		Path path = path("justsilver.json");
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
		Path path = path("justsilver.json");
		try {
			Files.createDirectories(path.getParent());
			Files.writeString(path, CODEC.encodeStart(JsonOps.INSTANCE, this).getOrThrow().toString());
		} catch (IOException e) {
			LOGGER.warn("Failed to save {}", path, e);
		}
	}
}
