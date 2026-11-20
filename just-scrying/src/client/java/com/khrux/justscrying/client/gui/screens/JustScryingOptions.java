package com.khrux.justscrying.client.gui.screens;

import com.khrux.justscrying.config.JustScryingConfig;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;

public class JustScryingOptions {
	public static OptionInstance<Boolean> effect() {
		JustScryingConfig config = JustScryingConfig.get();
		return OptionInstance.createBoolean("options.justscrying.scrying", OptionInstance.cachedConstantTooltip(Component.translatable("options.justscrying.scrying.tooltip")), config.effect, value -> config.effect = value);
	}

	public static OptionInstance<Boolean> libraryBooks() {
		JustScryingConfig config = JustScryingConfig.get();
		return OptionInstance.createBoolean("options.justscrying.library_books", OptionInstance.cachedConstantTooltip(Component.translatable("options.justscrying.library_books.tooltip")), config.libraryBooks, value -> config.libraryBooks = value);
	}

	public static OptionInstance<Boolean> fishingBooks() {
		JustScryingConfig config = JustScryingConfig.get();
		return OptionInstance.createBoolean("options.justscrying.fishing_books", OptionInstance.cachedConstantTooltip(Component.translatable("options.justscrying.fishing_books.tooltip")), config.fishingBooks, value -> config.fishingBooks = value);
	}

	public static OptionInstance<Boolean> parrotArmorRecipe(final OptionInstance.TooltipSupplier<Boolean> tooltip) {
		JustScryingConfig config = JustScryingConfig.get();
		return OptionInstance.createBoolean("options.justscrying.parrot_armor_recipe", tooltip, config.parrotArmorRecipe, value -> config.parrotArmorRecipe = value);
	}

	public static OptionInstance<Boolean> catArmorRecipe(final OptionInstance.TooltipSupplier<Boolean> tooltip) {
		JustScryingConfig config = JustScryingConfig.get();
		return OptionInstance.createBoolean("options.justscrying.cat_armor_recipe", tooltip, config.catArmorRecipe, value -> config.catArmorRecipe = value);
	}
}
