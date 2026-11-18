package com.khrux.justscrying.client.renderer.entity.layers;

import com.khrux.justscrying.JustScrying;
import com.khrux.justscrying.client.model.geom.JustScryingModelLayers;
import com.khrux.justscrying.client.renderer.entity.ArmoredCatRenderer;
import com.khrux.justscrying.world.item.JustScryingItems;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.minecraft.client.model.animal.feline.AbstractFelineModel;
import net.minecraft.client.model.animal.feline.AdultCatModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.CatRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

public class CatArmorLayer extends RenderLayer<CatRenderState, AbstractFelineModel<CatRenderState>> {
	private static final Identifier TEXTURE = JustScrying.id("textures/entity/cat/cat_armor.png");
	private static final Identifier OVERLAY_TEXTURE = JustScrying.id("textures/entity/cat/cat_armor_overlay.png");
	private final AdultCatModel model;

	public CatArmorLayer(final RenderLayerParent<CatRenderState, AbstractFelineModel<CatRenderState>> renderer, final EntityModelSet modelSet) {
		super(renderer);
		this.model = new AdultCatModel(modelSet.bakeLayer(JustScryingModelLayers.CAT_ARMOR));
	}

	public static LayerDefinition createArmorLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild(
			"head",
			CubeListBuilder.create().texOffs(0, 24).addBox(-2.0F, -2.0F, -2.5F, 4.0F, 1.0F, 3.0F, new CubeDeformation(0.2F)),
			PartPose.offset(0.0F, 15.0F, -9.0F)
		);
		root.addOrReplaceChild(
			"body",
			CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 3.0F, -8.0F, 4.0F, 14.0F, 6.0F, new CubeDeformation(0.3F)),
			PartPose.offsetAndRotation(0.0F, 12.0F, -10.0F, (float)(Math.PI / 2), 0.0F, 0.0F)
		);
		root.addOrReplaceChild("tail1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 15.0F, 8.0F, 0.9F, 0.0F, 0.0F));
		root.addOrReplaceChild("tail2", CubeListBuilder.create(), PartPose.offset(0.0F, 20.0F, 14.0F));
		root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create(), PartPose.offset(1.1F, 18.0F, 5.0F));
		root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create(), PartPose.offset(-1.1F, 18.0F, 5.0F));
		root.addOrReplaceChild("left_front_leg", CubeListBuilder.create(), PartPose.offset(1.2F, 14.1F, -5.0F));
		root.addOrReplaceChild("right_front_leg", CubeListBuilder.create(), PartPose.offset(-1.2F, 14.1F, -5.0F));
		return LayerDefinition.create(mesh, 64, 32).apply(AdultCatModel.CAT_TRANSFORMER);
	}

	@Override
	public void submit(
		final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final CatRenderState state, final float yRot, final float xRot
	) {
		ItemStack armor = ((FabricRenderState)state).getDataOrDefault(ArmoredCatRenderer.BODY_ARMOR, ItemStack.EMPTY);
		if (state.isBaby || !armor.is(JustScryingItems.CAT_ARMOR)) {
			return;
		}

		this.submitLayer(poseStack, submitNodeCollector, lightCoords, state, armor, TEXTURE, -1);
		DyedItemColor dye = armor.get(DataComponents.DYED_COLOR);
		if (dye != null) {
			this.submitLayer(poseStack, submitNodeCollector.order(1), lightCoords, state, armor, OVERLAY_TEXTURE, dye.rgb() | 0xFF000000);
		}
	}

	private void submitLayer(
		final PoseStack poseStack,
		final OrderedSubmitNodeCollector collector,
		final int lightCoords,
		final CatRenderState state,
		final ItemStack armor,
		final Identifier texture,
		final int color
	) {
		collector.submitModel(
			this.model,
			state,
			poseStack,
			armor.hasFoil() ? RenderTypes.armorCutoutNoCullGlint(texture) : RenderTypes.armorCutoutNoCull(texture),
			lightCoords,
			OverlayTexture.NO_OVERLAY,
			color,
			null,
			state.outlineColor
		);
	}
}
