package com.khrux.argent.client.gui.components;

import java.util.List;
import java.util.function.IntConsumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;

public class ColourPaletteWidget extends AbstractWidget {
	private static final int COLUMNS = 8;
	private static final int ROWS = 2;
	private static final int SWATCH_WIDTH = 16;
	private static final int SWATCH_HEIGHT = 7;
	private static final int STEP_X = 17;
	private static final int STEP_Y = 8;
	private final List<Integer> colours;
	private final IntConsumer onSelect;

	public ColourPaletteWidget(final int x, final int y, final List<Integer> colours, final IntConsumer onSelect) {
		super(x, y, COLUMNS * STEP_X, ROWS * STEP_Y, Component.translatable("gui.argent.palette"));
		this.colours = colours;
		this.onSelect = onSelect;
	}

	@Override
	protected void extractWidgetRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
		int hovered = this.isHovered() ? this.swatchAt(mouseX, mouseY) : -1;
		for (int i = 0; i < this.colours.size(); i++) {
			int x = this.getX() + i % COLUMNS * STEP_X;
			int y = this.getY() + i / COLUMNS * STEP_Y;
			graphics.fill(x, y, x + SWATCH_WIDTH, y + SWATCH_HEIGHT, this.colours.get(i));
			if (i == hovered) {
				graphics.outline(x, y, SWATCH_WIDTH, SWATCH_HEIGHT, CommonColors.WHITE);
			}
		}
	}

	private int swatchAt(final double mouseX, final double mouseY) {
		int x = (int)Math.floor(mouseX) - this.getX();
		int y = (int)Math.floor(mouseY) - this.getY();
		if (x < 0 || y < 0 || x % STEP_X >= SWATCH_WIDTH || y % STEP_Y >= SWATCH_HEIGHT) {
			return -1;
		}

		int index = y / STEP_Y * COLUMNS + x / STEP_X;
		return index < this.colours.size() ? index : -1;
	}

	@Override
	public void onClick(final MouseButtonEvent event, final boolean doubleClick) {
		int index = this.swatchAt(event.x(), event.y());
		if (index >= 0) {
			this.onSelect.accept(this.colours.get(index));
		}
	}

	@Override
	public void playDownSound(final SoundManager soundManager) {
	}

	@Override
	protected void updateWidgetNarration(final NarrationElementOutput output) {
	}
}
