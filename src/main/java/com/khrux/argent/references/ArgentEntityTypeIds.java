package com.khrux.argent.references;

import com.khrux.argent.Argent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;

public class ArgentEntityTypeIds {
	public static final ResourceKey<EntityType<?>> WITHER_ZOMBIE = create("wither_zombie");

	private static ResourceKey<EntityType<?>> create(final String name) {
		return ResourceKey.create(Registries.ENTITY_TYPE, Argent.id(name));
	}
}
