package com.khrux.argent.world.level.block;

import com.khrux.argent.world.level.block.entity.ArgentBlockEntityTypes;
import com.khrux.argent.world.level.block.entity.SilverBellBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jspecify.annotations.Nullable;

public class SilverBellBlock extends BellBlock {
	private static final float PITCH = 1.5F;

	public SilverBellBlock(final BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	public boolean attemptToRing(final @Nullable Entity ringingEntity, final Level level, final BlockPos pos, @Nullable Direction direction) {
		if (level.isClientSide() || !(level.getBlockEntity(pos) instanceof SilverBellBlockEntity bellBlockEntity)) {
			return false;
		}

		if (direction == null) {
			direction = level.getBlockState(pos).getValue(FACING);
		}

		bellBlockEntity.onHit(direction);
		level.playSound(null, pos, SoundEvents.BELL_BLOCK, SoundSource.BLOCKS, 2.0F, PITCH);
		level.gameEvent(ringingEntity, GameEvent.BLOCK_CHANGE, pos);
		return true;
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
		return new SilverBellBlockEntity(worldPosition, blockState);
	}

	@Override
	public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(final Level level, final BlockState blockState, final BlockEntityType<T> type) {
		return createTickerHelper(type, ArgentBlockEntityTypes.SILVER_BELL, SilverBellBlockEntity::tick);
	}
}
