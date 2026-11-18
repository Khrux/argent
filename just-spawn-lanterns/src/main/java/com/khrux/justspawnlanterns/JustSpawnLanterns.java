package com.khrux.justspawnlanterns;

import com.khrux.justspawnlanterns.config.JustSpawnLanternsConfig;
import net.fabricmc.api.ModInitializer;

public class JustSpawnLanterns implements ModInitializer {
	public static final String MOD_ID = "justspawnlanterns";

	@Override
	public void onInitialize() {
		JustSpawnLanternsConfig.load();
	}
}
