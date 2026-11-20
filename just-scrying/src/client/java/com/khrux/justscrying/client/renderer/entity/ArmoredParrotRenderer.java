package com.khrux.justscrying.client.renderer.entity;

import com.khrux.justscrying.client.renderer.entity.layers.ParrotArmorLayer;
import com.khrux.justscrying.world.item.JustScryingItems;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.client.renderer.entity.state.ParrotRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ArmoredParrotRenderer extends ParrotRenderer {
	public static final RenderStateDataKey<ItemStack> BODY_ARMOR = RenderStateDataKey.create(() -> "justscrying:body_armor");
	public static final RenderStateDataKey<ItemStackRenderState> PLUME = RenderStateDataKey.create(() -> "justscrying:plume");
	private static final ItemStack FEATHER = new ItemStack(Items.FEATHER);
	private final ItemModelResolver itemModelResolver;

	public ArmoredParrotRenderer(final EntityRendererProvider.Context context) {
		super(context);
		this.itemModelResolver = context.getItemModelResolver();
		this.addLayer(new ParrotArmorLayer(this, context.getModelSet()));
	}

	@Override
	public void extractRenderState(final Parrot entity, final ParrotRenderState state, final float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		ItemStack armor = entity.getBodyArmorItem();
		((FabricRenderState)state).setData(BODY_ARMOR, armor);
		ItemStackRenderState plume = ((FabricRenderState)state).getDataOrDefault(PLUME, null);
		if (plume == null) {
			plume = new ItemStackRenderState();
			((FabricRenderState)state).setData(PLUME, plume);
		}

		if (armor.is(JustScryingItems.PARROT_ARMOR)) {
			this.itemModelResolver.updateForLiving(plume, FEATHER, ItemDisplayContext.NONE, entity);
		} else {
			plume.clear();
		}
	}
}
