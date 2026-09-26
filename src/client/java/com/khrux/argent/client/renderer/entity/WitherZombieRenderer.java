package com.khrux.argent.client.renderer.entity;

import com.khrux.argent.Argent;
import com.khrux.argent.client.model.geom.ArgentModelLayers;
import com.khrux.argent.client.model.monster.WitherZombieModel;
import com.khrux.argent.client.renderer.entity.state.WitherZombieRenderState;
import com.khrux.argent.world.entity.monster.WitherZombie;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.Identifier;

public class WitherZombieRenderer extends HumanoidMobRenderer<WitherZombie, WitherZombieRenderState, WitherZombieModel> {
	private static final float KNEEL_DROP = 0.75F;
	private static final Identifier WITHER_ZOMBIE_LOCATION = Argent.id("textures/entity/wither_zombie/wither_zombie.png");

	public WitherZombieRenderer(final EntityRendererProvider.Context context) {
		super(context, new WitherZombieModel(context.bakeLayer(ArgentModelLayers.WITHER_ZOMBIE)), 0.5F);
	}

	@Override
	public Identifier getTextureLocation(final WitherZombieRenderState state) {
		return WITHER_ZOMBIE_LOCATION;
	}

	@Override
	public WitherZombieRenderState createRenderState() {
		return new WitherZombieRenderState();
	}

	@Override
	protected void scale(final WitherZombieRenderState state, final PoseStack poseStack) {
		super.scale(state, poseStack);
		if (state.shatterProgress <= 0.0F) {
			return;
		}

		poseStack.translate(0.0F, KNEEL_DROP * WitherZombieModel.kneel(state.shatterProgress), 0.0F);
	}

	@Override
	public void extractRenderState(final WitherZombie entity, final WitherZombieRenderState state, final float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		state.shatterProgress = entity.getShatterProgress(partialTicks);
	}
}
