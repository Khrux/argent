package com.khrux.justwitherzombies.client;

import com.khrux.justwitherzombies.client.gui.screens.JustWitherZombiesConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class JustWitherZombiesModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return JustWitherZombiesConfigScreen::new;
	}
}
