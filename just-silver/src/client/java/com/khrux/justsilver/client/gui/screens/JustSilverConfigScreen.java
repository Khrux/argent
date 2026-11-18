package com.khrux.justsilver.client.gui.screens;

import com.khrux.justsilver.config.JustSilverConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

public class JustSilverConfigScreen extends OptionsSubScreen {
	private static final Component TITLE = Component.translatable("options.justsilver.title");

	public JustSilverConfigScreen(final Screen lastScreen) {
		super(lastScreen, Minecraft.getInstance().options, TITLE);
	}

	@Override
	protected void addOptions() {
		this.list
			.addSmall(
				JustSilverOptions.oreGeneration(),
				JustSilverOptions.silverBellRecipe(JustSilverOptions.recipeTooltip()),
				JustSilverOptions.soulLanternRecipe(JustSilverOptions.recipeTooltip())
			);
	}

	@Override
	public void removed() {
		super.removed();
		JustSilverConfig.get().save();
	}
}
