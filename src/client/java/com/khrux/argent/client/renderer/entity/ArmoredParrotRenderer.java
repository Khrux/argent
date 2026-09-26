package com.khrux.argent.client.renderer.entity;

import com.khrux.argent.client.renderer.entity.layers.SilverParrotArmorLayer;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.client.renderer.entity.state.ParrotRenderState;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.item.ItemStack;

public class ArmoredParrotRenderer extends ParrotRenderer {
	public static final RenderStateDataKey<ItemStack> BODY_ARMOR = RenderStateDataKey.create(() -> "argent:body_armor");

	public ArmoredParrotRenderer(final EntityRendererProvider.Context context) {
		super(context);
		this.addLayer(new SilverParrotArmorLayer(this, context.getModelSet()));
	}

	@Override
	public void extractRenderState(final Parrot entity, final ParrotRenderState state, final float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		((FabricRenderState)state).setData(BODY_ARMOR, entity.getBodyArmorItem());
	}
}
