package com.khrux.argent.client.renderer.entity.layers;

import com.khrux.argent.Argent;
import com.khrux.argent.client.model.geom.ArgentModelLayers;
import com.khrux.argent.client.renderer.entity.ArmoredParrotRenderer;
import com.khrux.argent.world.item.ArgentItems;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.minecraft.client.model.animal.parrot.ParrotModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.ParrotRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class SilverParrotArmorLayer extends RenderLayer<ParrotRenderState, ParrotModel> {
	private static final Identifier TEXTURE = Argent.id("textures/entity/parrot/silver_parrot_armor.png");
	private final ParrotModel model;

	public SilverParrotArmorLayer(final RenderLayerParent<ParrotRenderState, ParrotModel> renderer, final EntityModelSet modelSet) {
		super(renderer);
		this.model = new ParrotModel(modelSet.bakeLayer(ArgentModelLayers.SILVER_PARROT_ARMOR));
	}

	public static LayerDefinition createArmorLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild(
			"body",
			CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.3F)),
			PartPose.offsetAndRotation(0.0F, 16.5F, -3.0F, 0.4937F, 0.0F, 0.0F)
		);
		root.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 21.07F, 1.16F, 1.015F, 0.0F, 0.0F));
		root.addOrReplaceChild("left_wing", CubeListBuilder.create(), PartPose.offsetAndRotation(1.5F, 16.94F, -2.76F, -0.6981F, (float)-Math.PI, 0.0F));
		root.addOrReplaceChild("right_wing", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.5F, 16.94F, -2.76F, -0.6981F, (float)-Math.PI, 0.0F));
		PartDefinition head = root.addOrReplaceChild(
			"head",
			CubeListBuilder.create().texOffs(0, 8).addBox(-1.0F, -1.5F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.25F)),
			PartPose.offset(0.0F, 15.69F, -2.76F)
		);
		head.addOrReplaceChild(
			"crown", CubeListBuilder.create().texOffs(0, 12).addBox(-1.0F, -0.5F, -2.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, -2.0F, -1.0F)
		);
		root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offsetAndRotation(1.0F, 22.0F, -1.05F, -0.0299F, 0.0F, 0.0F));
		root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.0F, 22.0F, -1.05F, -0.0299F, 0.0F, 0.0F));
		return LayerDefinition.create(mesh, 32, 32);
	}

	@Override
	public void submit(
		final PoseStack poseStack,
		final SubmitNodeCollector submitNodeCollector,
		final int lightCoords,
		final ParrotRenderState state,
		final float yRot,
		final float xRot
	) {
		ItemStack armor = ((FabricRenderState)state).getDataOrDefault(ArmoredParrotRenderer.BODY_ARMOR, ItemStack.EMPTY);
		if (!armor.is(ArgentItems.SILVER_PARROT_ARMOR)) {
			return;
		}

		submitNodeCollector.submitModel(
			this.model,
			state,
			poseStack,
			armor.hasFoil() ? RenderTypes.armorCutoutNoCullGlint(TEXTURE) : RenderTypes.armorCutoutNoCull(TEXTURE),
			lightCoords,
			OverlayTexture.NO_OVERLAY,
			state.outlineColor
		);
	}
}
