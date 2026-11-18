package com.khrux.justscrying.client.gui.screens;

import com.khrux.justscrying.config.JustScryingConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

public class JustScryingConfigScreen extends OptionsSubScreen {
	private static final Component TITLE = Component.translatable("options.justscrying.title");

	public JustScryingConfigScreen(final Screen lastScreen) {
		super(lastScreen, Minecraft.getInstance().options, TITLE);
	}

	@Override
	protected void addOptions() {
		this.list
			.addSmall(
				JustScryingOptions.effect(), JustScryingOptions.libraryBooks(), JustScryingOptions.parrotArmorRecipe(JustScryingOptions.recipeTooltip())
			);
	}

	@Override
	public void removed() {
		super.removed();
		JustScryingConfig.get().save();
	}
}
