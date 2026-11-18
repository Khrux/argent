package com.khrux.justscrying.references;

import com.khrux.justscrying.JustScrying;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class JustScryingItemIds {
	public static final ResourceKey<Item> PARROT_ARMOR = create("parrot_armor");

	private static ResourceKey<Item> create(final String name) {
		return ResourceKey.create(Registries.ITEM, JustScrying.id(name));
	}
}
