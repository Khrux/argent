package com.khrux.justsilver.client;

import com.khrux.justsilver.client.renderer.blockentity.SilverBellRenderer;
import com.khrux.justsilver.world.level.block.entity.JustSilverBlockEntityTypes;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class JustSilverClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockEntityRenderers.register(JustSilverBlockEntityTypes.SILVER_BELL, SilverBellRenderer::new);
	}
}
