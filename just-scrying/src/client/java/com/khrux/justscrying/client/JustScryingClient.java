package com.khrux.justscrying.client;

import com.khrux.justscrying.client.model.geom.JustScryingModelLayers;
import com.khrux.justscrying.client.player.ScryingView;
import com.khrux.justscrying.client.renderer.entity.ArmoredParrotRenderer;
import com.khrux.justscrying.client.renderer.entity.PlainArmorWolfRenderer;
import com.khrux.justscrying.client.renderer.entity.layers.ParrotArmorLayer;
import com.khrux.justscrying.network.protocol.ScryingLinkPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionEvents;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityTypes;

public class JustScryingClient implements ClientModInitializer {
	private static final ScryingView SCRYING_VIEW = new ScryingView();

	@Override
	public void onInitializeClient() {
		ModelLayerRegistry.registerModelLayer(JustScryingModelLayers.PARROT_ARMOR, ParrotArmorLayer::createArmorLayer);
		EntityRenderers.register(EntityTypes.PARROT, ArmoredParrotRenderer::new);
		EntityRenderers.register(EntityTypes.WOLF, PlainArmorWolfRenderer::new);
		LevelExtractionEvents.END_EXTRACTION.register(SCRYING_VIEW::extract);
		ClientTickEvents.START_CLIENT_TICK.register(SCRYING_VIEW::startTick);
		ClientTickEvents.END_CLIENT_TICK.register(SCRYING_VIEW::endTick);
		ClientPlayNetworking.registerGlobalReceiver(ScryingLinkPayload.TYPE, (payload, context) -> SCRYING_VIEW.link(context.client(), payload.petId()));
	}

	public static ScryingView scryingView() {
		return SCRYING_VIEW;
	}
}
