package com.khrux.justsilver.references;

import com.khrux.justsilver.JustSilver;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class JustSilverItemIds {
	public static final ResourceKey<Item> RAW_SILVER = create("raw_silver");
	public static final ResourceKey<Item> SILVER_INGOT = create("silver_ingot");
	public static final ResourceKey<Item> SILVER_NUGGET = create("silver_nugget");

	private static ResourceKey<Item> create(final String name) {
		return ResourceKey.create(Registries.ITEM, JustSilver.id(name));
	}
}
