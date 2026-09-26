package com.khrux.argent.client.player;

import com.khrux.argent.sounds.ArgentSoundEvents;
import com.khrux.argent.world.entity.monster.WitherZombie;
import com.khrux.argent.world.item.ArgentItems;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class WitherZombieGaze {
	private static final double RANGE = 24.0;
	private static final float MARGIN_DEGREES = 10.0F;
	private static final float WALL_DEGREES = 6.0F;
	private static final float MAX_PUSH = 30.0F;
	private static final float PITCH_EASE = 0.15F;
	private IntSet trackers = new IntOpenHashSet();

	public void extract(final LevelExtractionContext context) {
		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = minecraft.player;
		if (player == null) {
			return;
		}

		float ticks = context.deltaTracker().getGameTimeDeltaTicks();
		IntSet current = new IntOpenHashSet();
		for (WitherZombie zombie : context.level()
			.getEntitiesOfClass(WitherZombie.class, player.getBoundingBox().inflate(RANGE), zombie -> zombie.getTrackedPlayerId() == player.getId())) {
			current.add(zombie.getId());
			if (!this.trackers.contains(zombie.getId())) {
				context.level().playLocalSound(zombie, ArgentSoundEvents.WITHER_ZOMBIE_NOTICE, SoundSource.HOSTILE, 1.0F, 1.0F);
			}

			if (!player.isHolding(ArgentItems.REFLECTIVE_SHIELD) && player.hasLineOfSight(zombie)) {
				this.lookAway(minecraft, player, zombie, ticks);
			}
		}

		this.trackers = current;
	}

	private void lookAway(final Minecraft minecraft, final LocalPlayer player, final WitherZombie zombie, final float ticks) {
		Vec3 toZombie = zombie.getBoundingBox().getCenter().subtract(player.getEyePosition()).normalize();
		float angle = (float)Math.toDegrees(Math.acos(Mth.clamp(player.getLookAngle().dot(toZombie), -1.0, 1.0)));
		float cone = horizontalHalfFov(minecraft) + MARGIN_DEGREES;
		if (angle > cone) {
			return;
		}

		double distance = zombie.getBoundingBox().getCenter().distanceTo(player.getEyePosition());
		float wall = WALL_DEGREES + (float)Math.toDegrees(Math.atan2(zombie.getBbHeight() / 2.0F, distance));
		float yawToZombie = (float)Math.toDegrees(Mth.atan2(toZombie.z, toZombie.x)) - 90.0F;
		float side = Mth.wrapDegrees(yawToZombie - player.getYRot()) < 0.0F ? -1.0F : 1.0F;
		player.setXRot(player.getXRot() * Math.max(0.0F, 1.0F - PITCH_EASE * ticks));
		if (angle < wall) {
			player.setYRot(player.getYRot() + Mth.wrapDegrees(yawToZombie - side * wall - player.getYRot()));
			return;
		}

		float closeness = (cone - angle) / (cone - wall);
		player.setYRot(player.getYRot() - side * MAX_PUSH * closeness * closeness * ticks);
	}

	private static float horizontalHalfFov(final Minecraft minecraft) {
		double verticalHalf = Math.toRadians(minecraft.options.fov().get() / 2.0);
		double aspect = (double)minecraft.getWindow().getWidth() / Math.max(1, minecraft.getWindow().getHeight());
		return (float)Math.toDegrees(Math.atan(Math.tan(verticalHalf) * aspect));
	}
}
