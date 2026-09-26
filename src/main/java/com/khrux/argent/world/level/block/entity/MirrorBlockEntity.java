package com.khrux.argent.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MirrorBlockEntity extends BlockEntity {
	public MirrorBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
		super(ArgentBlockEntityTypes.MIRROR, worldPosition, blockState);
	}
}
