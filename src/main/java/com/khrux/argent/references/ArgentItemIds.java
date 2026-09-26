package com.khrux.argent.references;

import com.khrux.argent.Argent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class ArgentItemIds {
	public static final ResourceKey<Item> RAW_SILVER = create("raw_silver");
	public static final ResourceKey<Item> SILVER_INGOT = create("silver_ingot");
	public static final ResourceKey<Item> SILVER_NUGGET = create("silver_nugget");
	public static final ResourceKey<Item> REFLECTIVE_SHIELD = create("reflective_shield");
	public static final ResourceKey<Item> SILVER_PARROT_ARMOR = create("silver_parrot_armor");
	public static final ResourceKey<Item> WITHER_ZOMBIE_SPAWN_EGG = create("wither_zombie_spawn_egg");

	private static ResourceKey<Item> create(final String name) {
		return ResourceKey.create(Registries.ITEM, Argent.id(name));
	}
}
