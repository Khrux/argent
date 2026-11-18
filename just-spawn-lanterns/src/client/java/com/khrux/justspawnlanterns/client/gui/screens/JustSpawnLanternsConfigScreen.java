package com.khrux.justspawnlanterns.client.gui.screens;

import com.khrux.justspawnlanterns.config.JustSpawnLanternsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

public class JustSpawnLanternsConfigScreen extends OptionsSubScreen {
	private static final Component TITLE = Component.translatable("options.justspawnlanterns.title");

	public JustSpawnLanternsConfigScreen(final Screen lastScreen) {
		super(lastScreen, Minecraft.getInstance().options, TITLE);
	}

	@Override
	protected void addOptions() {
		this.list.addSmall(JustSpawnLanternsOptions.markers());
	}

	@Override
	public void removed() {
		super.removed();
		JustSpawnLanternsConfig.get().save();
	}
}
