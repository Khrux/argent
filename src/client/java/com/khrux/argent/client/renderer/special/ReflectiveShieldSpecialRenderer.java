package com.khrux.argent.client.renderer.special;

import com.khrux.argent.Argent;
import com.khrux.argent.client.ArgentClient;
import com.khrux.argent.client.renderer.MirrorReflections;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Consumer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.equipment.ShieldModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public class ReflectiveShieldSpecialRenderer implements SpecialModelRenderer<ItemStack> {
	private static final Identifier GLASS_TOP_LOCATION = Argent.id("textures/block/mirror_glass_top.png");
	private static final Identifier GLASS_BOTTOM_LOCATION = Argent.id("textures/block/mirror_glass_bottom.png");
	private static final int REFLECTION_COLOR = ARGB.color(0xAA, 0xFFFFFF);
	private static final float PIXEL = 1.0F / 16.0F;
	private static final float GLASS_LEFT = -5.0F;
	private static final float GLASS_RIGHT = 5.0F;
	private static final float GLASS_TOP = -10.0F;
	private static final float GLASS_MIDDLE = 0.0F;
	private static final float GLASS_BOTTOM = 10.0F;
	private static final float GLASS_Z = -2.1F;
	private static final float REFLECTION_Z = -2.3F;
	private static final float GLASS_U0 = 3.0F;
	private static final float GLASS_U1 = 13.0F;
	private static final float GLASS_V0 = 3.0F;
	private static final float GLASS_V1 = 13.0F;
	private static final int COLUMNS = 4;
	private static final int ROWS = 8;
	private final SpriteGetter sprites;
	private final ShieldModel model;
	private final boolean reflective;

	public ReflectiveShieldSpecialRenderer(final SpriteGetter sprites, final ShieldModel model, final boolean reflective) {
		this.sprites = sprites;
		this.model = model;
		this.reflective = reflective;
	}

	public static void bootstrap() {
		SpecialModelRenderers.ID_MAPPER.put(Argent.id("reflective_shield"), Unbaked.MAP_CODEC);
	}

	@Override
	public ItemStack extractArgument(final ItemStack stack) {
		return stack;
	}

	@Override
	public void submit(
		final @Nullable ItemStack stack,
		final PoseStack poseStack,
		final SubmitNodeCollector submitNodeCollector,
		final int lightCoords,
		final int overlayCoords,
		final boolean hasFoil,
		final int outlineColor
	) {
		SpriteId base = Sheets.SHIELD_BASE_NO_PATTERN;
		if (hasFoil) {
			submitNodeCollector.submitModel(
				this.model,
				Unit.INSTANCE,
				poseStack,
				RenderTypes.entitySolidGlint(base.atlasLocation()),
				lightCoords,
				overlayCoords,
				-1,
				this.sprites.get(base),
				outlineColor
			);
		} else {
			submitNodeCollector.submitModel(this.model, Unit.INSTANCE, poseStack, lightCoords, overlayCoords, -1, base, this.sprites, outlineColor);
		}

		submitGlass(poseStack, submitNodeCollector, GLASS_TOP_LOCATION, GLASS_TOP, GLASS_MIDDLE, lightCoords);
		submitGlass(poseStack, submitNodeCollector, GLASS_BOTTOM_LOCATION, GLASS_MIDDLE, GLASS_BOTTOM, lightCoords);
		if (!this.reflective) {
			return;
		}

		MirrorReflections reflections = ArgentClient.reflections();
		Matrix4f surface = new Matrix4f(poseStack.last().pose());
		Identifier reflection = reflections.request(
			stack,
			surface, GLASS_LEFT * PIXEL, GLASS_BOTTOM * PIXEL, GLASS_RIGHT * PIXEL, GLASS_TOP * PIXEL, REFLECTION_Z * PIXEL
		);
		if (reflection == null) {
			return;
		}

		submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.text(reflection), (pose, buffer) -> {
			float columnWidth = (GLASS_RIGHT - GLASS_LEFT) / COLUMNS;
			float rowHeight = (GLASS_BOTTOM - GLASS_TOP) / ROWS;
			for (int column = 0; column < COLUMNS; column++) {
				for (int row = 0; row < ROWS; row++) {
					float left = GLASS_LEFT + column * columnWidth;
					float top = GLASS_TOP + row * rowHeight;
					addReflectionVertex(buffer, pose, reflections, surface, left, top + rowHeight);
					addReflectionVertex(buffer, pose, reflections, surface, left + columnWidth, top + rowHeight);
					addReflectionVertex(buffer, pose, reflections, surface, left + columnWidth, top);
					addReflectionVertex(buffer, pose, reflections, surface, left, top);
				}
			}
		});
	}

	private static void addReflectionVertex(
		final VertexConsumer buffer, final PoseStack.Pose pose, final MirrorReflections reflections, final Matrix4f surface, final float x, final float y
	) {
		Vector2f uv = reflections.textureCoordinates(surface, x * PIXEL, y * PIXEL, REFLECTION_Z * PIXEL);
		buffer.addVertex(pose, x * PIXEL, y * PIXEL, REFLECTION_Z * PIXEL).setColor(REFLECTION_COLOR).setUv(uv.x, uv.y).setLight(LightCoordsUtil.FULL_BRIGHT);
	}

	private static void submitGlass(
		final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final Identifier texture, final float top, final float bottom, final int lightCoords
	) {
		submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.text(texture), (pose, buffer) -> {
			buffer.addVertex(pose, GLASS_LEFT * PIXEL, bottom * PIXEL, GLASS_Z * PIXEL).setColor(-1).setUv(GLASS_U0 * PIXEL, GLASS_V1 * PIXEL).setLight(lightCoords);
			buffer.addVertex(pose, GLASS_RIGHT * PIXEL, bottom * PIXEL, GLASS_Z * PIXEL).setColor(-1).setUv(GLASS_U1 * PIXEL, GLASS_V1 * PIXEL).setLight(lightCoords);
			buffer.addVertex(pose, GLASS_RIGHT * PIXEL, top * PIXEL, GLASS_Z * PIXEL).setColor(-1).setUv(GLASS_U1 * PIXEL, GLASS_V0 * PIXEL).setLight(lightCoords);
			buffer.addVertex(pose, GLASS_LEFT * PIXEL, top * PIXEL, GLASS_Z * PIXEL).setColor(-1).setUv(GLASS_U0 * PIXEL, GLASS_V0 * PIXEL).setLight(lightCoords);
		});
	}

	@Override
	public void getExtents(final Consumer<Vector3fc> output) {
		this.model.root().getExtentsForGui(new PoseStack(), output);
	}

	public record Unbaked(boolean reflective) implements SpecialModelRenderer.Unbaked<ItemStack> {
		public static final MapCodec<Unbaked> MAP_CODEC = Codec.BOOL.optionalFieldOf("reflective", false).xmap(Unbaked::new, Unbaked::reflective);

		@Override
		public MapCodec<Unbaked> type() {
			return MAP_CODEC;
		}

		@Override
		public ReflectiveShieldSpecialRenderer bake(final SpecialModelRenderer.BakingContext context) {
			return new ReflectiveShieldSpecialRenderer(context.sprites(), new ShieldModel(context.entityModelSet().bakeLayer(ModelLayers.SHIELD)), this.reflective);
		}
	}
}
