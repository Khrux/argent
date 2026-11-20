package com.khrux.justscrying.client.gui.screens;

import com.khrux.justscrying.client.gui.components.PetArmorRecipeGrid;
import com.khrux.justscrying.config.JustScryingConfig;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.fabricmc.loader.api.FabricLoader;
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
	private final Map<OptionInstance<?>, Supplier<PetArmorRecipeGrid>> recipeGrids = new LinkedHashMap<>();

	public JustScryingConfigScreen(final Screen lastScreen) {
		super(lastScreen, Minecraft.getInstance().options, TITLE);
	}

	@Override
	protected void addOptions() {
		this.recipeGrids.clear();
		List<OptionInstance<?>> options = new ArrayList<>();
		options.add(JustScryingOptions.effect());
		if (!FabricLoader.getInstance().isModLoaded("argent")) {
			options.add(JustScryingOptions.fishingBooks());
		}

		options.add(JustScryingOptions.libraryBooks());
		options.add(JustScryingOptions.elderGuardianBook());
		options.add(this.recipe(JustScryingOptions.parrotArmorRecipe(OptionInstance.noTooltip()), PetArmorRecipeGrid::parrotArmor));
		options.add(this.recipe(JustScryingOptions.catArmorRecipe(OptionInstance.noTooltip()), PetArmorRecipeGrid::catArmor));
		this.list.addSmall(options.toArray(OptionInstance[]::new));
	}

	private OptionInstance<?> recipe(final OptionInstance<?> option, final Supplier<PetArmorRecipeGrid> grid) {
		this.recipeGrids.put(option, grid);
		return option;
	}

	@Override
	public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float partialTicks) {
		super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
		if (this.list == null) {
			return;
		}

		for (Map.Entry<OptionInstance<?>, Supplier<PetArmorRecipeGrid>> entry : this.recipeGrids.entrySet()) {
			AbstractWidget widget = this.list.findOption(entry.getKey());
			if (widget != null && widget.isHovered()) {
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
