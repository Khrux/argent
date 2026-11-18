package com.khrux.justwitherzombies.client.gui.screens;

import com.khrux.justwitherzombies.config.JustWitherZombiesConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

public class JustWitherZombiesConfigScreen extends OptionsSubScreen {
	private static final Component TITLE = Component.translatable("options.justwitherzombies.title");

	public JustWitherZombiesConfigScreen(final Screen lastScreen) {
		super(lastScreen, Minecraft.getInstance().options, TITLE);
	}

	@Override
	protected void addOptions() {
		this.list.addSmall(JustWitherZombiesOptions.meleeDamage(), JustWitherZombiesOptions.itemStealing());
		if (FabricLoader.getInstance().isModLoaded("justscrying")) {
			this.list.addSmall(JustWitherZombiesOptions.scryingBooks());
		}
	}

	@Override
	public void removed() {
		super.removed();
		JustWitherZombiesConfig.get().save();
	}
}
