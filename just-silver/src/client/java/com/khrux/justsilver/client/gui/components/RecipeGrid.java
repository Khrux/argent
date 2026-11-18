package com.khrux.justsilver.client.gui.components;

import com.khrux.justsilver.config.JustSilverConfig;
import com.khrux.justsilver.world.item.JustSilverItems;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public record RecipeGrid(List<ItemStack> ingredients, ItemStack result, boolean needsSilver) implements ClientTooltipComponent {
	private static final int SLOT_SIZE = 18;
	private static final int ARROW_WIDTH = 24;
	private static final int SLOT_COLOR = 0x40FFFFFF;
	private static final int ARROW_COLOR = 0xFFAAAAAA;
	private static final int TOOLTIP_WIDTH = 200;
	private static final Component SILVER_WARNING = Component.translatable("options.justsilver.silver_warning").withStyle(ChatFormatting.GOLD);

	public static RecipeGrid of(final Item result, final boolean needsSilver, final Item... ingredients) {
		return new RecipeGrid(
			Arrays.stream(ingredients).map(item -> item == Items.AIR ? ItemStack.EMPTY : new ItemStack(item)).toList(), new ItemStack(result), needsSilver
		);
	}

	public static RecipeGrid silverBell() {
		Item log = Items.OAK_LOG;
		Item air = Items.AIR;
		return of(JustSilverItems.SILVER_BELL, true, log, log, log, air, JustSilverItems.SILVER_BLOCK, air, air, air, air);
	}

	public static RecipeGrid soulLantern() {
		Item n = JustSilverItems.SILVER_NUGGET;
		return of(Items.SOUL_LANTERN, false, n, n, n, n, Items.SOUL_TORCH, n, n, n, n);
	}

	public List<ClientTooltipComponent> tooltip(final Font font, final Component text) {
		List<ClientTooltipComponent> lines = new ArrayList<>();
		font.split(text, TOOLTIP_WIDTH).forEach(line -> lines.add(ClientTooltipComponent.create(line)));
		if (this.needsSilver && !JustSilverConfig.get().generatesOre()) {
			font.split(SILVER_WARNING, TOOLTIP_WIDTH).forEach(line -> lines.add(ClientTooltipComponent.create(line)));
		}

		lines.add(this);
		return lines;
	}

	@Override
	public int getHeight(final Font font) {
		return SLOT_SIZE * 3 + 2;
	}

	@Override
	public int getWidth(final Font font) {
		return SLOT_SIZE * 4 + ARROW_WIDTH;
	}

	@Override
	public void extractImage(final Font font, final int x, final int y, final int w, final int h, final GuiGraphicsExtractor graphics) {
		for (int i = 0; i < this.ingredients.size(); i++) {
			this.slot(graphics, x + i % 3 * SLOT_SIZE, y + i / 3 * SLOT_SIZE, this.ingredients.get(i));
		}

		int arrowY = y + SLOT_SIZE + SLOT_SIZE / 2;
		int arrowX = x + SLOT_SIZE * 3 + 4;
		graphics.fill(arrowX, arrowY - 1, arrowX + ARROW_WIDTH - 10, arrowY + 1, ARROW_COLOR);
		for (int i = 0; i < 4; i++) {
			graphics.fill(arrowX + ARROW_WIDTH - 11 + i, arrowY - 4 + i, arrowX + ARROW_WIDTH - 10 + i, arrowY + 4 - i, ARROW_COLOR);
		}

		this.slot(graphics, x + SLOT_SIZE * 3 + ARROW_WIDTH, y + SLOT_SIZE, this.result);
	}

	private void slot(final GuiGraphicsExtractor graphics, final int x, final int y, final ItemStack stack) {
		graphics.fill(x, y, x + SLOT_SIZE - 2, y + SLOT_SIZE - 2, SLOT_COLOR);
		if (!stack.isEmpty()) {
			graphics.item(stack, x, y);
		}
	}
}
