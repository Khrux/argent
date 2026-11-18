package com.khrux.justscrying.client;

import com.khrux.justscrying.client.gui.screens.JustScryingConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class JustScryingModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return JustScryingConfigScreen::new;
	}
}
