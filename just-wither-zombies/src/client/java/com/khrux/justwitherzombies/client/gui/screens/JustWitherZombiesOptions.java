package com.khrux.justwitherzombies.client.gui.screens;

import com.khrux.justwitherzombies.config.JustWitherZombiesConfig;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;

public class JustWitherZombiesOptions {
	private static final Component MELEE_DAMAGE_TOOLTIP = Component.translatable("options.justwitherzombies.melee_damage.tooltip");
	private static final Component ITEM_STEALING_TOOLTIP = Component.translatable("options.justwitherzombies.item_stealing.tooltip");
	private static final Component SCRYING_BOOKS_TOOLTIP = Component.translatable("options.justwitherzombies.scrying_books.tooltip");

	public static OptionInstance<Boolean> meleeDamage() {
		JustWitherZombiesConfig config = JustWitherZombiesConfig.get();
		return OptionInstance.createBoolean(
			"options.justwitherzombies.melee_damage",
			OptionInstance.cachedConstantTooltip(MELEE_DAMAGE_TOOLTIP),
			config.meleeDamage,
			value -> config.meleeDamage = value
		);
	}

	public static OptionInstance<Boolean> itemStealing() {
		JustWitherZombiesConfig config = JustWitherZombiesConfig.get();
		return OptionInstance.createBoolean(
			"options.justwitherzombies.item_stealing",
			OptionInstance.cachedConstantTooltip(ITEM_STEALING_TOOLTIP),
			config.itemStealing,
			value -> config.itemStealing = value
		);
	}

	public static OptionInstance<Boolean> scryingBooks() {
		JustWitherZombiesConfig config = JustWitherZombiesConfig.get();
		return OptionInstance.createBoolean(
			"options.justwitherzombies.scrying_books",
			OptionInstance.cachedConstantTooltip(SCRYING_BOOKS_TOOLTIP),
			config.scryingBooks,
			value -> config.scryingBooks = value
		);
	}
}
