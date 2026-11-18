package com.khrux.justspawnlanterns.client;

import com.khrux.justspawnlanterns.client.gui.screens.JustSpawnLanternsConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class JustSpawnLanternsModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return JustSpawnLanternsConfigScreen::new;
	}
}
