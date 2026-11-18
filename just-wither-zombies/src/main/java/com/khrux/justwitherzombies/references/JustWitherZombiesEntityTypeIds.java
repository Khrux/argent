package com.khrux.justwitherzombies.references;

import com.khrux.justwitherzombies.JustWitherZombies;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;

public class JustWitherZombiesEntityTypeIds {
	public static final ResourceKey<EntityType<?>> WITHER_ZOMBIE = create("wither_zombie");

	private static ResourceKey<EntityType<?>> create(final String name) {
		return ResourceKey.create(Registries.ENTITY_TYPE, JustWitherZombies.id(name));
	}
}
