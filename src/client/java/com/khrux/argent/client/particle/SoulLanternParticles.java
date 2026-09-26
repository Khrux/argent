package com.khrux.argent.client.particle;

import com.khrux.argent.config.ArgentConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.dimension.DimensionType;

public class SoulLanternParticles {
	private static final int ATTEMPTS = 64;
	private static final int HORIZONTAL_RANGE = 18;
	private static final int VERTICAL_RANGE = 8;
	private static final int THUNDER_SKY_DARKENING = 10;
	private static final double RISE_SPEED = 0.05;

	public static void tick(final ClientLevel level) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null || ArgentConfig.get().soulLantern == ArgentConfig.SoulLantern.OFF || !isCarryingLantern(player)) {
			return;
		}

		RandomSource random = level.getRandom();
		BlockPos center = player.blockPosition();
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		for (int i = 0; i < ATTEMPTS; i++) {
			pos.set(
				center.getX() + random.nextInt(HORIZONTAL_RANGE) - random.nextInt(HORIZONTAL_RANGE),
				center.getY() + random.nextInt(VERTICAL_RANGE) - random.nextInt(VERTICAL_RANGE),
				center.getZ() + random.nextInt(HORIZONTAL_RANGE) - random.nextInt(HORIZONTAL_RANGE)
			);
			if (canMonsterSpawn(level, pos)) {
				level.addParticle(ParticleTypes.SOUL, pos.getX() + random.nextDouble(), pos.getY() + 0.1, pos.getZ() + random.nextDouble(), 0.0, RISE_SPEED, 0.0);
			}
		}
	}

	private static boolean isCarryingLantern(final LocalPlayer player) {
		if (ArgentConfig.get().soulLantern == ArgentConfig.SoulLantern.INVENTORY) {
			return player.getInventory().contains(stack -> stack.is(Items.SOUL_LANTERN));
		}

		return player.getMainHandItem().is(Items.SOUL_LANTERN) || player.getOffhandItem().is(Items.SOUL_LANTERN);
	}

	private static boolean canMonsterSpawn(final ClientLevel level, final BlockPos pos) {
		if (!SpawnPlacements.isSpawnPositionOk(EntityTypes.ZOMBIE, level, pos)) {
			return false;
		}

		DimensionType dimensionType = level.dimensionType();
		int blockLightLimit = dimensionType.monsterSpawnBlockLightLimit();
		if (blockLightLimit < 15 && level.getBrightness(LightLayer.BLOCK, pos) > blockLightLimit) {
			return false;
		}

		int brightness = level.isThundering() ? level.getMaxLocalRawBrightness(pos, THUNDER_SKY_DARKENING) : level.getMaxLocalRawBrightness(pos);
		return brightness <= dimensionType.monsterSpawnLightTest().maxInclusive();
	}
}
