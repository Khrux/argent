package com.khrux.argent.client.skin;

import com.mojang.blaze3d.platform.NativeImage;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import org.jspecify.annotations.Nullable;

public class SkinPreset {
	private static final int HISTORY_LIMIT = 64;
	private final Identifier textureLocation;
	private final DynamicTexture texture;
	private final Deque<int[]> undoHistory = new ArrayDeque<>();
	private final Deque<int[]> redoHistory = new ArrayDeque<>();
	private int @Nullable [] strokeStart;
	private PlayerModelType model;
	private boolean dirty;

	public SkinPreset(final Identifier textureLocation, final NativeImage image, final PlayerModelType model) {
		this.textureLocation = textureLocation;
		this.texture = new DynamicTexture(textureLocation::toString, image);
		this.model = model;
		Minecraft.getInstance().getTextureManager().register(textureLocation, this.texture);
	}

	public NativeImage image() {
		return this.texture.getPixels();
	}

	public Identifier textureLocation() {
		return this.textureLocation;
	}

	public PlayerModelType model() {
		return this.model;
	}

	public PlayerSkin skin() {
		return PlayerSkin.insecure(this.body(), null, null, this.model);
	}

	public PlayerSkin skin(final PlayerSkin worn) {
		return PlayerSkin.insecure(this.body(), worn.cape(), worn.elytra(), this.model);
	}

	private ClientAsset.Texture body() {
		return new ClientAsset.ResourceTexture(this.textureLocation, this.textureLocation);
	}

	public void replace(final NativeImage image, final PlayerModelType model) {
		this.beginStroke();
		this.texture.getPixels().copyFrom(image);
		image.close();
		this.model = model;
		this.changed();
		this.endStroke();
	}

	public void close() {
		Minecraft.getInstance().getTextureManager().release(this.textureLocation);
	}

	public void setModel(final PlayerModelType model) {
		this.model = model;
		this.dirty = true;
	}

	public void beginStroke() {
		this.strokeStart = this.image().getPixelsABGR();
	}

	public boolean endStroke() {
		int[] before = this.strokeStart;
		this.strokeStart = null;
		if (before == null || Arrays.equals(before, this.image().getPixelsABGR())) {
			return false;
		}

		remember(this.undoHistory, before);
		this.redoHistory.clear();
		return true;
	}

	public boolean canUndo() {
		return !this.undoHistory.isEmpty();
	}

	public boolean canRedo() {
		return !this.redoHistory.isEmpty();
	}

	public void undo() {
		this.restore(this.undoHistory, this.redoHistory);
	}

	public void redo() {
		this.restore(this.redoHistory, this.undoHistory);
	}

	private void restore(final Deque<int[]> from, final Deque<int[]> to) {
		if (from.isEmpty()) {
			return;
		}

		NativeImage image = this.image();
		remember(to, image.getPixelsABGR());
		int[] pixels = from.pop();
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				image.setPixelABGR(x, y, pixels[x + y * image.getWidth()]);
			}
		}

		this.changed();
	}

	private static void remember(final Deque<int[]> history, final int[] pixels) {
		history.push(pixels);
		if (history.size() > HISTORY_LIMIT) {
			history.removeLast();
		}
	}

	public void changed() {
		this.texture.upload();
		this.dirty = true;
	}

	public boolean isDirty() {
		return this.dirty;
	}

	public void setClean() {
		this.dirty = false;
	}
}
