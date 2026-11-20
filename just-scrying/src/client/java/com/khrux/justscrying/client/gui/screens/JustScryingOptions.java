package com.khrux.justscrying.client.gui.screens;

import com.khrux.justscrying.config.JustScryingConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;

public class JustScryingOptions {
	public static final Component BOOKS_LOOTED_FROM = Component.translatable("options.justscrying.books_looted_from").withStyle(ChatFormatting.GRAY);
	public static final Component ELDER_GUARDIAN = Component.translatable("options.justscrying.books_looted_from.elder_guardian");
	public static final Component ELDER_GUARDIAN_TOOLTIP = Component.translatable("options.justscrying.books_looted_from.elder_guardian.tooltip");
	public static final Component FISHING_LOOT = Component.translatable("options.justscrying.books_looted_from.fishing");
	public static final Component FISHING_LOOT_TOOLTIP = Component.translatable("options.justscrying.books_looted_from.fishing.tooltip");
	public static final Component STRONGHOLD_LIBRARY = Component.translatable("options.justscrying.books_looted_from.library");
	public static final Component STRONGHOLD_LIBRARY_TOOLTIP = Component.translatable("options.justscrying.books_looted_from.library.tooltip");
	public static final Component ARMOR_RECIPES = Component.translatable("options.justscrying.armor_recipes").withStyle(ChatFormatting.GRAY);
	public static final Component PARROT = Component.translatable("options.justscrying.armor_recipes.parrot");
	public static final Component CAT = Component.translatable("options.justscrying.armor_recipes.cat");
	private static final Component SCRYING_TOOLTIP = Component.translatable("options.justscrying.scrying.tooltip");

	public static OptionInstance<Boolean> effect() {
		JustScryingConfig config = JustScryingConfig.get();
		return OptionInstance.createBoolean(
			"options.justscrying.scrying", OptionInstance.cachedConstantTooltip(SCRYING_TOOLTIP), config.effect, value -> config.effect = value
		);
	}
}
