package com.khrux.justsilver.world.level.block.entity;

import com.khrux.justsilver.JustSilver;
import com.khrux.justsilver.world.level.block.JustSilverBlocks;
import java.util.Set;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class JustSilverBlockEntityTypes {
	public static final BlockEntityType<SilverBellBlockEntity> SILVER_BELL = Registry.register(
		BuiltInRegistries.BLOCK_ENTITY_TYPE, JustSilver.id("silver_bell"), new BlockEntityType<>(SilverBellBlockEntity::new, Set.of(JustSilverBlocks.SILVER_BELL))
	);

	public static void bootstrap() {
	}
}
