package com.khrux.argent.client.player;

import com.khrux.argent.network.protocol.ScryingBitePayload;
import com.khrux.argent.network.protocol.ScryingEndPayload;
import com.khrux.argent.network.protocol.ScryingMovePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionContext;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class ScryingView {
	private static final float WALK_SPEED = 0.3F;
	private static final float SPRINT_MULTIPLIER = 1.5F;
	private static final float FLY_SPEED = 0.25F;
	private static final float JUMP_POWER = 0.42F;
	private int petId;
	private CameraType previousCameraType = CameraType.FIRST_PERSON;
	private Input keys = Input.EMPTY;
	private Vec2 move = Vec2.ZERO;
	private Vec3 petDeltaMovement = Vec3.ZERO;

	public boolean isActive() {
		return this.petId != 0;
	}

	public boolean isPet(final Entity entity) {
		return this.isActive() && entity.level().isClientSide() && entity.getId() == this.petId;
	}

	public void link(final Minecraft minecraft, final int petId) {
		if (petId == 0) {
			this.unlink(minecraft);
			return;
		}

		this.petId = petId;
		this.previousCameraType = minecraft.options.getCameraType();
	}

	private void unlink(final Minecraft minecraft) {
		if (this.pet(minecraft) instanceof Parrot parrot) {
			parrot.setNoGravity(false);
		}

		this.petId = 0;
		this.keys = Input.EMPTY;
		this.move = Vec2.ZERO;
		this.petDeltaMovement = Vec3.ZERO;
		minecraft.options.setCameraType(this.previousCameraType);
		minecraft.setCameraEntity(minecraft.player);
	}

	private @Nullable LivingEntity pet(final Minecraft minecraft) {
		return minecraft.level != null && minecraft.level.getEntity(this.petId) instanceof LivingEntity pet ? pet : null;
	}

	public void captureInput(final Input keys, final Vec2 move) {
		this.keys = keys;
		this.move = move;
	}

	public boolean turn(final Entity entity, final double xo, final double yo) {
		Minecraft minecraft = Minecraft.getInstance();
		LivingEntity pet = this.pet(minecraft);
		if (entity != minecraft.player || pet == null) {
			return false;
		}

		float yRotBefore = pet.getYRot();
		pet.turn(xo, yo);
		pet.yHeadRotO += pet.getYRot() - yRotBefore;
		pet.setYHeadRot(pet.getYRot());
		pet.setYBodyRot(pet.getYRot());
		return true;
	}

	public void startTick(final Minecraft minecraft) {
		if (!this.isActive()) {
			return;
		}

		while (minecraft.options.keyUse.consumeClick()) {
			ClientPlayNetworking.send(ScryingEndPayload.INSTANCE);
		}

		while (minecraft.options.keyAttack.consumeClick()) {
			if (minecraft.hitResult instanceof EntityHitResult hit) {
				ClientPlayNetworking.send(new ScryingBitePayload(hit.getEntity().getId()));
			}
		}

		minecraft.options.keyUse.setDown(false);
		minecraft.options.keyAttack.setDown(false);
		LivingEntity pet = this.pet(minecraft);
		if (pet != null) {
			pet.getInterpolation().cancel();
			pet.setDeltaMovement(this.petDeltaMovement);
			pet.lerpHeadTo(pet.getYRot(), 0);
			pet.setYHeadRot(pet.getYRot());
		}
	}

	public void endTick(final Minecraft minecraft) {
		LivingEntity pet = this.pet(minecraft);
		if (pet == null) {
			return;
		}

		if (minecraft.getCameraEntity() != pet) {
			minecraft.setCameraEntity(pet);
		}

		if (!minecraft.options.getCameraType().isFirstPerson()) {
			minecraft.options.setCameraType(CameraType.FIRST_PERSON);
		}

		float yRot = pet.getYRot() * Mth.DEG_TO_RAD;
		float sin = Mth.sin(yRot);
		float cos = Mth.cos(yRot);
		boolean flying = pet instanceof Parrot;
		float speed = flying ? FLY_SPEED : this.keys.sprint() ? WALK_SPEED * SPRINT_MULTIPLIER : WALK_SPEED;
		double x = (this.move.x * cos - this.move.y * sin) * speed;
		double z = (this.move.y * cos + this.move.x * sin) * speed;
		double y = pet.getDeltaMovement().y;
		if (flying) {
			pet.setNoGravity(true);
			y = ((this.keys.jump() ? 1.0 : 0.0) - (this.keys.shift() ? 1.0 : 0.0)) * speed;
		} else if (this.keys.jump() && pet.onGround()) {
			y = JUMP_POWER;
		}

		this.petDeltaMovement = new Vec3(x, y, z);
		pet.setDeltaMovement(this.petDeltaMovement);
		ClientPlayNetworking.send(new ScryingMovePayload(pet.position(), pet.getYRot(), pet.getXRot(), pet.onGround()));
	}

	public void extract(final LevelExtractionContext context) {
		Minecraft minecraft = Minecraft.getInstance();
		if (!this.isActive() || minecraft.player == null) {
			return;
		}

		context.levelState().playerRenderState.hasPlayer = false;
		float partialTicks = context.deltaTracker().getGameTimeDeltaPartialTick(false);
		context.levelState().entityRenderStates.add(minecraft.getEntityRenderDispatcher().extractEntity(minecraft.player, partialTicks));
	}
}
