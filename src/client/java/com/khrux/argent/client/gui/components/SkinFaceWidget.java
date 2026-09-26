package com.khrux.argent.client.gui.components;

import com.khrux.argent.client.skin.SkinFace;
import com.khrux.argent.client.skin.SkinPreset;
import com.khrux.argent.client.skin.SkinTool;
import com.mojang.blaze3d.platform.NativeImage;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
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
import net.minecraft.world.entity.player.PlayerModelType;
import org.jspecify.annotations.Nullable;

public class SkinFaceWidget extends AbstractWidget {
	private static final Component NO_FACE = Component.translatable("gui.argent.face.none");
	private static final int MAX_PIXEL_SCALE = 14;
	private static final int LABEL_X = 4;
	private static final int LABEL_Y = 3;
	private static final int LABEL_HEIGHT = 12;
	private static final int MARGIN = 4;
	private static final int CHECKER_LIGHT = 0xFF9A9A9A;
	private static final int CHECKER_DARK = 0xFF6E6E6E;
	private static final int HOVER_COLOR = 0x50FFFFFF;
	private final Supplier<SkinPreset> preset;
	private final Supplier<SkinFace> face;
	private final Supplier<SkinTool> tool;
	private final IntSupplier colour;
	private final IntConsumer onColourPicked;
	private final IntConsumer onColourUsed;
	private boolean painting;
	private int @Nullable [] lastCell;

	public SkinFaceWidget(
		final int x,
		final int y,
		final int width,
		final int height,
		final Supplier<SkinPreset> preset,
		final Supplier<SkinFace> face,
		final Supplier<SkinTool> tool,
		final IntSupplier colour,
		final IntConsumer onColourPicked,
		final IntConsumer onColourUsed
	) {
		super(x, y, width, height, NO_FACE);
		this.preset = preset;
		this.face = face;
		this.tool = tool;
		this.colour = colour;
		this.onColourPicked = onColourPicked;
		this.onColourUsed = onColourUsed;
	}

	private int pixelScale(final SkinFace face, final PlayerModelType model) {
		int byWidth = (this.getWidth() - MARGIN * 2) / face.width(model);
		int byHeight = (this.getHeight() - LABEL_HEIGHT - MARGIN * 2) / face.height();
		return Math.min(MAX_PIXEL_SCALE, Math.min(byWidth, byHeight));
	}

	private int gridLeft(final SkinFace face, final PlayerModelType model, final int scale) {
		return this.getX() + (this.getWidth() - face.width(model) * scale) / 2;
	}

	private int gridTop(final SkinFace face, final int scale) {
		return this.getY() + LABEL_HEIGHT + (this.getHeight() - LABEL_HEIGHT - face.height() * scale) / 2;
	}

	@Override
	protected void extractWidgetRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
		SkinFace face = this.face.get();
		if (face == null) {
			graphics.text(Minecraft.getInstance().font, NO_FACE, this.getX() + LABEL_X, this.getY() + LABEL_Y, CommonColors.LIGHT_GRAY);
			return;
		}

		SkinPreset preset = this.preset.get();
		PlayerModelType model = preset.model();
		graphics.text(Minecraft.getInstance().font, face.getName(), this.getX() + LABEL_X, this.getY() + LABEL_Y, CommonColors.LIGHT_GRAY);
		int scale = this.pixelScale(face, model);
		int left = this.gridLeft(face, model, scale);
		int top = this.gridTop(face, scale);
		int u = face.u(model);
		int v = face.v();
		for (int row = 0; row < face.height(); row++) {
			for (int column = 0; column < face.width(model); column++) {
				int x = left + column * scale;
				int y = top + row * scale;
				graphics.fill(x, y, x + scale, y + scale, (row + column) % 2 == 0 ? CHECKER_LIGHT : CHECKER_DARK);
				graphics.fill(x, y, x + scale, y + scale, preset.image().getPixel(u + column, v + row));
			}
		}

		int[] hovered = this.isHovered() ? this.cellAt(face, model, mouseX, mouseY) : null;
		if (hovered != null) {
			int x = left + hovered[0] * scale;
			int y = top + hovered[1] * scale;
			graphics.fill(x, y, x + scale, y + scale, HOVER_COLOR);
		}
	}

	private int @Nullable [] cellAt(final SkinFace face, final PlayerModelType model, final double mouseX, final double mouseY) {
		int scale = this.pixelScale(face, model);
		int column = (int)Math.floor((mouseX - this.gridLeft(face, model, scale)) / scale);
		int row = (int)Math.floor((mouseY - this.gridTop(face, scale)) / scale);
		if (column < 0 || column >= face.width(model) || row < 0 || row >= face.height()) {
			return null;
		}

		return new int[]{column, row};
	}

	@Override
	public void onClick(final MouseButtonEvent event, final boolean doubleClick) {
		SkinFace face = this.face.get();
		if (face == null) {
			return;
		}

		SkinPreset preset = this.preset.get();
		int[] cell = this.cellAt(face, preset.model(), event.x(), event.y());
		if (cell == null) {
			return;
		}

		SkinTool tool = this.tool.get();
		if (tool == SkinTool.PICKER) {
			this.pick(preset, face, cell);
			return;
		}

		preset.beginStroke();
		this.painting = true;
		if (tool == SkinTool.FILL) {
			this.fill(preset, face, cell);
			return;
		}

		this.paintCell(preset, face, cell[0], cell[1]);
		this.lastCell = cell;
	}

	@Override
	protected void onDrag(final MouseButtonEvent event, final double dx, final double dy) {
		SkinFace face = this.face.get();
		if (!this.painting || face == null || this.tool.get() == SkinTool.FILL) {
			return;
		}

		SkinPreset preset = this.preset.get();
		int[] cell = this.cellAt(face, preset.model(), event.x(), event.y());
		if (cell == null) {
			this.lastCell = null;
			return;
		}

		int[] from = this.lastCell != null ? this.lastCell : cell;
		int steps = Math.max(Math.abs(cell[0] - from[0]), Math.abs(cell[1] - from[1]));
		for (int step = 0; step <= steps; step++) {
			float progress = steps == 0 ? 0.0F : (float)step / steps;
			this.paintCell(preset, face, Math.round(Mth.lerp(progress, from[0], cell[0])), Math.round(Mth.lerp(progress, from[1], cell[1])));
		}

		this.lastCell = cell;
	}

	@Override
	public void onRelease(final MouseButtonEvent event) {
		if (!this.painting) {
			return;
		}

		this.painting = false;
		this.lastCell = null;
		if (this.preset.get().endStroke() && this.tool.get() != SkinTool.ERASER) {
			this.onColourUsed.accept(this.colour.getAsInt());
		}
	}

	private int paintColour() {
		return this.tool.get() == SkinTool.ERASER ? 0 : this.colour.getAsInt();
	}

	private void paintCell(final SkinPreset preset, final SkinFace face, final int column, final int row) {
		int x = face.u(preset.model()) + column;
		int y = face.v() + row;
		int colour = this.paintColour();
		if (preset.image().getPixel(x, y) != colour) {
			preset.image().setPixel(x, y, colour);
			preset.changed();
		}
	}

	private void fill(final SkinPreset preset, final SkinFace face, final int[] start) {
		NativeImage image = preset.image();
		int u = face.u(preset.model());
		int v = face.v();
		int width = face.width(preset.model());
		int height = face.height();
		int target = image.getPixel(u + start[0], v + start[1]);
		int colour = this.paintColour();
		if (target == colour) {
			return;
		}

		Deque<int[]> cells = new ArrayDeque<>();
		cells.push(start);
		while (!cells.isEmpty()) {
			int[] cell = cells.pop();
			int column = cell[0];
			int row = cell[1];
			if (column < 0 || column >= width || row < 0 || row >= height || image.getPixel(u + column, v + row) != target) {
				continue;
			}

			image.setPixel(u + column, v + row, colour);
			cells.push(new int[]{column + 1, row});
			cells.push(new int[]{column - 1, row});
			cells.push(new int[]{column, row + 1});
			cells.push(new int[]{column, row - 1});
		}

		preset.changed();
	}

	private void pick(final SkinPreset preset, final SkinFace face, final int[] cell) {
		int pixel = preset.image().getPixel(face.u(preset.model()) + cell[0], face.v() + cell[1]);
		if (ARGB.alpha(pixel) != 0) {
			this.onColourPicked.accept(ARGB.opaque(pixel));
		}
	}

	@Override
	public void playDownSound(final SoundManager soundManager) {
	}

	@Override
	protected void updateWidgetNarration(final NarrationElementOutput output) {
	}
}
