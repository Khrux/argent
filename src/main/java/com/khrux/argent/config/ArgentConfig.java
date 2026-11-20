package com.khrux.argent.config;

import com.google.gson.JsonParser;
import com.khrux.justmirrors.config.JustMirrorsConfig;
import com.khrux.justscrying.config.JustScryingConfig;
import com.khrux.justsilver.config.JustSilverConfig;
import com.khrux.justspawnlanterns.config.JustSpawnLanternsConfig;
import com.khrux.justwitherzombies.config.JustWitherZombiesConfig;
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

public class ArgentConfig {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final Codec<ArgentConfig> CODEC = RecordCodecBuilder.create(
		i -> i.group(
				Mode.CODEC.lenientOptionalFieldOf("silver_mode", Mode.ENABLED).forGetter(config -> config.silver),
				Mode.CODEC.lenientOptionalFieldOf("mirrors_mode", Mode.ENABLED).forGetter(config -> config.mirrors),
				Mode.CODEC.lenientOptionalFieldOf("spawn_lanterns_mode", Mode.ENABLED).forGetter(config -> config.spawnLanterns),
				Mode.CODEC.lenientOptionalFieldOf("wither_zombie_mode", Mode.ENABLED).forGetter(config -> config.witherZombie),
				Mode.CODEC.lenientOptionalFieldOf("scrying_mode", Mode.ENABLED).forGetter(config -> config.scrying),
				Codec.BOOL.optionalFieldOf("silver_mirror_recipe", true).forGetter(config -> config.silverMirrorRecipe),
				Codec.BOOL.optionalFieldOf("diamond_mirror_recipe", false).forGetter(config -> config.diamondMirrorRecipe)
			)
			.apply(i, ArgentConfig::new)
	);
	private static ArgentConfig instance = new ArgentConfig(Mode.ENABLED, Mode.ENABLED, Mode.ENABLED, Mode.ENABLED, Mode.ENABLED, true, false);
	public Mode silver;
	public Mode mirrors;
	public Mode spawnLanterns;
	public Mode witherZombie;
	public Mode scrying;
	public boolean silverMirrorRecipe;
	public boolean diamondMirrorRecipe;

	private ArgentConfig(
		final Mode silver,
		final Mode mirrors,
		final Mode spawnLanterns,
		final Mode witherZombie,
		final Mode scrying,
		final boolean silverMirrorRecipe,
		final boolean diamondMirrorRecipe
	) {
		this.silver = silver;
		this.mirrors = mirrors;
		this.spawnLanterns = spawnLanterns;
		this.witherZombie = witherZombie;
		this.scrying = scrying;
		this.silverMirrorRecipe = silverMirrorRecipe;
		this.diamondMirrorRecipe = diamondMirrorRecipe;
	}

	public static ArgentConfig get() {
		return instance;
	}

	public boolean usesMirrorRecipe(final MirrorRecipe recipe) {
		if (!JustMirrorsConfig.get().isEnabled()) {
			return false;
		}

		if (this.mirrors == Mode.CUSTOM) {
			return recipe == MirrorRecipe.SILVER ? this.silverMirrorRecipe && JustSilverConfig.get().isEnabled() : this.diamondMirrorRecipe;
		}

		return recipe == MirrorRecipe.SILVER == JustSilverConfig.get().generatesOre();
	}

	public Mode mode(final Section section) {
		return switch (section) {
			case SILVER -> this.silver;
			case MIRRORS -> this.mirrors;
			case SPAWN_LANTERNS -> this.spawnLanterns;
			case WITHER_ZOMBIE -> this.witherZombie;
			case SCRYING -> this.scrying;
		};
	}

	public void setMode(final Section section, final Mode mode) {
		switch (section) {
			case SILVER -> {
				this.silver = mode;
				JustSilverConfig config = JustSilverConfig.get();
				config.enabled = mode != Mode.DISABLED;
				if (mode == Mode.ENABLED) {
					config.setDefaults();
				}
			}
			case MIRRORS -> {
				this.mirrors = mode;
				JustMirrorsConfig config = JustMirrorsConfig.get();
				config.enabled = mode != Mode.DISABLED;
				if (mode == Mode.ENABLED) {
					config.setDefaults();
				}
			}
			case SPAWN_LANTERNS -> {
				this.spawnLanterns = mode;
				JustSpawnLanternsConfig config = JustSpawnLanternsConfig.get();
				config.enabled = mode != Mode.DISABLED;
				if (mode == Mode.ENABLED) {
					config.setDefaults();
					JustSilverConfig.get().setSoulLanternDefaults();
				}
			}
			case WITHER_ZOMBIE -> {
				this.witherZombie = mode;
				JustWitherZombiesConfig config = JustWitherZombiesConfig.get();
				config.enabled = mode != Mode.DISABLED;
				if (mode == Mode.ENABLED) {
					boolean scryingBooks = config.scryingBooks;
					config.setDefaults();
					config.scryingBooks = scryingBooks;
				}
			}
			case SCRYING -> {
				this.scrying = mode;
				JustScryingConfig config = JustScryingConfig.get();
				config.enabled = mode != Mode.DISABLED;
				if (mode == Mode.ENABLED) {
					config.setDefaults();
					JustWitherZombiesConfig.get().scryingBooks = true;
				}
			}
		}
	}

	public void saveAll() {
		this.save();
		JustSilverConfig.get().save();
		JustMirrorsConfig.get().save();
		JustSpawnLanternsConfig.get().save();
		JustWitherZombiesConfig.get().save();
		JustScryingConfig.get().save();
	}

	private static Path path() {
		return FabricLoader.getInstance().getConfigDir().resolve("argent.json");
	}

	public static void load() {
		Path path = path();
		if (!Files.isRegularFile(path)) {
			instance.save();
			return;
		}

		try {
			CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(Files.readString(path))).ifSuccess(config -> instance = config);
		} catch (IOException e) {
			LOGGER.warn("Failed to read {}", path, e);
		}
	}

	public void save() {
		Path path = path();
		try {
			Files.createDirectories(path.getParent());
			Files.writeString(path, CODEC.encodeStart(JsonOps.INSTANCE, this).getOrThrow().toString());
		} catch (IOException e) {
			LOGGER.warn("Failed to save {}", path, e);
		}
	}

	public enum Section {
		SILVER("silver"),
		MIRRORS("mirrors"),
		SPAWN_LANTERNS("spawn_lanterns"),
		WITHER_ZOMBIE("wither_zombie"),
		SCRYING("scrying");

		private final String name;

		Section(final String name) {
			this.name = name;
		}

		public Component getDisplayName() {
			return Component.translatable("options.argent." + this.name);
		}
	}

	public enum Mode implements StringRepresentable {
		ENABLED("enabled"),
		DISABLED("disabled"),
		CUSTOM("custom");

		public static final Codec<Mode> CODEC = StringRepresentable.fromEnum(Mode::values);
		private final String name;

		Mode(final String name) {
			this.name = name;
		}

		public Component getDisplayName() {
			return Component.translatable("options.argent.mode." + this.name);
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}
	}

	public enum MirrorRecipe implements StringRepresentable {
		SILVER("silver"),
		DIAMOND("diamond");

		public static final Codec<MirrorRecipe> CODEC = StringRepresentable.fromEnum(MirrorRecipe::values);
		private final String name;

		MirrorRecipe(final String name) {
			this.name = name;
		}

		public Component getDisplayName() {
			return Component.translatable("options.argent.mirror_recipe." + this.name);
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}
	}
}
