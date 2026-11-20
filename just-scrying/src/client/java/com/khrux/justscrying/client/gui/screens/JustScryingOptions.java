package com.khrux.justscrying.client.gui.screens;

import com.khrux.justscrying.config.JustScryingConfig;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;

public class JustScryingOptions {
	private static final Component SCRYING_TOOLTIP = Component.translatable("options.justscrying.scrying.tooltip");
	private static final Component LIBRARY_BOOKS_TOOLTIP = Component.translatable("options.justscrying.library_books.tooltip");
	private static final Component FISHING_BOOKS_TOOLTIP = Component.translatable("options.justscrying.fishing_books.tooltip");
	private static final Component ELDER_GUARDIAN_BOOK_TOOLTIP = Component.translatable("options.justscrying.elder_guardian_book.tooltip");

	public static OptionInstance<Boolean> effect() {
		JustScryingConfig config = JustScryingConfig.get();
		return OptionInstance.createBoolean(
			"options.justscrying.scrying", OptionInstance.cachedConstantTooltip(SCRYING_TOOLTIP), config.effect, value -> config.effect = value
		);
	}

	public static OptionInstance<Boolean> libraryBooks() {
		JustScryingConfig config = JustScryingConfig.get();
		return OptionInstance.createBoolean(
			"options.justscrying.library_books",
			OptionInstance.cachedConstantTooltip(LIBRARY_BOOKS_TOOLTIP),
			config.libraryBooks,
			value -> config.libraryBooks = value
		);
	}

	public static OptionInstance<Boolean> fishingBooks() {
		JustScryingConfig config = JustScryingConfig.get();
		return OptionInstance.createBoolean(
			"options.justscrying.fishing_books",
			OptionInstance.cachedConstantTooltip(FISHING_BOOKS_TOOLTIP),
			config.fishingBooks,
			value -> config.fishingBooks = value
		);
	}

	public static OptionInstance<Boolean> elderGuardianBook() {
		JustScryingConfig config = JustScryingConfig.get();
		return OptionInstance.createBoolean(
			"options.justscrying.elder_guardian_book",
			OptionInstance.cachedConstantTooltip(ELDER_GUARDIAN_BOOK_TOOLTIP),
			config.elderGuardianBook,
			value -> config.elderGuardianBook = value
		);
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
