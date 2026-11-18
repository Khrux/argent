package com.khrux.justmirrors.client;

import com.khrux.justmirrors.client.gui.screens.JustMirrorsConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class JustMirrorsModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return JustMirrorsConfigScreen::new;
	}
}
