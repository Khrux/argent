package com.khrux.justwitherzombies.client.gui.screens;

import com.khrux.justwitherzombies.config.JustWitherZombiesConfig;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;

public class JustWitherZombiesOptions {
	public static OptionInstance<Boolean> meleeDamage() {
		JustWitherZombiesConfig config = JustWitherZombiesConfig.get();
		return OptionInstance.createBoolean("options.justwitherzombies.melee_damage", OptionInstance.cachedConstantTooltip(Component.translatable("options.justwitherzombies.melee_damage.tooltip")), config.meleeDamage, value -> config.meleeDamage = value);
	}

	public static OptionInstance<Boolean> itemStealing() {
		JustWitherZombiesConfig config = JustWitherZombiesConfig.get();
		return OptionInstance.createBoolean("options.justwitherzombies.item_stealing", OptionInstance.cachedConstantTooltip(Component.translatable("options.justwitherzombies.item_stealing.tooltip")), config.itemStealing, value -> config.itemStealing = value);
	}

	public static OptionInstance<Boolean> scryingBooks() {
		JustWitherZombiesConfig config = JustWitherZombiesConfig.get();
		return OptionInstance.createBoolean("options.justwitherzombies.scrying_books", OptionInstance.cachedConstantTooltip(Component.translatable("options.justwitherzombies.scrying_books.tooltip")), config.scryingBooks, value -> config.scryingBooks = value);
	}
}
