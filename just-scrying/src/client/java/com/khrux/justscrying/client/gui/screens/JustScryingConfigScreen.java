package com.khrux.justscrying.client.gui.screens;

import com.khrux.justscrying.client.gui.components.PetArmorRecipeGrid;
import com.khrux.justscrying.config.JustScryingConfig;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class JustScryingConfigScreen extends OptionsSubScreen {
	private static final Component TITLE = Component.translatable("options.justscrying.title");
	private static final Component RECIPE_TOOLTIP = Component.translatable("options.justscrying.recipe.tooltip");
	private static final int CHECKBOX_WIDTH = 150;
	private final Map<AbstractWidget, Supplier<@Nullable PetArmorRecipeGrid>> recipeGrids = new LinkedHashMap<>();

	public JustScryingConfigScreen(final Screen lastScreen) {
		super(lastScreen, Minecraft.getInstance().options, TITLE);
	}

	@Override
	protected void addOptions() {
		this.recipeGrids.clear();
		JustScryingConfig config = JustScryingConfig.get();
		this.list.addBig(JustScryingOptions.effect());
		this.list.addHeader(JustScryingOptions.BOOKS_LOOTED_FROM);
		this.list
			.addSmall(
				List.of(
					checkbox(this, JustScryingOptions.ELDER_GUARDIAN, JustScryingOptions.ELDER_GUARDIAN_TOOLTIP, config.elderGuardianBook, value -> config.elderGuardianBook = value),
					checkbox(this, JustScryingOptions.FISHING_LOOT, JustScryingOptions.FISHING_LOOT_TOOLTIP, config.fishingBooks, value -> config.fishingBooks = value),
					checkbox(this, JustScryingOptions.STRONGHOLD_LIBRARY, JustScryingOptions.STRONGHOLD_LIBRARY_TOOLTIP, config.libraryBooks, value -> config.libraryBooks = value)
				)
			);
		this.list.addHeader(JustScryingOptions.ARMOR_RECIPES);
		this.list
			.addSmall(
				List.of(
					this.recipe(checkbox(this, JustScryingOptions.PARROT, null, config.parrotArmorRecipe, value -> config.parrotArmorRecipe = value), PetArmorRecipeGrid::parrotArmor),
					this.recipe(checkbox(this, JustScryingOptions.CAT, null, config.catArmorRecipe, value -> config.catArmorRecipe = value), PetArmorRecipeGrid::catArmor)
				)
			);
	}

	public static Checkbox checkbox(
		final Screen screen, final Component label, final @Nullable Component tooltip, final boolean selected, final BooleanConsumer onChange
	) {
		Checkbox.Builder builder = Checkbox.builder(label, screen.getFont())
			.selected(selected)
			.maxWidth(CHECKBOX_WIDTH)
			.onValueChange((checkbox, value) -> onChange.accept(value));
		if (tooltip != null) {
			builder.tooltip(Tooltip.create(tooltip));
		}

		return builder.build();
	}

	private AbstractWidget recipe(final AbstractWidget widget, final Supplier<@Nullable PetArmorRecipeGrid> grid) {
		this.recipeGrids.put(widget, grid);
		return widget;
	}

	@Override
	public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float partialTicks) {
		super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
		for (Map.Entry<AbstractWidget, Supplier<@Nullable PetArmorRecipeGrid>> entry : this.recipeGrids.entrySet()) {
			if (entry.getKey().isHovered()) {
				PetArmorRecipeGrid grid = entry.getValue().get();
				if (grid != null) {
					graphics.tooltip(this.font, grid.tooltip(this.font, RECIPE_TOOLTIP), mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null, false);
				}

				return;
			}
		}
	}

	@Override
	public void removed() {
		super.removed();
		JustScryingConfig.get().save();
	}
}
