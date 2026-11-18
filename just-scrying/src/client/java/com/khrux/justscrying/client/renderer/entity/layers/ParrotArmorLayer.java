package com.khrux.justscrying.client.renderer.entity.layers;

import com.khrux.justscrying.JustScrying;
import com.khrux.justscrying.client.model.geom.JustScryingModelLayers;
import com.khrux.justscrying.client.renderer.entity.ArmoredParrotRenderer;
import com.khrux.justscrying.world.item.JustScryingItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.minecraft.client.model.animal.parrot.ParrotModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
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
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

public class ParrotArmorLayer extends RenderLayer<ParrotRenderState, ParrotModel> {
	private static final Identifier TEXTURE = JustScrying.id("textures/entity/parrot/parrot_armor.png");
	private static final float PLUME_X = 0.0F;
	private static final float PLUME_Y = -2.6F / 16.0F;
	private static final float PLUME_Z = 0.25F / 16.0F;
	private static final float PLUME_SCALE = 0.3F;
	private static final float PLUME_TILT = -18.0F;
	private final ParrotModel model;

	public ParrotArmorLayer(final RenderLayerParent<ParrotRenderState, ParrotModel> renderer, final EntityModelSet modelSet) {
		super(renderer);
		this.model = new ParrotModel(modelSet.bakeLayer(JustScryingModelLayers.PARROT_ARMOR));
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
		if (!armor.is(JustScryingItems.PARROT_ARMOR)) {
			return;
		}

		submitNodeCollector.submitModel(
			this.model,
			state,
			poseStack,
			armor.hasFoil() ? RenderTypes.armorCutoutNoCullGlint(TEXTURE) : RenderTypes.armorCutoutNoCull(TEXTURE),
			lightCoords,
			OverlayTexture.NO_OVERLAY,
			DyedItemColor.getOrDefault(armor, DyedItemColor.LEATHER_COLOR),
			null,
			state.outlineColor
		);
		ItemStackRenderState plume = ((FabricRenderState)state).getDataOrDefault(ArmoredParrotRenderer.PLUME, null);
		if (plume == null || plume.isEmpty()) {
			return;
		}

		poseStack.pushPose();
		ModelPart root = this.getParentModel().root();
		root.translateAndRotate(poseStack);
		root.getChild("head").translateAndRotate(poseStack);
		poseStack.translate(PLUME_X, PLUME_Y, PLUME_Z);
		poseStack.scale(PLUME_SCALE, -PLUME_SCALE, -PLUME_SCALE);
		poseStack.rotateDegrees(Axis.XP, PLUME_TILT);
		poseStack.rotateDegrees(Axis.YP, 90.0F);
		poseStack.translate(-0.09F, 0.49F, 0.0F);
		poseStack.rotateDegrees(Axis.ZP, 45.0F);
		plume.submit(poseStack, submitNodeCollector, lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
		poseStack.popPose();
	}
}
