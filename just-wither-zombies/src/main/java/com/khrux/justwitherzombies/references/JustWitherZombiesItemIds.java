package com.khrux.justwitherzombies.references;

import com.khrux.justwitherzombies.JustWitherZombies;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class JustWitherZombiesItemIds {
	public static final ResourceKey<Item> REFLECTIVE_SHIELD = create("reflective_shield");
	public static final ResourceKey<Item> WITHER_ZOMBIE_SPAWN_EGG = create("wither_zombie_spawn_egg");

	private static ResourceKey<Item> create(final String name) {
		return ResourceKey.create(Registries.ITEM, JustWitherZombies.id(name));
	}
}
