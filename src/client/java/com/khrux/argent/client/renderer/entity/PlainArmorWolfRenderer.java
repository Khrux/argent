package com.khrux.argent.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.animal.wolf.Wolf;

public class PlainArmorWolfRenderer extends WolfRenderer {
	public PlainArmorWolfRenderer(final EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void extractRenderState(final Wolf entity, final WolfRenderState state, final float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		if (state.bodyArmorItem.hasFoil()) {
			state.bodyArmorItem.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, false);
		}
	}
}
