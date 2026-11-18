package com.khrux.justscrying.client.gui.screens;

import com.khrux.justscrying.client.gui.components.RecipeGrid;
import com.khrux.justscrying.config.JustScryingConfig;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

public class JustScryingConfigScreen extends OptionsSubScreen {
	private static final Component TITLE = Component.translatable("options.justscrying.title");
	private static final Component RECIPE_TOOLTIP = Component.translatable("options.justscrying.recipe.tooltip");
	private final Map<OptionInstance<?>, Supplier<RecipeGrid>> recipeGrids = new LinkedHashMap<>();

	public JustScryingConfigScreen(final Screen lastScreen) {
		super(lastScreen, Minecraft.getInstance().options, TITLE);
	}

	@Override
	protected void addOptions() {
		this.recipeGrids.clear();
		this.list
			.addSmall(
				JustScryingOptions.effect(),
				JustScryingOptions.libraryBooks(),
				this.recipe(JustScryingOptions.parrotArmorRecipe(OptionInstance.noTooltip()), RecipeGrid::parrotArmor)
			);
	}

	private OptionInstance<?> recipe(final OptionInstance<?> option, final Supplier<RecipeGrid> grid) {
		this.recipeGrids.put(option, grid);
		return option;
	}

	@Override
	public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float partialTicks) {
		super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
		if (this.list == null) {
			return;
		}

		for (Map.Entry<OptionInstance<?>, Supplier<RecipeGrid>> entry : this.recipeGrids.entrySet()) {
			AbstractWidget widget = this.list.findOption(entry.getKey());
			if (widget != null && widget.isHovered()) {
				graphics.tooltip(this.font, entry.getValue().get().tooltip(this.font, RECIPE_TOOLTIP), mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null, false);
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
