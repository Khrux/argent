package com.khrux.argent.client.gui.components;

import com.khrux.argent.client.skin.SkinPresets;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;

public class SkinPresetTab extends AbstractButton {
	public static final int WIDTH = 32;
	public static final int HEIGHT = 24;
	private static final int FACE_SIZE = 16;
	private final SkinPresets presets;
	private final int index;

	public SkinPresetTab(final int x, final int y, final SkinPresets presets, final int index) {
		super(x, y, WIDTH, HEIGHT, Component.translatable("gui.argent.preset", index + 1));
		this.presets = presets;
		this.index = index;
		this.setOverrideRenderHighlightedSprite(() -> this.isSelected() || this.isHoveredOrFocused());
	}

	private boolean isSelected() {
		return this.presets.selectedIndex() == this.index;
	}

	@Override
	public void onPress(final InputWithModifiers input) {
		this.presets.select(this.index);
	}

	@Override
	protected void extractContents(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
		this.extractDefaultSprite(graphics);
		int x = this.getX() + (WIDTH - FACE_SIZE) / 2;
		int y = this.getY() + (HEIGHT - FACE_SIZE) / 2;
		PlayerFaceExtractor.extractRenderState(graphics, this.presets.get(this.index).textureLocation(), x, y, FACE_SIZE, true, false, -1);
	}

	@Override
	protected void updateWidgetNarration(final NarrationElementOutput output) {
		this.defaultButtonNarrationText(output);
	}
}
