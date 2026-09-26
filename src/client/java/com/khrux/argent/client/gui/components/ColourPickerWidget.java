package com.khrux.argent.client.gui.components;

import java.util.Locale;
import java.util.function.IntConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;

public class ColourPickerWidget extends AbstractWidget {
	private static final Component TITLE = Component.translatable("gui.argent.colour");
	private static final int LABEL_X = 4;
	private static final int LABEL_Y = 3;
	private static final int SQUARE_X = 4;
	private static final int SQUARE_Y = 16;
	private static final int SQUARE_SIZE = 108;
	private static final int HUE_X = 116;
	private static final int HUE_WIDTH = 16;
	private static final int HEX_Y = 127;
	private static final int MARKER_SIZE = 5;
	private final IntConsumer onChange;
	private float hue;
	private float saturation;
	private float value;
	private boolean dragging;
	private boolean draggingHue;

	public ColourPickerWidget(final int x, final int y, final int size, final IntConsumer onChange) {
		super(x, y, size, size, TITLE);
		this.onChange = onChange;
	}

	public void setColour(final int colour) {
		float red = ARGB.red(colour) / 255.0F;
		float green = ARGB.green(colour) / 255.0F;
		float blue = ARGB.blue(colour) / 255.0F;
		float max = Math.max(red, Math.max(green, blue));
		float delta = max - Math.min(red, Math.min(green, blue));
		this.value = max;
		if (max > 0.0F) {
			this.saturation = delta / max;
		}

		if (delta <= 0.0F) {
			return;
		}

		float sector;
		if (max == red) {
			sector = (green - blue) / delta;
		} else if (max == green) {
			sector = 2.0F + (blue - red) / delta;
		} else {
			sector = 4.0F + (red - green) / delta;
		}

		this.hue = Math.min(Mth.positiveModulo(sector / 6.0F, 1.0F), this.hueAt(SQUARE_SIZE - 1));
	}

	private int colour() {
		return Mth.hsvToArgb(this.hue, this.saturation, this.value, 255);
	}

	private float hueAt(final int row) {
		return (float)row / SQUARE_SIZE;
	}

	@Override
	protected void extractWidgetRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
		int left = this.getX();
		int top = this.getY();
		graphics.text(Minecraft.getInstance().font, TITLE, left + LABEL_X, top + LABEL_Y, CommonColors.LIGHT_GRAY);
		int squareLeft = left + SQUARE_X;
		int squareTop = top + SQUARE_Y;
		for (int column = 0; column < SQUARE_SIZE; column++) {
			int brightest = Mth.hsvToArgb(this.hue, (float)column / (SQUARE_SIZE - 1), 1.0F, 255);
			graphics.fillGradient(squareLeft + column, squareTop, squareLeft + column + 1, squareTop + SQUARE_SIZE, brightest, CommonColors.BLACK);
		}

		int hueLeft = left + HUE_X;
		for (int row = 0; row < SQUARE_SIZE; row++) {
			graphics.fill(hueLeft, squareTop + row, hueLeft + HUE_WIDTH, squareTop + row + 1, Mth.hsvToArgb(this.hueAt(row), 1.0F, 1.0F, 255));
		}

		int markerX = squareLeft + Math.round(this.saturation * (SQUARE_SIZE - 1));
		int markerY = squareTop + Math.round((1.0F - this.value) * (SQUARE_SIZE - 1));
		int markerColour = this.value > 0.5F && this.saturation < 0.5F ? CommonColors.BLACK : CommonColors.WHITE;
		graphics.outline(markerX - MARKER_SIZE / 2, markerY - MARKER_SIZE / 2, MARKER_SIZE, MARKER_SIZE, markerColour);
		int hueY = squareTop + Math.round(this.hue * SQUARE_SIZE);
		graphics.outline(hueLeft - 1, hueY - 1, HUE_WIDTH + 2, 3, CommonColors.WHITE);
		String hex = String.format(Locale.ROOT, "#%06X", this.colour() & 0xFFFFFF);
		graphics.text(Minecraft.getInstance().font, hex, squareLeft, top + HEX_Y, CommonColors.LIGHT_GRAY);
	}

	@Override
	public void onClick(final MouseButtonEvent event, final boolean doubleClick) {
		double x = event.x() - this.getX();
		double y = event.y() - this.getY();
		boolean inRows = y >= SQUARE_Y && y < SQUARE_Y + SQUARE_SIZE;
		boolean inSquare = inRows && x >= SQUARE_X && x < SQUARE_X + SQUARE_SIZE;
		this.draggingHue = inRows && x >= HUE_X && x < HUE_X + HUE_WIDTH;
		this.dragging = inSquare || this.draggingHue;
		if (this.dragging) {
			this.update(event.x(), event.y());
		}
	}

	@Override
	protected void onDrag(final MouseButtonEvent event, final double dx, final double dy) {
		if (this.dragging) {
			this.update(event.x(), event.y());
		}
	}

	private void update(final double mouseX, final double mouseY) {
		int column = Mth.clamp((int)Math.floor(mouseX) - this.getX() - SQUARE_X, 0, SQUARE_SIZE - 1);
		int row = Mth.clamp((int)Math.floor(mouseY) - this.getY() - SQUARE_Y, 0, SQUARE_SIZE - 1);
		if (this.draggingHue) {
			this.hue = this.hueAt(row);
		} else {
			this.saturation = (float)column / (SQUARE_SIZE - 1);
			this.value = 1.0F - (float)row / (SQUARE_SIZE - 1);
		}

		this.onChange.accept(this.colour());
	}

	@Override
	public void playDownSound(final SoundManager soundManager) {
	}

	@Override
	protected void updateWidgetNarration(final NarrationElementOutput output) {
	}
}
