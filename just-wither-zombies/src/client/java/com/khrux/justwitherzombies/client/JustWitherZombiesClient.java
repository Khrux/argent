package com.khrux.justwitherzombies.client;

import com.khrux.justwitherzombies.client.model.geom.JustWitherZombiesModelLayers;
import com.khrux.justwitherzombies.client.model.monster.WitherZombieModel;
import com.khrux.justwitherzombies.client.player.WitherZombieGaze;
import com.khrux.justwitherzombies.client.renderer.entity.WitherZombieRenderer;
import com.khrux.justwitherzombies.client.renderer.special.ReflectiveShieldSpecialRenderer;
import com.khrux.justwitherzombies.world.entity.JustWitherZombiesEntityTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionEvents;
import net.minecraft.client.renderer.entity.EntityRenderers;

public class JustWitherZombiesClient implements ClientModInitializer {
	private static final WitherZombieGaze WITHER_ZOMBIE_GAZE = new WitherZombieGaze();

	@Override
	public void onInitializeClient() {
		ReflectiveShieldSpecialRenderer.bootstrap();
		ModelLayerRegistry.registerModelLayer(JustWitherZombiesModelLayers.WITHER_ZOMBIE, WitherZombieModel::createBodyLayer);
		EntityRenderers.register(JustWitherZombiesEntityTypes.WITHER_ZOMBIE, WitherZombieRenderer::new);
		LevelExtractionEvents.END_EXTRACTION.register(WITHER_ZOMBIE_GAZE::extract);
	}
}
