package com.khrux.argent.client.skin;

import com.google.gson.JsonParser;
import com.khrux.argent.Argent;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.DyeColor;
import org.slf4j.Logger;

public class SkinPresets {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final int COUNT = 9;
	private static final int COLOUR_COUNT = 16;
	private static final int SKIN_SIZE = 64;
	private static final List<PlayerSkin> DEFAULT_SKINS = List.of(
		defaultSkin("slim/alex", PlayerModelType.SLIM),
		defaultSkin("wide/ari", PlayerModelType.WIDE),
		defaultSkin("wide/efe", PlayerModelType.WIDE),
		defaultSkin("wide/kai", PlayerModelType.WIDE),
		defaultSkin("wide/makena", PlayerModelType.WIDE),
		defaultSkin("wide/noor", PlayerModelType.WIDE),
		defaultSkin("wide/sunny", PlayerModelType.WIDE),
		defaultSkin("wide/zuri", PlayerModelType.WIDE)
	);
	private static final List<Integer> DEFAULT_COLOURS = Arrays.stream(DyeColor.values()).map(color -> ARGB.opaque(color.getTextureDiffuseColor())).toList();
	private final Path directory;
	private final List<SkinPreset> presets = new ArrayList<>(COUNT);
	private final List<Integer> colours = new ArrayList<>(COLOUR_COUNT);
	private int selected;
	private boolean settingsDirty;

	private SkinPresets(final Path directory) {
		this.directory = directory;
	}

	private static PlayerSkin defaultSkin(final String name, final PlayerModelType model) {
		return PlayerSkin.insecure(new ClientAsset.ResourceTexture(Identifier.withDefaultNamespace("entity/player/" + name)), null, null, model);
	}

	public static SkinPresets load(final Minecraft minecraft) {
		Path directory = minecraft.gameDirectory.toPath().resolve(Argent.MOD_ID);
		Path legacyDirectory = minecraft.gameDirectory.toPath().resolve("mirror");
		if (!Files.exists(directory) && Files.isDirectory(legacyDirectory)) {
			try {
				Files.move(legacyDirectory, directory);
			} catch (IOException e) {
				LOGGER.warn("Failed to move {} to {}", legacyDirectory, directory, e);
			}
		}

		SkinPresets presets = new SkinPresets(directory.resolve("presets"));
		Settings settings = presets.readSettings();
		presets.selected = settings.selected();
		presets.colours.addAll(settings.colours());
		for (int i = 0; i < COUNT; i++) {
			PlayerSkin fallback = fallback(minecraft, i);
			NativeImage image = presets.readImage(i);
			boolean seeded = image == null;
			if (seeded) {
				image = readResourceSkin(minecraft, fallback.body().texturePath());
			}

			PlayerModelType model = i < settings.models().size() ? settings.models().get(i) : fallback.model();
			SkinPreset preset = new SkinPreset(Argent.id("preset/" + i), image, model);
			presets.presets.add(preset);
			if (seeded) {
				preset.changed();
				presets.settingsDirty = true;
			}
		}

		if (presets.get(0).isDirty()) {
			SkinImporter.importProfile(minecraft, presets.get(0), minecraft.getGameProfile());
		}

		return presets;
	}

	private static PlayerSkin fallback(final Minecraft minecraft, final int index) {
		return index == 0 ? DefaultPlayerSkin.get(minecraft.getGameProfile()) : DEFAULT_SKINS.get(index - 1);
	}

	public void reset(final Minecraft minecraft, final int index) {
		PlayerSkin fallback = fallback(minecraft, index);
		SkinPreset preset = this.get(index);
		preset.replace(readResourceSkin(minecraft, fallback.body().texturePath()), fallback.model());
		if (index == 0) {
			SkinImporter.importProfile(minecraft, preset, minecraft.getGameProfile());
		}
	}

	private static NativeImage readResourceSkin(final Minecraft minecraft, final Identifier texturePath) {
		try (InputStream stream = minecraft.getResourceManager().open(texturePath)) {
			return NativeImage.read(stream);
		} catch (IOException e) {
			throw new IllegalStateException("Missing default skin " + texturePath, e);
		}
	}

	private NativeImage readImage(final int index) {
		Path path = this.imagePath(index);
		if (!Files.isRegularFile(path)) {
			return null;
		}

		try (InputStream stream = Files.newInputStream(path)) {
			NativeImage image = NativeImage.read(stream);
			if (image.getWidth() == SKIN_SIZE && image.getHeight() == SKIN_SIZE) {
				return image;
			}

			image.close();
		} catch (IOException e) {
			LOGGER.warn("Failed to read skin preset {}", path, e);
		}

		return null;
	}

	private Settings readSettings() {
		Path path = this.directory.resolve("presets.json");
		if (!Files.isRegularFile(path)) {
			return Settings.DEFAULT;
		}

		try {
			return Settings.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(Files.readString(path))).result().orElse(Settings.DEFAULT);
		} catch (IOException e) {
			LOGGER.warn("Failed to read {}", path, e);
			return Settings.DEFAULT;
		}
	}

	public void save() {
		try {
			Files.createDirectories(this.directory);
			for (int i = 0; i < COUNT; i++) {
				SkinPreset preset = this.get(i);
				if (preset.isDirty()) {
					preset.image().writeToFile(this.imagePath(i));
					preset.setClean();
					this.settingsDirty = true;
				}
			}

			if (this.settingsDirty) {
				Settings settings = new Settings(this.selected, this.presets.stream().map(SkinPreset::model).toList(), List.copyOf(this.colours));
				Files.writeString(this.directory.resolve("presets.json"), Settings.CODEC.encodeStart(JsonOps.INSTANCE, settings).getOrThrow().toString());
				this.settingsDirty = false;
			}
		} catch (IOException e) {
			LOGGER.warn("Failed to save skin presets to {}", this.directory, e);
		}
	}

	public Path imagePath(final int index) {
		return this.directory.resolve(index + 1 + ".png");
	}

	public Path directory() {
		return this.directory;
	}

	public SkinPreset get(final int index) {
		return this.presets.get(index);
	}

	public SkinPreset selected() {
		return this.get(this.selected);
	}

	public int selectedIndex() {
		return this.selected;
	}

	public void select(final int index) {
		this.selected = index;
		this.settingsDirty = true;
	}

	public List<Integer> colours() {
		return this.colours;
	}

	public void useColour(final int colour) {
		this.colours.remove(Integer.valueOf(colour));
		this.colours.addFirst(colour);
		if (this.colours.size() > COLOUR_COUNT) {
			this.colours.removeLast();
		}

		this.settingsDirty = true;
	}

	public boolean isDirty() {
		return this.settingsDirty || this.presets.stream().anyMatch(SkinPreset::isDirty);
	}

	private record Settings(int selected, List<PlayerModelType> models, List<Integer> colours) {
		public static final Settings DEFAULT = new Settings(0, List.of(), DEFAULT_COLOURS);
		public static final Codec<Settings> CODEC = RecordCodecBuilder.create(
			i -> i.group(
					Codec.intRange(0, COUNT - 1).fieldOf("selected").forGetter(Settings::selected),
					PlayerModelType.CODEC.listOf().fieldOf("models").forGetter(Settings::models),
					Codec.INT.listOf(0, COLOUR_COUNT).optionalFieldOf("colours", DEFAULT_COLOURS).forGetter(Settings::colours)
				)
				.apply(i, Settings::new)
		);
	}
}
