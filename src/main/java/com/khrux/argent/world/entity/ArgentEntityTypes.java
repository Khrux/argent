package com.khrux.argent.world.entity;

import com.khrux.argent.references.ArgentEntityTypeIds;
import com.khrux.argent.world.entity.monster.WitherZombie;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;

public class ArgentEntityTypes {
	public static final EntityType<WitherZombie> WITHER_ZOMBIE = register(
		ArgentEntityTypeIds.WITHER_ZOMBIE,
		EntityType.Builder.of(WitherZombie::new, MobCategory.MONSTER)
			.fireImmune()
			.immuneTo(BlockTags.WITHER_SKELETON_IMMUNE_TO)
			.sized(0.7F, 2.4F)
			.eyeHeight(2.1F)
			.ridingOffset(-0.875F)
			.clientTrackingRange(8)
			.notInPeaceful()
	);

	private static <T extends Entity> EntityType<T> register(final ResourceKey<EntityType<?>> id, final EntityType.Builder<T> builder) {
		return Registry.register(BuiltInRegistries.ENTITY_TYPE, id, builder.build(id));
	}

	public static void bootstrap() {
		FabricDefaultAttributeRegistry.register(WITHER_ZOMBIE, WitherZombie.createAttributes().build());
		SpawnPlacements.register(WITHER_ZOMBIE, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
	}
}
