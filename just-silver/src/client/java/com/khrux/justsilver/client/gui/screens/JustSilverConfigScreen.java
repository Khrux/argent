package com.khrux.justsilver.client.gui.screens;

import com.khrux.justsilver.client.gui.components.RecipeGrid;
import com.khrux.justsilver.config.JustSilverConfig;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class JustSilverConfigScreen extends OptionsSubScreen {
	private static final Component TITLE = Component.translatable("options.justsilver.title");
	private static final Component RECIPE_TOOLTIP = Component.translatable("options.justsilver.recipe.tooltip");
	private static final Component SOUL_LANTERN_RECIPE = Component.translatable("options.justsilver.soul_lantern_recipe").withStyle(ChatFormatting.GRAY);
	private static final Component IRON = Component.translatable("options.justsilver.soul_lantern_recipe.iron");
	private static final Component SILVER = Component.translatable("options.justsilver.soul_lantern_recipe.silver");
	private static final int CHECKBOX_WIDTH = 150;
	private final Map<AbstractWidget, Supplier<@Nullable RecipeGrid>> recipeGrids = new LinkedHashMap<>();

	public JustSilverConfigScreen(final Screen lastScreen) {
		super(lastScreen, Minecraft.getInstance().options, TITLE);
	}

	@Override
	protected void addOptions() {
		this.recipeGrids.clear();
		JustSilverConfig config = JustSilverConfig.get();
		this.list
			.addSmall(
				List.of(
					JustSilverOptions.oreGeneration().createButton(this.options),
					this.recipe(JustSilverOptions.silverBellRecipe(OptionInstance.noTooltip()).createButton(this.options), RecipeGrid::silverBell)
				)
			);
		this.list.addHeader(SOUL_LANTERN_RECIPE);
		this.list
			.addSmall(
				List.of(
					this.recipe(this.checkbox(IRON, config.soulLanternIron, value -> config.soulLanternIron = value), RecipeGrid::soulLantern),
					this.recipe(this.checkbox(SILVER, config.soulLanternSilver, value -> config.soulLanternSilver = value), RecipeGrid::soulLantern)
				)
			);
	}

	private Checkbox checkbox(final Component label, final boolean selected, final BooleanConsumer onChange) {
		return Checkbox.builder(label, this.font).selected(selected).maxWidth(CHECKBOX_WIDTH).onValueChange((checkbox, value) -> onChange.accept(value)).build();
	}

	private AbstractWidget recipe(final AbstractWidget widget, final Supplier<@Nullable RecipeGrid> grid) {
		this.recipeGrids.put(widget, grid);
		return widget;
	}

	@Override
	public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float partialTicks) {
		super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
		for (Map.Entry<AbstractWidget, Supplier<@Nullable RecipeGrid>> entry : this.recipeGrids.entrySet()) {
			if (entry.getKey().isHovered()) {
				RecipeGrid grid = entry.getValue().get();
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
		JustSilverConfig.get().save();
	}
}
