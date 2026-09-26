package com.khrux.argent.client;

import com.khrux.argent.client.gui.screens.ArgentConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ArgentModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return ArgentConfigScreen::new;
	}
}
