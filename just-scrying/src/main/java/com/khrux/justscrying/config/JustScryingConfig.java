package com.khrux.justscrying.config;

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

public class JustScryingConfig {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final Codec<JustScryingConfig> CODEC = RecordCodecBuilder.create(
		i -> i.group(
				Codec.BOOL.optionalFieldOf("enabled", true).forGetter(config -> config.enabled),
				Codec.BOOL.optionalFieldOf("scrying", true).forGetter(config -> config.effect),
				Codec.BOOL.optionalFieldOf("scrying_library_loot", false).forGetter(config -> config.libraryBooks),
				Codec.BOOL.optionalFieldOf("parrot_armor_recipe", true).forGetter(config -> config.parrotArmorRecipe),
				Codec.BOOL.optionalFieldOf("cat_armor_recipe", true).forGetter(config -> config.catArmorRecipe)
			)
			.apply(i, JustScryingConfig::new)
	);
	private static JustScryingConfig instance = new JustScryingConfig(true, true, false, true, true);
	public boolean enabled;
	public boolean effect;
	public boolean libraryBooks;
	public boolean parrotArmorRecipe;
	public boolean catArmorRecipe;

	private JustScryingConfig(
		final boolean enabled, final boolean effect, final boolean libraryBooks, final boolean parrotArmorRecipe, final boolean catArmorRecipe
	) {
		this.enabled = enabled;
		this.effect = effect;
		this.libraryBooks = libraryBooks;
		this.parrotArmorRecipe = parrotArmorRecipe;
		this.catArmorRecipe = catArmorRecipe;
	}

	public static JustScryingConfig get() {
		return instance;
	}

	public boolean isEnabled() {
		return this.enabled || !FabricLoader.getInstance().isModLoaded("argent");
	}

	public boolean scryingWorks() {
		return this.isEnabled() && this.effect;
	}

	public void setDefaults() {
		this.effect = true;
		this.libraryBooks = false;
		this.parrotArmorRecipe = true;
		this.catArmorRecipe = true;
	}

	private static Path path(final String name) {
		return FabricLoader.getInstance().getConfigDir().resolve(name);
	}

	public static void load() {
		Path path = path("justscrying.json");
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
		Path path = path("justscrying.json");
		try {
			Files.createDirectories(path.getParent());
			Files.writeString(path, CODEC.encodeStart(JsonOps.INSTANCE, this).getOrThrow().toString());
		} catch (IOException e) {
			LOGGER.warn("Failed to save {}", path, e);
		}
	}
}
