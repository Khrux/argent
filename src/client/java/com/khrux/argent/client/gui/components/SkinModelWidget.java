package com.khrux.argent.client.gui.components;

import com.khrux.argent.client.skin.SkinFace;
import com.khrux.argent.client.skin.SkinPart;
import com.khrux.argent.client.skin.SkinPreset;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.PlayerModelType;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

public class SkinModelWidget extends AbstractWidget {
	private static final int SKIN_SIZE = 64;
	private static final float MODEL_HEIGHT = 2.125F;
	private static final float FIT_SCALE = 0.97F;
	private static final float PIVOT_Y = -1.0625F;
	private static final float ROTATION_SENSITIVITY = 2.5F;
	private static final float DEFAULT_ROTATION_X = -5.0F;
	private static final float DEFAULT_ROTATION_Y = 30.0F;
	private static final float ROTATION_X_LIMIT = 50.0F;
	private static final float MODEL_OFFSET_Y = -1.6010001F;
	private static final float RAY_START_Z = 1000.0F;
	private static final float PIXELS_PER_UNIT = 16.0F;
	private static final double CLICK_DRAG_LIMIT = 2.0;
	private final Model.Simple wideModel;
	private final Model.Simple slimModel;
	private final Supplier<SkinPreset> preset;
	private final Set<SkinPart> hiddenParts;
	private final Consumer<SkinFace> onFacePicked;
	private double dragDistance;
	private float pickDistance;
	private @Nullable SkinFace pickedFace;
	private float rotationX = DEFAULT_ROTATION_X;
	private float rotationY = DEFAULT_ROTATION_Y;

	public SkinModelWidget(
		final int width,
		final int height,
		final Supplier<SkinPreset> preset,
		final Set<SkinPart> hiddenParts,
		final Consumer<SkinFace> onFacePicked
	) {
		super(0, 0, width, height, CommonComponents.EMPTY);
		this.wideModel = new Model.Simple(bakePlayerModel(false), RenderTypes::entityTranslucent);
		this.slimModel = new Model.Simple(bakePlayerModel(true), RenderTypes::entityTranslucent);
		this.preset = preset;
		this.hiddenParts = hiddenParts;
		this.onFacePicked = onFacePicked;
	}

	private static ModelPart bakePlayerModel(final boolean slim) {
		return LayerDefinition.create(PlayerModel.createMesh(CubeDeformation.NONE, slim), SKIN_SIZE, SKIN_SIZE).bakeRoot();
	}

	public void resetView() {
		this.rotationX = DEFAULT_ROTATION_X;
		this.rotationY = DEFAULT_ROTATION_Y;
	}

	@Override
	protected void extractWidgetRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
		SkinPreset preset = this.preset.get();
		Model.Simple model = preset.model() == PlayerModelType.SLIM ? this.slimModel : this.wideModel;
		this.updateVisibility(model.root());
		float scale = FIT_SCALE * this.getHeight() / MODEL_HEIGHT;
		graphics.skin(model, preset.textureLocation(), scale, this.rotationX, this.rotationY, PIVOT_Y, this.getX(), this.getY(), this.getRight(), this.getBottom());
	}

	private void updateVisibility(final ModelPart root) {
		for (SkinPart part : SkinPart.values()) {
			ModelPart modelPart = part.modelPart(root);
			boolean hidden = this.hiddenParts.contains(part);
			if (part.isOverlay()) {
				modelPart.visible = !hidden;
			} else {
				modelPart.skipDraw = hidden;
			}
		}
	}

	@Override
	public void onClick(final MouseButtonEvent event, final boolean doubleClick) {
		this.dragDistance = 0.0;
	}

	@Override
	public void onRelease(final MouseButtonEvent event) {
		if (this.dragDistance >= CLICK_DRAG_LIMIT) {
			return;
		}

		SkinFace face = this.pickFace(event.x(), event.y());
		if (face != null) {
			this.onFacePicked.accept(face);
		}
	}

	private @Nullable SkinFace pickFace(final double mouseX, final double mouseY) {
		Model.Simple model = this.preset.get().model() == PlayerModelType.SLIM ? this.slimModel : this.wideModel;
		float scale = FIT_SCALE * this.getHeight() / MODEL_HEIGHT;
		Matrix4f view = new Matrix4f().rotateAround(Axis.XP.rotationDegrees(this.rotationX), 0.0F, scale * -PIVOT_Y, 0.0F);
		PoseStack poseStack = new PoseStack();
		poseStack.translate(this.getWidth() / 2.0F, this.getHeight(), 0.0F);
		poseStack.scale(scale, scale, -scale);
		poseStack.rotateDegrees(Axis.YP, -this.rotationY);
		poseStack.translate(0.0F, MODEL_OFFSET_Y, 0.0F);
		Vector3f origin = new Vector3f((float)(mouseX - this.getX()), (float)(mouseY - this.getY()), RAY_START_Z);
		this.pickDistance = Float.MAX_VALUE;
		this.pickedFace = null;
		model.root().visit(poseStack, (pose, path, cubeIndex, cube) -> this.testCube(view, pose, path, cube, origin));
		return this.pickedFace;
	}

	private void testCube(final Matrix4f view, final PoseStack.Pose pose, final String path, final ModelPart.Cube cube, final Vector3f origin) {
		SkinPart part = SkinPart.byName(path.substring(path.lastIndexOf('/') + 1));
		if (part == null || this.hiddenParts.contains(part)) {
			return;
		}

		Matrix4f inverse = new Matrix4f(view).mul(pose.pose()).invert();
		Vector3f start = inverse.transformPosition(origin, new Vector3f());
		Vector3f direction = inverse.transformDirection(new Vector3f(0.0F, 0.0F, -1.0F));
		float grow = part.grow();
		Vector3f min = new Vector3f(cube.minX - grow, cube.minY - grow, cube.minZ - grow).div(PIXELS_PER_UNIT);
		Vector3f max = new Vector3f(cube.maxX + grow, cube.maxY + grow, cube.maxZ + grow).div(PIXELS_PER_UNIT);
		float enter = -Float.MAX_VALUE;
		float exit = Float.MAX_VALUE;
		Direction side = null;
		for (Direction.Axis axis : Direction.Axis.values()) {
			int component = axis.ordinal();
			float from = start.get(component);
			float step = direction.get(component);
			float low = min.get(component);
			float high = max.get(component);
			if (Math.abs(step) < Mth.EPSILON) {
				if (from < low || from > high) {
					return;
				}

				continue;
			}

			float near = ((step > 0.0F ? low : high) - from) / step;
			float far = ((step > 0.0F ? high : low) - from) / step;
			if (near > enter) {
				enter = near;
				side = Direction.fromAxisAndDirection(axis, step > 0.0F ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE);
			}

			exit = Math.min(exit, far);
		}

		if (side == null || enter > exit || exit < 0.0F || enter >= this.pickDistance) {
			return;
		}

		this.pickDistance = enter;
		this.pickedFace = new SkinFace(part, side);
	}

	@Override
	protected void onDrag(final MouseButtonEvent event, final double dx, final double dy) {
		this.dragDistance += Math.abs(dx) + Math.abs(dy);
		this.rotationX = Mth.clamp(this.rotationX - (float)dy * ROTATION_SENSITIVITY, -ROTATION_X_LIMIT, ROTATION_X_LIMIT);
		this.rotationY += (float)dx * ROTATION_SENSITIVITY;
	}

	@Override
	public void playDownSound(final SoundManager soundManager) {
	}

	@Override
	protected void updateWidgetNarration(final NarrationElementOutput output) {
	}

	@Override
	public @Nullable ComponentPath nextFocusPath(final FocusNavigationEvent navigationEvent) {
		return null;
	}
}
