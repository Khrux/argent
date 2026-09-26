package com.khrux.argent.world.level.block;

import com.khrux.argent.config.ArgentConfig;
import com.khrux.argent.world.level.block.entity.MirrorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

public class MirrorBlock extends Block implements EntityBlock {
	public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

	public MirrorBlock(final BlockBehaviour.Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(ROTATION, 0).setValue(HALF, DoubleBlockHalf.LOWER));
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
		return blockState.getValue(HALF) == DoubleBlockHalf.LOWER ? new MirrorBlockEntity(worldPosition, blockState) : null;
	}

	@Override
	protected RenderShape getRenderShape(final BlockState state) {
		return state.getValue(HALF) == DoubleBlockHalf.LOWER ? RenderShape.MODEL : RenderShape.INVISIBLE;
	}

	@Override
	protected boolean canSurvive(final BlockState state, final LevelReader level, final BlockPos pos) {
		if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
			return level.getBlockState(pos.below()).isSolid();
		}

		BlockState belowState = level.getBlockState(pos.below());
		return belowState.is(this) && belowState.getValue(HALF) == DoubleBlockHalf.LOWER;
	}

	@Override
	public @Nullable BlockState getStateForPlacement(final BlockPlaceContext context) {
		BlockPos pos = context.getClickedPos();
		Level level = context.getLevel();
		if (pos.getY() >= level.getMaxY() || !level.getBlockState(pos.above()).canBeReplaced(context)) {
			return null;
		}

		return this.defaultBlockState().setValue(ROTATION, RotationSegment.convertToSegment(context.getRotation() + 180.0F));
	}

	@Override
	public void setPlacedBy(final Level level, final BlockPos pos, final BlockState state, final @Nullable LivingEntity by, final ItemStack itemStack) {
		level.setBlockAndUpdate(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER));
	}

	@Override
	public BlockState playerWillDestroy(final Level level, final BlockPos pos, final BlockState state, final Player player) {
		if (!level.isClientSide() && player.preventsBlockDrops() && state.getValue(HALF) == DoubleBlockHalf.UPPER) {
			BlockPos lowerPos = pos.below();
			BlockState lowerState = level.getBlockState(lowerPos);
			if (lowerState.is(this) && lowerState.getValue(HALF) == DoubleBlockHalf.LOWER) {
				level.setBlock(lowerPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
				level.levelEvent(player, LevelEvent.PARTICLES_AND_SOUND_DESTROY_BLOCK, lowerPos, Block.getId(lowerState));
			}
		}

		return super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
		return ArgentConfig.get().mirrorScreen ? InteractionResult.SUCCESS : InteractionResult.PASS;
	}

	@Override
	protected BlockState updateShape(
		final BlockState state,
		final LevelReader level,
		final ScheduledTickAccess ticks,
		final BlockPos pos,
		final Direction directionToNeighbour,
		final BlockPos neighbourPos,
		final BlockState neighbourState,
		final RandomSource random
	) {
		DoubleBlockHalf half = state.getValue(HALF);
		if (directionToNeighbour.getAxis() == Direction.Axis.Y
			&& half == DoubleBlockHalf.LOWER == (directionToNeighbour == Direction.UP)
			&& !(neighbourState.is(this) && neighbourState.getValue(HALF) != half)) {
			return Blocks.AIR.defaultBlockState();
		}

		return half == DoubleBlockHalf.LOWER && directionToNeighbour == Direction.DOWN && !state.canSurvive(level, pos)
			? Blocks.AIR.defaultBlockState()
			: super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
	}

	@Override
	protected BlockState rotate(final BlockState state, final Rotation rotation) {
		return state.setValue(ROTATION, rotation.rotate(state.getValue(ROTATION), 16));
	}

	@Override
	protected BlockState mirror(final BlockState state, final Mirror mirror) {
		return state.setValue(ROTATION, mirror.mirror(state.getValue(ROTATION), 16));
	}

	@Override
	protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(ROTATION, HALF);
	}
}
