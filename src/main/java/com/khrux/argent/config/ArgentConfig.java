package com.khrux.argent.config;

import com.google.gson.JsonParser;
import com.khrux.justmirrors.config.JustMirrorsConfig;
import com.khrux.justsilver.config.JustSilverConfig;
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
import org.jspecify.annotations.Nullable;
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
				MirrorRecipe.CODEC.lenientOptionalFieldOf("mirror_recipe", MirrorRecipe.SILVER).forGetter(config -> config.mirrorRecipe)
			)
			.apply(i, ArgentConfig::new)
	);
	private static ArgentConfig instance = new ArgentConfig(Mode.ENABLED, Mode.ENABLED, Mode.ENABLED, Mode.ENABLED, Mode.ENABLED, MirrorRecipe.SILVER);
	public Mode silver;
	public Mode mirrors;
	public Mode spawnLanterns;
	public Mode witherZombie;
	public Mode scrying;
	public MirrorRecipe mirrorRecipe;

	private ArgentConfig(
		final Mode silver, final Mode mirrors, final Mode spawnLanterns, final Mode witherZombie, final Mode scrying, final MirrorRecipe mirrorRecipe
	) {
		this.silver = silver;
		this.mirrors = mirrors;
		this.spawnLanterns = spawnLanterns;
		this.witherZombie = witherZombie;
		this.scrying = scrying;
		this.mirrorRecipe = mirrorRecipe;
	}

	public static ArgentConfig get() {
		return instance;
	}

	public @Nullable MirrorRecipe activeMirrorRecipe() {
		if (!JustMirrorsConfig.get().isEnabled()) {
			return null;
		}

		if (this.mirrors == Mode.CUSTOM) {
			return this.mirrorRecipe;
		}

		return JustSilverConfig.get().generatesOre() ? MirrorRecipe.SILVER : MirrorRecipe.DIAMOND;
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
