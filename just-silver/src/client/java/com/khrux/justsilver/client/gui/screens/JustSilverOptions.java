package com.khrux.justsilver.client.gui.screens;

import com.khrux.justsilver.config.JustSilverConfig;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;

public class JustSilverOptions {
	public static OptionInstance<Boolean> oreGeneration() {
		JustSilverConfig config = JustSilverConfig.get();
		return OptionInstance.createBoolean("options.justsilver.ore_generation", OptionInstance.cachedConstantTooltip(Component.translatable("options.justsilver.ore_generation.tooltip")), config.oreGeneration, value -> config.oreGeneration = value);
	}

	public static OptionInstance<Boolean> silverBellRecipe(final OptionInstance.TooltipSupplier<Boolean> tooltip) {
		JustSilverConfig config = JustSilverConfig.get();
		return OptionInstance.createBoolean("options.justsilver.silver_bell_recipe", tooltip, config.silverBellRecipe, value -> config.silverBellRecipe = value);
	}

	public static OptionInstance<Boolean> soulLanternRecipe(final OptionInstance.TooltipSupplier<Boolean> tooltip) {
		JustSilverConfig config = JustSilverConfig.get();
		return OptionInstance.createBoolean("options.justsilver.soul_lantern_recipe", tooltip, config.soulLanternRecipe, value -> config.soulLanternRecipe = value);
	}
}
