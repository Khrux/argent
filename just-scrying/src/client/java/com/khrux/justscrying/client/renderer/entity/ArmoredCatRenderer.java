package com.khrux.justscrying.client.renderer.entity;

import com.khrux.justscrying.client.renderer.entity.layers.CatArmorLayer;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.renderer.entity.CatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.CatRenderState;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.item.ItemStack;

public class ArmoredCatRenderer extends CatRenderer {
	public static final RenderStateDataKey<ItemStack> BODY_ARMOR = RenderStateDataKey.create(() -> "justscrying:cat_body_armor");

	public ArmoredCatRenderer(final EntityRendererProvider.Context context) {
		super(context);
		this.addLayer(new CatArmorLayer(this, context.getModelSet()));
	}

	@Override
	public void extractRenderState(final Cat entity, final CatRenderState state, final float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		((FabricRenderState)state).setData(BODY_ARMOR, entity.getBodyArmorItem());
	}
}
