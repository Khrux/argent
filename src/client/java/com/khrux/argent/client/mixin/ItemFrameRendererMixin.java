package com.khrux.argent.client.mixin;

import com.khrux.argent.Argent;
import com.khrux.argent.client.ArgentClient;
import com.khrux.argent.client.renderer.MirrorReflections;
import com.khrux.argent.world.item.ArgentItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.entity.decoration.ItemFrame;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemFrameRenderer.class)
public abstract class ItemFrameRendererMixin {
	@Unique
	private static final RenderStateDataKey<Boolean> MIRROR_TILE = RenderStateDataKey.create(() -> "argent:mirror_tile");
	@Unique
	private static final Identifier TILE_TEXTURE = Argent.id("textures/block/mirror_glass_tile.png");
	@Unique
	private static final int REFLECTION_COLOR = ARGB.color(0xAA, 0xFFFFFF);
	@Unique
	private static final float REFLECTION_Z = -0.015F;
	@Unique
	private static final int REFLECTION_CELLS = 4;
	@Shadow
	@Final
	private BlockModelResolver blockModelResolver;

	@Inject(
		method = "extractRenderState(Lnet/minecraft/world/entity/decoration/ItemFrame;Lnet/minecraft/client/renderer/entity/state/ItemFrameRenderState;F)V",
		at = @At("TAIL")
	)
	private void mirror$extractMirrorTile(final ItemFrame entity, final ItemFrameRenderState state, final float partialTicks, final CallbackInfo ci) {
		boolean tile = state.mapId == null && entity.getItem().is(ArgentItems.MIRROR);
		((FabricRenderState)state).setData(MIRROR_TILE, tile);
		if (!tile) {
			return;
		}

		state.item.clear();
		if (!state.isInvisible) {
			this.blockModelResolver.updateForItemFrame(state.frameModel, state.isGlowFrame, true);
		}
	}

	@Inject(
		method = "submit(Lnet/minecraft/client/renderer/entity/state/ItemFrameRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
		at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V", ordinal = 1)
	)
	private void mirror$submitMirrorTile(
		final ItemFrameRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera, final CallbackInfo ci
	) {
		if (!((FabricRenderState)state).getDataOrDefault(MIRROR_TILE, false)) {
			return;
		}

		poseStack.rotateDegrees(Axis.ZP, 180.0F);
		poseStack.translate(-0.5F, -0.5F, -0.0078125F);
		int lightCoords = state.lightCoords;
		submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.text(TILE_TEXTURE), (pose, buffer) -> {
			buffer.addVertex(pose, 0.0F, 1.0F, -0.0001F).setColor(-1).setUv(0.0F, 1.0F).setLight(lightCoords);
			buffer.addVertex(pose, 1.0F, 1.0F, -0.0001F).setColor(-1).setUv(1.0F, 1.0F).setLight(lightCoords);
			buffer.addVertex(pose, 1.0F, 0.0F, -0.0001F).setColor(-1).setUv(1.0F, 0.0F).setLight(lightCoords);
			buffer.addVertex(pose, 0.0F, 0.0F, -0.0001F).setColor(-1).setUv(0.0F, 0.0F).setLight(lightCoords);
		});
		MirrorReflections reflections = ArgentClient.reflections();
		Matrix4f surface = new Matrix4f(poseStack.last().pose());
		Identifier reflection = reflections.request(null, surface, 0.0F, 0.0F, 1.0F, 1.0F, REFLECTION_Z);
		if (reflection == null) {
			return;
		}

		submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.text(reflection), (pose, buffer) -> {
			float cell = 1.0F / REFLECTION_CELLS;
			for (int column = 0; column < REFLECTION_CELLS; column++) {
				for (int row = 0; row < REFLECTION_CELLS; row++) {
					float x0 = column * cell;
					float y0 = row * cell;
					mirror$addReflectionVertex(buffer, pose, reflections, surface, x0, y0 + cell);
					mirror$addReflectionVertex(buffer, pose, reflections, surface, x0 + cell, y0 + cell);
					mirror$addReflectionVertex(buffer, pose, reflections, surface, x0 + cell, y0);
					mirror$addReflectionVertex(buffer, pose, reflections, surface, x0, y0);
				}
			}
		});
	}

	@Unique
	private static void mirror$addReflectionVertex(
		final VertexConsumer buffer, final PoseStack.Pose pose, final MirrorReflections reflections, final Matrix4f surface, final float x, final float y
	) {
		Vector2f uv = reflections.textureCoordinates(surface, x, y, REFLECTION_Z);
		buffer.addVertex(pose, x, y, REFLECTION_Z).setColor(REFLECTION_COLOR).setUv(uv.x, uv.y).setLight(LightCoordsUtil.FULL_BRIGHT);
	}
}
