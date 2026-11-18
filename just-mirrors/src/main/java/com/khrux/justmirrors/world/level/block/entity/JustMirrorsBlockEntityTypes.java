package com.khrux.justmirrors.world.level.block.entity;

import com.khrux.justmirrors.JustMirrors;
import com.khrux.justmirrors.world.level.block.JustMirrorsBlocks;
import java.util.Set;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class JustMirrorsBlockEntityTypes {
	public static final BlockEntityType<MirrorBlockEntity> MIRROR = Registry.register(
		BuiltInRegistries.BLOCK_ENTITY_TYPE, JustMirrors.id("mirror"), new BlockEntityType<>(MirrorBlockEntity::new, Set.of(JustMirrorsBlocks.MIRROR))
	);

	public static void bootstrap() {
	}
}
