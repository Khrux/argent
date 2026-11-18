package com.khrux.argent.client.gui.components;

import com.khrux.argent.config.ArgentConfig;
import com.khrux.justmirrors.world.item.JustMirrorsItems;
import com.khrux.justscrying.world.item.JustScryingItems;
import com.khrux.justsilver.world.item.JustSilverItems;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public record RecipeGrid(List<ItemStack> ingredients, ItemStack result, boolean needsSilver) implements ClientTooltipComponent {
	private static final int SLOT_SIZE = 18;
	private static final int ARROW_WIDTH = 24;
	private static final int SLOT_COLOR = 0x40FFFFFF;
	private static final int ARROW_COLOR = 0xFFAAAAAA;

	private static RecipeGrid of(final Item result, final boolean needsSilver, final Item... ingredients) {
		return new RecipeGrid(List.of(ingredients).stream().map(item -> item == Items.AIR ? ItemStack.EMPTY : new ItemStack(item)).toList(), new ItemStack(result), needsSilver);
	}

	public static RecipeGrid mirror(final ArgentConfig.MirrorRecipe recipe) {
		Item s = Items.STICK;
		Item core = recipe == ArgentConfig.MirrorRecipe.SILVER ? JustSilverItems.SILVER_INGOT : Items.DIAMOND;
		return of(JustMirrorsItems.MIRROR, recipe == ArgentConfig.MirrorRecipe.SILVER, s, s, s, s, core, s, s, s, s);
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

	public static RecipeGrid parrotArmor() {
		Item f = Items.FEATHER;
		Item l = Items.LEATHER;
		Item air = Items.AIR;
		return of(JustScryingItems.PARROT_ARMOR, false, air, f, air, air, l, air, l, l, air);
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
