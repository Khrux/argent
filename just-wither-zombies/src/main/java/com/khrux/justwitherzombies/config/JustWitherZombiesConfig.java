package com.khrux.justwitherzombies.config;

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

public class JustWitherZombiesConfig {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final Codec<JustWitherZombiesConfig> CODEC = RecordCodecBuilder.create(
		i -> i.group(
				Codec.BOOL.optionalFieldOf("wither_zombies", true).forGetter(config -> config.enabled),
				Codec.BOOL.optionalFieldOf("melee_damage", false).forGetter(config -> config.meleeDamage),
				Codec.BOOL.optionalFieldOf("item_stealing", true).forGetter(config -> config.itemStealing),
				Codec.BOOL.optionalFieldOf("scrying_books", true).forGetter(config -> config.scryingBooks)
			)
			.apply(i, JustWitherZombiesConfig::new)
	);
	private static JustWitherZombiesConfig instance = new JustWitherZombiesConfig(true, false, true, true);
	public boolean enabled;
	public boolean meleeDamage;
	public boolean itemStealing;
	public boolean scryingBooks;

	private JustWitherZombiesConfig(final boolean enabled, final boolean meleeDamage, final boolean itemStealing, final boolean scryingBooks) {
		this.enabled = enabled;
		this.meleeDamage = meleeDamage;
		this.itemStealing = itemStealing;
		this.scryingBooks = scryingBooks;
	}

	public static JustWitherZombiesConfig get() {
		return instance;
	}

	public boolean isEnabled() {
		return this.enabled || !FabricLoader.getInstance().isModLoaded("argent");
	}

	public void setDefaults() {
		this.meleeDamage = false;
		this.itemStealing = true;
		this.scryingBooks = true;
	}

	private static Path path(final String name) {
		return FabricLoader.getInstance().getConfigDir().resolve(name);
	}

	public static void load() {
		Path path = path("justwitherzombies.json");
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
		Path path = path("justwitherzombies.json");
		try {
			Files.createDirectories(path.getParent());
			Files.writeString(path, CODEC.encodeStart(JsonOps.INSTANCE, this).getOrThrow().toString());
		} catch (IOException e) {
			LOGGER.warn("Failed to save {}", path, e);
		}
	}
}
