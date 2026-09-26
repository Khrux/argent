package com.khrux.argent.client.renderer;

import com.khrux.argent.Argent;
import com.khrux.argent.client.renderer.block.ReflectedBlockMesh;
import com.khrux.argent.client.renderer.texture.ReflectionTexture;
import com.khrux.argent.config.ArgentConfig;
import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.commands.CommandEncoder;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.textures.GpuTexture;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.dimension.DimensionType.Skybox;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.jspecify.annotations.Nullable;

public class MirrorReflections {
	private static final int MAX_REFLECTIONS = 4;
	private static final int UNUSED_FRAMES_KEPT = 600;
	private static final double ENTITY_RANGE = 32.0;
	private static final int RESOLUTION_DIVISOR = 2;
	private static final float SAME_PLANE_NORMAL = 0.9995F;
	private static final double SAME_PLANE_DISTANCE = 0.05;
	private static final Vector4fc CLEAR_COLOR = new Vector4f(0.72F, 0.75F, 0.78F, 0.0F);
	private static final double BLOCK_RANGE = 32.0;
	private static final int MAX_BLOCKS = 4096;
	private static final int SCAN_BUDGET = 16384;
	private static final float BLOCK_RADIUS = 0.87F;
	private final List<Reflection> reflections = new ArrayList<>();
	private final Map<Object, Reflection> keyedReflections = new IdentityHashMap<>();
	private final ProjectionMatrixBuffer projectionMatrixBuffer = new ProjectionMatrixBuffer("Mirror reflection");
	private final Matrix4f viewProjection = new Matrix4f();
	private final Matrix4f projection = new Matrix4f();
	private final FogRenderer fogRenderer = new FogRenderer();
	private final RenderBuffers renderBuffers = new RenderBuffers(0);
	private @Nullable FeatureRenderDispatcher featureRenderDispatcher;
	private @Nullable LevelRenderState levelState;
	private @Nullable TextureTarget skyTarget;
	private @Nullable SkyRenderer skyRenderer;
	private Vec3 cameraPosition = Vec3.ZERO;
	private boolean renderingReflection;
	private int nextId;

	public @Nullable Identifier request(
		final @Nullable Object key, final Matrix4fc pose, final float left, final float bottom, final float right, final float top, final float z
	) {
		if (this.renderingReflection) {
			return null;
		}

		Vector3f normal = pose.transformDirection(new Vector3f(0.0F, 0.0F, -1.0F)).normalize();
		Vec3 point = this.worldPosition(pose, (left + right) / 2.0F, (bottom + top) / 2.0F, z);
		AABB bounds = new AABB(this.worldPosition(pose, left, bottom, z), this.worldPosition(pose, right, top, z))
			.minmax(new AABB(this.worldPosition(pose, left, top, z), this.worldPosition(pose, right, bottom, z)));
		double distance = normal.x * point.x + normal.y * point.y + normal.z * point.z;
		Reflection reflection = key != null ? this.keyedReflections.get(key) : this.find(normal, point);
		if (reflection == null) {
			reflection = new Reflection(Argent.id("reflection/" + this.nextId++), key);
			this.reflections.add(reflection);
			if (key != null) {
				this.keyedReflections.put(key, reflection);
			}
		}

		reflection.request(normal, distance, point, bounds, this.cameraPosition);
		return reflection.texture != null ? reflection.location : null;
	}

	private @Nullable Reflection find(final Vector3f normal, final Vec3 point) {
		Reflection closest = null;
		double closestDifference = Double.MAX_VALUE;
		for (Reflection reflection : this.reflections) {
			if (reflection.key != null) {
				continue;
			}

			float alignment = reflection.normal.dot(normal);
			double offset = Math.abs(reflection.normal.x * point.x + reflection.normal.y * point.y + reflection.normal.z * point.z - reflection.distance);
			if (alignment < SAME_PLANE_NORMAL || offset > SAME_PLANE_DISTANCE) {
				continue;
			}

			double difference = offset + (1.0 - alignment);
			if (difference < closestDifference) {
				closest = reflection;
				closestDifference = difference;
			}
		}

		return closest;
	}

	private Vec3 worldPosition(final Matrix4fc pose, final float x, final float y, final float z) {
		Vector3f relative = pose.transformPosition(new Vector3f(x, y, z));
		return this.cameraPosition.add(relative.x, relative.y, relative.z);
	}

	public Vector2f textureCoordinates(final Matrix4fc pose, final float x, final float y, final float z) {
		Vector3f relative = pose.transformPosition(new Vector3f(x, y, z));
		Vector4f clip = this.viewProjection.transform(new Vector4f(relative, 1.0F));
		return new Vector2f(0.5F - clip.x / clip.w * 0.5F, 0.5F + clip.y / clip.w * 0.5F);
	}

	public void extract(final LevelExtractionContext context) {
		if (ArgentConfig.get().reflections == ArgentConfig.Reflections.OFF) {
			this.clear();
			return;
		}

		this.levelState = context.levelState();
		CameraRenderState camera = context.levelState().cameraRenderState;
		this.cameraPosition = camera.pos;
		this.viewProjection.set(camera.projectionMatrix).mul(camera.viewRotationMatrix);
		this.projection.set(camera.projectionMatrix);
		List<Reflection> requested = new ArrayList<>();
		for (Reflection reflection : this.reflections) {
			if (reflection.requested) {
				requested.add(reflection);
			}
		}

		requested.sort(Comparator.comparingDouble(reflection -> reflection.point.distanceToSqr(camera.pos)));
		int rendered = 0;
		TextureManager textureManager = Minecraft.getInstance().getTextureManager();
		for (Reflection reflection : requested) {
			if (rendered < MAX_REFLECTIONS && this.extractReflection(context, camera, reflection)) {
				rendered++;
			} else {
				reflection.release(textureManager);
			}
		}

		this.reflections.removeIf(reflection -> {
			if (reflection.requested) {
				reflection.requested = false;
				reflection.unusedFrames = 0;
				return false;
			}

			if (++reflection.unusedFrames < UNUSED_FRAMES_KEPT) {
				return false;
			}

			reflection.release(textureManager);
			if (reflection.key != null) {
				this.keyedReflections.remove(reflection.key);
			}

			return true;
		});
	}

	private boolean extractReflection(final LevelExtractionContext context, final CameraRenderState camera, final Reflection reflection) {
		Vector3f normal = reflection.normal;
		Vec3 normalVector = new Vec3(normal);
		double distance = camera.pos.subtract(reflection.point).dot(normalVector);
		if (distance <= 0.0) {
			return false;
		}

		Vec3 reflectedPosition = camera.pos.subtract(normalVector.scale(2.0 * distance));
		Matrix4f view = new Matrix4f().scaling(-1.0F, 1.0F, 1.0F).mul(camera.viewRotationMatrix).reflect(normal.x, normal.y, normal.z, 0.0F);
		CameraRenderState reflectedCamera = new CameraRenderState();
		reflectedCamera.initialized = true;
		reflectedCamera.pos = reflectedPosition;
		reflectedCamera.orientation = new Quaternionf().setFromNormalized(new Matrix3f(view).transpose());
		reflectedCamera.viewRotationMatrix = view;
		reflectedCamera.projectionMatrix = camera.projectionMatrix;
		reflectedCamera.fogData = camera.fogData;
		reflectedCamera.depthFar = camera.depthFar;
		this.renderingReflection = true;
		this.submitEntities(context, reflectedCamera, reflection, normalVector);
		if (ArgentConfig.get().reflections == ArgentConfig.Reflections.ALL) {
			this.scanBlocks(context.level(), reflection);
			reflection.mesh.startFrame();
			this.submitBlocks(context.level(), reflectedCamera, reflection, normalVector);
		}

		this.renderingReflection = false;
		reflection.view.set(view);
		reflection.clearColor.set(clearColor(camera));
		reflection.extracted = true;
		return true;
	}

	private void submitEntities(final LevelExtractionContext context, final CameraRenderState reflectedCamera, final Reflection reflection, final Vec3 normal) {
		EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
		float partialTicks = context.deltaTracker().getGameTimeDeltaPartialTick(false);
		PoseStack poseStack = new PoseStack();
		Vec3 origin = reflectedCamera.pos;
		for (Entity entity : context.level().entitiesForRendering()) {
			Vec3 position = entity.position();
			if (position.distanceToSqr(reflection.point) > ENTITY_RANGE * ENTITY_RANGE || position.subtract(reflection.point).dot(normal) <= 0.0) {
				continue;
			}

			EntityRenderState state = dispatcher.extractEntity(entity, partialTicks);
			state.shadowPieces.clear();
			dispatcher.submit(state, reflectedCamera, state.x - origin.x, state.y - origin.y, state.z - origin.z, poseStack, reflection.submitNodeStorage);
		}
	}

	private void scanBlocks(final ClientLevel level, final Reflection reflection) {
		if (reflection.scan == null) {
			AABB region = reflection.bounds.inflate(BLOCK_RANGE);
			reflection.scan = BlockPos.betweenClosed(region).iterator();
			reflection.scanned = new ArrayList<>();
		}

		for (int i = 0; i < SCAN_BUDGET && reflection.scan.hasNext(); i++) {
			BlockPos pos = reflection.scan.next();
			if (level.getBlockState(pos).getRenderShape() == RenderShape.MODEL && !isHidden(level, pos)) {
				reflection.scanned.add(pos.immutable());
			}
		}

		if (!reflection.scan.hasNext()) {
			reflection.blocks = reflection.scanned;
			reflection.mesh.retain(reflection.blocks);
			reflection.scan = null;
		}
	}

	private void submitBlocks(final ClientLevel level, final CameraRenderState reflectedCamera, final Reflection reflection, final Vec3 normal) {
		Vec3 origin = reflectedCamera.pos;
		Matrix4f viewProjection = new Matrix4f(reflectedCamera.projectionMatrix).mul(reflectedCamera.viewRotationMatrix);
		float minX = Float.MAX_VALUE;
		float minY = Float.MAX_VALUE;
		float maxX = -Float.MAX_VALUE;
		float maxY = -Float.MAX_VALUE;
		for (Vec3 corner : corners(reflection.bounds)) {
			Vector4f clip = project(viewProjection, corner, origin);
			if (clip.w <= 0.0F) {
				minX = -Float.MAX_VALUE;
				minY = -Float.MAX_VALUE;
				maxX = Float.MAX_VALUE;
				maxY = Float.MAX_VALUE;
				break;
			}

			minX = Math.min(minX, clip.x / clip.w);
			minY = Math.min(minY, clip.y / clip.w);
			maxX = Math.max(maxX, clip.x / clip.w);
			maxY = Math.max(maxY, clip.y / clip.w);
		}

		List<BlockPos> visible = new ArrayList<>();
		for (BlockPos pos : reflection.blocks) {
			Vec3 centre = Vec3.atCenterOf(pos);
			if (centre.subtract(reflection.point).dot(normal) <= 0.0) {
				continue;
			}

			Vector4f clip = project(viewProjection, centre, origin);
			if (clip.w <= 0.0F) {
				continue;
			}

			float marginX = BLOCK_RADIUS * reflectedCamera.projectionMatrix.m00() / clip.w;
			float marginY = BLOCK_RADIUS * reflectedCamera.projectionMatrix.m11() / clip.w;
			float x = clip.x / clip.w;
			float y = clip.y / clip.w;
			if (x + marginX >= minX && x - marginX <= maxX && y + marginY >= minY && y - marginY <= maxY) {
				visible.add(pos);
			}
		}

		visible.sort(Comparator.comparingDouble(pos -> pos.distToCenterSqr(origin)));
		List<BlockPos> positions = new ArrayList<>();
		List<List<ReflectedBlockMesh.Quad>> meshes = new ArrayList<>();
		for (BlockPos pos : visible.subList(0, Math.min(visible.size(), MAX_BLOCKS))) {
			List<ReflectedBlockMesh.Quad> quads = reflection.mesh.quads(level, pos);
			if (quads != null && !quads.isEmpty()) {
				positions.add(pos);
				meshes.add(quads);
			}
		}

		PoseStack poseStack = new PoseStack();
		for (ChunkSectionLayer layer : ChunkSectionLayer.values()) {
			reflection.submitNodeStorage.submitCustomGeometry(poseStack, renderType(layer), (pose, buffer) -> {
				for (int i = 0; i < positions.size(); i++) {
					PoseStack.Pose blockPose = null;
					for (ReflectedBlockMesh.Quad quad : meshes.get(i)) {
						if (quad.layer() != layer) {
							continue;
						}

						if (blockPose == null) {
							BlockPos pos = positions.get(i);
							blockPose = pose.copy();
							blockPose.translate((float)(pos.getX() - origin.x), (float)(pos.getY() - origin.y), (float)(pos.getZ() - origin.z));
						}

						buffer.putBakedQuad(blockPose, quad.quad(), quad.instance());
					}
				}
			});
		}
	}

	private static RenderType renderType(final ChunkSectionLayer layer) {
		return switch (layer) {
			case SOLID -> RenderTypes.solidMovingBlock();
			case CUTOUT -> RenderTypes.cutoutMovingBlock();
			case TRANSLUCENT -> RenderTypes.translucentMovingBlock();
		};
	}


	private static boolean isHidden(final ClientLevel level, final BlockPos pos) {
		for (Direction direction : Direction.values()) {
			if (!level.getBlockState(pos.relative(direction)).isSolidRender()) {
				return false;
			}
		}

		return true;
	}

	private static Vector4f project(final Matrix4f viewProjection, final Vec3 position, final Vec3 origin) {
		return viewProjection.transform(
			new Vector4f((float)(position.x - origin.x), (float)(position.y - origin.y), (float)(position.z - origin.z), 1.0F)
		);
	}

	private static List<Vec3> corners(final AABB box) {
		List<Vec3> corners = new ArrayList<>(8);
		for (int i = 0; i < 8; i++) {
			corners.add(new Vec3((i & 1) == 0 ? box.minX : box.maxX, (i & 2) == 0 ? box.minY : box.maxY, (i & 4) == 0 ? box.minZ : box.maxZ));
		}

		return corners;
	}

	public void draw(final GameRenderer gameRenderer) {
		LevelRenderState levelState = this.levelState;
		if (levelState == null) {
			return;
		}

		this.levelState = null;
		if (levelState.shouldResetSkyRenderer && this.skyRenderer != null) {
			this.skyRenderer.close();
			this.skyRenderer = null;
		}

		GpuBufferSlice previousFog = RenderSystem.getShaderFog();
		this.fogRenderer.updateBuffer(levelState.cameraRenderState.fogData);
		GpuBufferSlice fog = this.fogRenderer.getBuffer(FogRenderer.FogMode.WORLD);
		RenderSystem.setShaderFog(fog);
		for (Reflection reflection : this.reflections) {
			if (reflection.extracted) {
				reflection.extracted = false;
				this.draw(gameRenderer, levelState, fog, reflection);
			}
		}

		this.fogRenderer.endFrame();
		this.renderBuffers.endFrame();
		if (previousFog != null) {
			RenderSystem.setShaderFog(previousFog);
		}
	}

	private void draw(final GameRenderer gameRenderer, final LevelRenderState levelState, final GpuBufferSlice fog, final Reflection reflection) {
		if (reflection.texture == null) {
			reflection.texture = new ReflectionTexture();
			Minecraft.getInstance().getTextureManager().register(reflection.location, reflection.texture);
		}

		RenderTarget mainTarget = gameRenderer.mainRenderTarget();
		reflection.texture.resize(Math.max(1, mainTarget.width / RESOLUTION_DIVISOR), Math.max(1, mainTarget.height / RESOLUTION_DIVISOR));
		Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
		modelViewStack.pushMatrix();
		modelViewStack.set(reflection.view);
		RenderSystem.setProjectionMatrix(this.projectionMatrixBuffer.getBuffer(this.projection), ProjectionType.PERSPECTIVE);
		gameRenderer.lighting().setupFor(Lighting.Entry.LEVEL);
		boolean sky = ArgentConfig.get().reflections == ArgentConfig.Reflections.ALL && this.drawSky(levelState, fog, reflection.texture, reflection.clearColor);
		try (
			FeatureRenderDispatcher.PreparedFrame frame = this.featureRenderDispatcher(gameRenderer).prepareFrame(reflection.submitNodeStorage);
			RenderPass renderPass = RenderSystem.getDevice()
				.createCommandEncoder()
				.createRenderPass(
					() -> "Mirror reflection",
					reflection.texture.getTextureView(),
					sky ? Optional.empty() : Optional.of(reflection.clearColor),
					reflection.texture.getDepthTextureView(),
					OptionalDouble.of(0.0)
				);
		) {
			RenderSystem.bindDefaultUniforms(renderPass);
			FeatureRenderDispatcher.renderAllFeatures(renderPass, frame);
		}

		modelViewStack.popMatrix();
	}

	private FeatureRenderDispatcher featureRenderDispatcher(final GameRenderer gameRenderer) {
		if (this.featureRenderDispatcher == null) {
			Minecraft minecraft = Minecraft.getInstance();
			this.featureRenderDispatcher = new FeatureRenderDispatcher(
				this.renderBuffers, minecraft.getModelManager(), minecraft.getAtlasManager(), minecraft.font, gameRenderer.gameRenderState()
			);
		}

		return this.featureRenderDispatcher;
	}

	private boolean drawSky(final LevelRenderState levelState, final GpuBufferSlice fog, final ReflectionTexture reflectionTexture, final Vector4fc clearColor) {
		CameraRenderState camera = levelState.cameraRenderState;
		if (camera.fogType == FogType.POWDER_SNOW
			|| camera.fogType == FogType.LAVA
			|| camera.entityRenderState.doesMobEffectBlockSky
			|| levelState.skyRenderState.skybox == Skybox.NONE) {
			return false;
		}

		GpuTexture texture = reflectionTexture.getTexture();
		int width = texture.getWidth(0);
		int height = texture.getHeight(0);
		if (this.skyTarget == null) {
			this.skyTarget = new TextureTarget("Mirror reflection sky", width, height, GpuFormat.RGBA8_UNORM, GpuFormat.D32_FLOAT);
		} else if (this.skyTarget.width != width || this.skyTarget.height != height) {
			this.skyTarget.resize(width, height);
		}

		if (this.skyRenderer == null) {
			Minecraft minecraft = Minecraft.getInstance();
			this.skyRenderer = new SkyRenderer(minecraft.getTextureManager(), minecraft.getAtlasManager(), this.skyTarget);
		}

		CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
		encoder.clearColorAndDepthTextures(this.skyTarget.getColorTexture(), clearColor, this.skyTarget.getDepthTexture(), 0.0);
		this.skyRenderer.render(fog, levelState.skyRenderState);
		encoder.copyTextureToTexture(this.skyTarget.getColorTexture(), texture, 0, 0, 0, 0, 0, width, height);
		return true;
	}

	private static Vector4fc clearColor(final CameraRenderState camera) {
		if (ArgentConfig.get().reflections != ArgentConfig.Reflections.ALL) {
			return CLEAR_COLOR;
		}

		Vector4f fog = camera.fogData.color;
		return new Vector4f(fog.x, fog.y, fog.z, 1.0F);
	}

	public void clear() {
		TextureManager textureManager = Minecraft.getInstance().getTextureManager();
		for (Reflection reflection : this.reflections) {
			reflection.release(textureManager);
		}

		this.reflections.clear();
	}

	private static class Reflection {
		private final Identifier location;
		private final @Nullable Object key;
		private final ReflectedBlockMesh mesh = new ReflectedBlockMesh();
		private final SubmitNodeStorage submitNodeStorage = new SubmitNodeStorage();
		private final Matrix4f view = new Matrix4f();
		private final Vector4f clearColor = new Vector4f();
		private boolean extracted;
		private Vector3f normal = new Vector3f();
		private double distance;
		private Vec3 point = Vec3.ZERO;
		private AABB bounds = new AABB(BlockPos.ZERO);
		private boolean requested;
		private int unusedFrames;
		private @Nullable ReflectionTexture texture;
		private List<BlockPos> blocks = List.of();
		private List<BlockPos> scanned = new ArrayList<>();
		private @Nullable Iterator<BlockPos> scan;

		private Reflection(final Identifier location, final @Nullable Object key) {
			this.location = location;
			this.key = key;
		}

		private void request(final Vector3f normal, final double distance, final Vec3 point, final AABB bounds, final Vec3 cameraPosition) {
			if (!this.requested) {
				this.requested = true;
				this.normal = normal;
				this.distance = distance;
				this.point = point;
				this.bounds = bounds;
				return;
			}

			this.bounds = this.bounds.minmax(bounds);
			if (point.distanceToSqr(cameraPosition) < this.point.distanceToSqr(cameraPosition)) {
				this.point = point;
			}
		}

		private void release(final TextureManager textureManager) {
			if (this.texture != null) {
				textureManager.release(this.location);
				this.texture = null;
			}
		}
	}
}
