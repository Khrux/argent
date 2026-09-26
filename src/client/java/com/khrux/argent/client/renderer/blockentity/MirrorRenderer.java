package com.khrux.argent.client.renderer.blockentity;

import com.khrux.argent.client.ArgentClient;
import com.khrux.argent.client.renderer.MirrorReflections;
import com.khrux.argent.client.renderer.blockentity.state.MirrorRenderState;
import com.khrux.argent.world.level.block.MirrorBlock;
import com.khrux.argent.world.level.block.entity.MirrorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.jspecify.annotations.Nullable;

public class MirrorRenderer implements BlockEntityRenderer<MirrorBlockEntity, MirrorRenderState> {
	private static final int REFLECTION_COLOR = ARGB.color(0xAA, 0xFFFFFF);
	private static final float PIXEL = 1.0F / 16.0F;
	private static final float DEGREES_PER_ROTATION = 22.5F;
	private static final float LEAN_DEGREES = 12.0F;
	private static final float GLASS_LEFT = 12.0F;
	private static final float GLASS_RIGHT = 4.0F;
	private static final float GLASS_BOTTOM = 3.0F;
	private static final float GLASS_MIDDLE = 16.0F;
	private static final float GLASS_TOP = 29.0F;
	private static final float REFLECTION_Z = 7.75F;
	private static final int COLUMNS = 4;
	private static final int ROWS = 13;

	public MirrorRenderer(final BlockEntityRendererProvider.Context context) {
	}

	private static Matrix4f glassPose(final int rotation) {
		int turn = (rotation + 8) % 16;
		return new Matrix4f()
			.translate(0.5F, 0.5F, 0.5F)
			.rotateY(-turn * DEGREES_PER_ROTATION * Mth.DEG_TO_RAD)
			.translate(-0.5F, -0.5F, -0.5F)
			.translate(0.5F, 1.0F, 0.5F)
			.rotateX(LEAN_DEGREES * Mth.DEG_TO_RAD)
			.translate(-0.5F, -1.0F, -0.5F);
	}

	@Override
	public MirrorRenderState createRenderState() {
		return new MirrorRenderState();
	}

	@Override
	public void extractRenderState(
		final MirrorBlockEntity blockEntity,
		final MirrorRenderState state,
		final float partialTicks,
		final Vec3 cameraPosition,
		final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
	) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
		state.rotation = blockEntity.getBlockState().getValue(MirrorBlock.ROTATION);
	}

	@Override
	public void submit(final MirrorRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
		MirrorReflections reflections = ArgentClient.reflections();
		poseStack.pushPose();
		poseStack.mulPose(glassPose(state.rotation));
		Matrix4f surface = new Matrix4f(poseStack.last().pose());
		Identifier reflection = reflections.request(
			null,
			surface, GLASS_RIGHT * PIXEL, GLASS_BOTTOM * PIXEL, GLASS_LEFT * PIXEL, GLASS_TOP * PIXEL, REFLECTION_Z * PIXEL
		);
		if (reflection == null) {
			poseStack.popPose();
			return;
		}

		submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.text(reflection), (pose, buffer) -> {
			float columnWidth = (GLASS_LEFT - GLASS_RIGHT) / COLUMNS;
			float rowHeight = (GLASS_TOP - GLASS_BOTTOM) / ROWS;
			for (int column = 0; column < COLUMNS; column++) {
				for (int row = 0; row < ROWS; row++) {
					float left = GLASS_LEFT - column * columnWidth;
					float right = left - columnWidth;
					float bottom = GLASS_BOTTOM + row * rowHeight;
					float top = bottom + rowHeight;
					addReflectionVertex(buffer, pose, reflections, surface, left, bottom);
					addReflectionVertex(buffer, pose, reflections, surface, right, bottom);
					addReflectionVertex(buffer, pose, reflections, surface, right, top);
					addReflectionVertex(buffer, pose, reflections, surface, left, top);
				}
			}
		});
		poseStack.popPose();
	}

	private static void addReflectionVertex(
		final VertexConsumer buffer,
		final PoseStack.Pose pose,
		final MirrorReflections reflections,
		final Matrix4f surface,
		final float x,
		final float y
	) {
		Vector2f uv = reflections.textureCoordinates(surface, x * PIXEL, y * PIXEL, REFLECTION_Z * PIXEL);
		buffer.addVertex(pose, x * PIXEL, y * PIXEL, REFLECTION_Z * PIXEL).setColor(REFLECTION_COLOR).setUv(uv.x, uv.y).setLight(LightCoordsUtil.FULL_BRIGHT);
	}

}
