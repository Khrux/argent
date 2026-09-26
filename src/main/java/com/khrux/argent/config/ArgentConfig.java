package com.khrux.argent.config;

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

public class ArgentConfig {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final Codec<ArgentConfig> CODEC = RecordCodecBuilder.create(
		i -> i.group(
				Codec.BOOL.optionalFieldOf("silver", true).forGetter(config -> config.silver),
				MirrorRecipe.CODEC.optionalFieldOf("mirror_recipe", MirrorRecipe.SILVER).forGetter(config -> config.mirrorRecipe),
				Codec.BOOL.optionalFieldOf("mirror_screen", true).forGetter(config -> config.mirrorScreen),
				SoulLantern.CODEC.optionalFieldOf("soul_lantern_mode", SoulLantern.HAND).forGetter(config -> config.soulLantern),
				Reflections.CODEC.optionalFieldOf("reflection_mode", Reflections.ALL).forGetter(config -> config.reflections),
				Codec.BOOL.optionalFieldOf("wither_zombies", true).forGetter(config -> config.witherZombies),
				Codec.BOOL.optionalFieldOf("scrying", true).forGetter(config -> config.scrying)
			)
			.apply(i, ArgentConfig::new)
	);
	private static ArgentConfig instance = new ArgentConfig(true, MirrorRecipe.SILVER, true, SoulLantern.HAND, Reflections.ALL, true, true);
	public boolean silver;
	public MirrorRecipe mirrorRecipe;
	public boolean mirrorScreen;
	public SoulLantern soulLantern;
	public Reflections reflections;
	public boolean witherZombies;
	public boolean scrying;

	private ArgentConfig(
		final boolean silver,
		final MirrorRecipe mirrorRecipe,
		final boolean mirrorScreen,
		final SoulLantern soulLantern,
		final Reflections reflections,
		final boolean witherZombies,
		final boolean scrying
	) {
		this.silver = silver;
		this.mirrorRecipe = mirrorRecipe;
		this.mirrorScreen = mirrorScreen;
		this.soulLantern = soulLantern;
		this.reflections = reflections;
		this.witherZombies = witherZombies;
		this.scrying = scrying;
	}

	public static ArgentConfig get() {
		return instance;
	}

	public boolean mirrors() {
		return this.mirrorRecipe != MirrorRecipe.OFF;
	}

	private static Path path() {
		return FabricLoader.getInstance().getConfigDir().resolve("argent.json");
	}

	private static Path legacyPath() {
		return FabricLoader.getInstance().getConfigDir().resolve("mirror.json");
	}

	public static void load() {
		Path path = path();
		Path legacyPath = legacyPath();
		if (!Files.exists(path) && Files.isRegularFile(legacyPath)) {
			try {
				Files.move(legacyPath, path);
			} catch (IOException e) {
				LOGGER.warn("Failed to move {} to {}", legacyPath, path, e);
			}
		}

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

	private static Component displayName(final String option, final String value) {
		return Component.translatable("options.argent." + option + "." + value);
	}

	public enum MirrorRecipe implements StringRepresentable {
		OFF("off"),
		SILVER("silver"),
		DIAMOND("diamond");

		public static final Codec<MirrorRecipe> CODEC = StringRepresentable.fromEnum(MirrorRecipe::values);
		private final String name;

		MirrorRecipe(final String name) {
			this.name = name;
		}

		public Component getDisplayName() {
			return displayName("mirror_recipe", this.name);
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}
	}

	public enum SoulLantern implements StringRepresentable {
		OFF("off"),
		HAND("hand"),
		INVENTORY("inventory");

		public static final Codec<SoulLantern> CODEC = StringRepresentable.fromEnum(SoulLantern::values);
		private final String name;

		SoulLantern(final String name) {
			this.name = name;
		}

		public Component getDisplayName() {
			return displayName("soul_lantern", this.name);
		}

		@Override
		public String getSerializedName() {
			return this.name;
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
			return displayName("reflections", this.name);
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}
	}
}
