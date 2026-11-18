package com.khrux.justsilver.client;

import com.khrux.justsilver.client.gui.screens.JustSilverConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class JustSilverModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return JustSilverConfigScreen::new;
	}
}
