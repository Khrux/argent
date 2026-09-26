package com.khrux.argent.client.renderer.blockentity;

import com.khrux.argent.Argent;
import com.khrux.argent.world.level.block.entity.SilverBellBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.bell.BellModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BellRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class SilverBellRenderer implements BlockEntityRenderer<SilverBellBlockEntity, BellRenderState> {
	public static final SpriteId BELL_TEXTURE = new SpriteId(TextureAtlas.LOCATION_BLOCKS, Argent.id("block/silver_bell_body"));
	private final SpriteGetter sprites;
	private final BellModel model;

	public SilverBellRenderer(final BlockEntityRendererProvider.Context context) {
		this.sprites = context.sprites();
		this.model = new BellModel(context.bakeLayer(ModelLayers.BELL));
	}

	@Override
	public BellRenderState createRenderState() {
		return new BellRenderState();
	}

	@Override
	public void extractRenderState(
		final SilverBellBlockEntity blockEntity,
		final BellRenderState state,
		final float partialTicks,
		final Vec3 cameraPosition,
		final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
	) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
		state.ticks = blockEntity.ticks + partialTicks;
		state.shakeDirection = blockEntity.shaking ? blockEntity.clickDirection : null;
	}

	@Override
	public void submit(final BellRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
		BellModel.State modelState = new BellModel.State(state.ticks, state.shakeDirection);
		this.model.setupAnim(modelState);
		submitNodeCollector.submitModel(this.model, modelState, poseStack, state.lightCoords, OverlayTexture.NO_OVERLAY, -1, BELL_TEXTURE, this.sprites, 0);
		if (state.breakProgress != null) {
			submitNodeCollector.order(1)
				.submitCrumblingOverlay(
					this.model, modelState, poseStack, BELL_TEXTURE.renderType(this.model.renderType()), state.lightCoords, OverlayTexture.NO_OVERLAY, -1, state.breakProgress
				);
		}
	}
}
