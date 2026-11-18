package com.khrux.justspawnlanterns.client;

import com.khrux.justspawnlanterns.client.particle.SoulLanternParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class JustSpawnLanternsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_LEVEL_TICK.register(SoulLanternParticles::tick);
	}
}
