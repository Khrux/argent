package com.khrux.argent.client.gui.components;

import com.khrux.argent.client.skin.SkinPart;
import com.khrux.argent.client.skin.SkinPreset;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.PlayerModelType;
import org.jspecify.annotations.Nullable;

public class SkinLayerDoll extends AbstractWidget {
	public static final int WIDTH = 16;
	public static final int HEIGHT = 32;
	private static final int SKIN_SIZE = 64;
	private static final float UNDERLAY_ALPHA = 0.35F;
	private static final int HIDDEN_COLOR = 0xC0202020;
	private static final int HOVER_COLOR = 0x60FFFFFF;
	private final boolean overlay;
	private final Supplier<SkinPreset> preset;
	private final Set<SkinPart> hiddenParts;

	public SkinLayerDoll(final int x, final int y, final Component message, final boolean overlay, final Supplier<SkinPreset> preset, final Set<SkinPart> hiddenParts) {
		super(x, y, WIDTH, HEIGHT, message);
		this.overlay = overlay;
		this.preset = preset;
		this.hiddenParts = hiddenParts;
	}

	@Override
	protected void extractWidgetRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
		SkinPreset preset = this.preset.get();
		PlayerModelType model = preset.model();
		SkinPart hovered = this.isHovered() ? this.partAt(mouseX, mouseY) : null;
		for (SkinPart part : SkinPart.values()) {
			if (part.isOverlay() != this.overlay) {
				continue;
			}

			int x = this.getX() + part.dollX(model);
			int y = this.getY() + part.dollY();
			int width = part.width(model);
			if (this.overlay) {
				this.blitFront(graphics, preset, part.base(), x, y, width, ARGB.white(UNDERLAY_ALPHA));
			}

			this.blitFront(graphics, preset, part, x, y, width, -1);
			if (this.hiddenParts.contains(part)) {
				graphics.fill(x, y, x + width, y + part.height(), HIDDEN_COLOR);
			}

			if (part == hovered) {
				graphics.fill(x, y, x + width, y + part.height(), HOVER_COLOR);
			}
		}
	}

	private void blitFront(final GuiGraphicsExtractor graphics, final SkinPreset preset, final SkinPart part, final int x, final int y, final int width, final int color) {
		graphics.blit(RenderPipelines.GUI_TEXTURED, preset.textureLocation(), x, y, part.frontU(), part.frontV(), width, part.height(), SKIN_SIZE, SKIN_SIZE, color);
	}

	private @Nullable SkinPart partAt(final double mouseX, final double mouseY) {
		PlayerModelType model = this.preset.get().model();
		for (SkinPart part : SkinPart.values()) {
			if (part.isOverlay() != this.overlay) {
				continue;
			}

			int x = this.getX() + part.dollX(model);
			int y = this.getY() + part.dollY();
			if (mouseX >= x && mouseX < x + part.width(model) && mouseY >= y && mouseY < y + part.height()) {
				return part;
			}
		}

		return null;
	}

	@Override
	public void onClick(final MouseButtonEvent event, final boolean doubleClick) {
		SkinPart part = this.partAt(event.x(), event.y());
		if (part != null && !this.hiddenParts.remove(part)) {
			this.hiddenParts.add(part);
		}
	}

	@Override
	protected void updateWidgetNarration(final NarrationElementOutput output) {
		this.defaultButtonNarrationText(output);
	}
}
