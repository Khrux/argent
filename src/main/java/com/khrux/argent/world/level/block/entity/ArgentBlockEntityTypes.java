package com.khrux.argent.world.level.block.entity;

import com.khrux.argent.Argent;
import com.khrux.argent.world.level.block.ArgentBlocks;
import java.util.Set;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ArgentBlockEntityTypes {
	public static final BlockEntityType<MirrorBlockEntity> MIRROR = Registry.register(
		BuiltInRegistries.BLOCK_ENTITY_TYPE, Argent.id("mirror"), new BlockEntityType<>(MirrorBlockEntity::new, Set.of(ArgentBlocks.MIRROR))
	);
	public static final BlockEntityType<SilverBellBlockEntity> SILVER_BELL = Registry.register(
		BuiltInRegistries.BLOCK_ENTITY_TYPE, Argent.id("silver_bell"), new BlockEntityType<>(SilverBellBlockEntity::new, Set.of(ArgentBlocks.SILVER_BELL))
	);

	public static void bootstrap() {
	}
}
