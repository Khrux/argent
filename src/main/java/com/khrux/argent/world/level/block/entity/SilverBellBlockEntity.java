package com.khrux.argent.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SilverBellBlockEntity extends BlockEntity {
	private static final int DURATION = 50;
	public int ticks;
	public boolean shaking;
	public Direction clickDirection;

	public SilverBellBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
		super(ArgentBlockEntityTypes.SILVER_BELL, worldPosition, blockState);
	}

	@Override
	public boolean triggerEvent(final int b0, final int b1) {
		if (b0 != BellBlock.EVENT_BELL_RING) {
			return super.triggerEvent(b0, b1);
		}

		this.clickDirection = Direction.from3DDataValue(b1);
		this.ticks = 0;
		this.shaking = true;
		return true;
	}

	public static void tick(final Level level, final BlockPos pos, final BlockState state, final SilverBellBlockEntity entity) {
		if (entity.shaking) {
			entity.ticks++;
		}

		if (entity.ticks >= DURATION) {
			entity.shaking = false;
			entity.ticks = 0;
		}
	}

	public void onHit(final Direction clickDirection) {
		this.clickDirection = clickDirection;
		if (this.shaking) {
			this.ticks = 0;
		} else {
			this.shaking = true;
		}

		this.level.blockEvent(this.getBlockPos(), this.getBlockState().getBlock(), BellBlock.EVENT_BELL_RING, clickDirection.get3DDataValue());
	}
}
