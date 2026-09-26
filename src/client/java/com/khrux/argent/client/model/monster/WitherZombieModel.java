package com.khrux.argent.client.model.monster;

import com.khrux.argent.client.renderer.entity.state.WitherZombieRenderState;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class WitherZombieModel extends HumanoidModel<WitherZombieRenderState> {
	private static final float SCALE = 1.2F;
	private static final float POINTING_Y_ROT = -0.1F;
	private static final float KNEEL_LEG_X_ROT = (float)(Math.PI / 2);
	private static final float SLUMP_HEAD_X_ROT = 0.7F;
	private static final float SLUMP_BODY_X_ROT = 0.3F;
	private static final float SLUMP_ARM_X_ROT = -0.3F;

	private final ModelPart shatter;

	public WitherZombieModel(final ModelPart root) {
		super(root);
		this.shatter = root.getChild("shatter");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
		PartDefinition root = mesh.getRoot();
		root.getChild("right_arm")
			.addOrReplaceChild("finger", CubeListBuilder.create().texOffs(56, 16).addBox(-1.5F, 10.0F, -2.0F, 1.0F, 3.0F, 1.0F), PartPose.ZERO);
		root.addOrReplaceChild("shatter", CubeListBuilder.create(), PartPose.ZERO);
		root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(5.0F, 2.0F, 0.0F));
		root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(1.9F, 12.0F, 0.0F));
		return LayerDefinition.create(mesh, 64, 64).apply(MeshTransformer.scaling(SCALE));
	}

	@Override
	public void setupAnim(final WitherZombieRenderState state) {
		super.setupAnim(state);
		this.shatter.xRot = state.shatterProgress;
		if (state.shatterProgress > 0.0F) {
			this.setupShatterAnimation(state.shatterProgress);
		}
	}

	private void setupShatterAnimation(final float progress) {
		float kneel = kneel(progress);
		float slump = Mth.clamp((progress - 0.3F) / 0.4F, 0.0F, 1.0F);
		this.rightLeg.xRot = Mth.lerp(kneel, this.rightLeg.xRot, KNEEL_LEG_X_ROT);
		this.leftLeg.xRot = Mth.lerp(kneel, this.leftLeg.xRot, KNEEL_LEG_X_ROT);
		this.head.xRot = Mth.lerp(slump, this.head.xRot, SLUMP_HEAD_X_ROT);
		this.body.xRot = SLUMP_BODY_X_ROT * slump;
		this.rightArm.xRot = Mth.lerp(slump, this.rightArm.xRot, SLUMP_ARM_X_ROT);
		this.leftArm.xRot = Mth.lerp(slump, this.leftArm.xRot, 0.0F);
	}

	public static float kneel(final float shatterProgress) {
		return Mth.clamp(shatterProgress * 2.5F, 0.0F, 1.0F);
	}

	@Override
	protected void setupAttackAnimation(final WitherZombieRenderState state) {
		super.setupAttackAnimation(state);
		float lunge = Mth.sin(state.swingAnimation * (float)Math.PI);
		this.rightArm.xRot = (float)(-Math.PI / 2) + this.head.xRot - lunge * 0.4F;
		this.rightArm.yRot = POINTING_Y_ROT + this.head.yRot;
		this.rightArm.zRot = 0.0F;
		AnimationUtils.bobModelPart(this.rightArm, state.ageInTicks, 1.0F);
		AnimationUtils.bobModelPart(this.leftArm, state.ageInTicks, -1.0F);
	}
}
