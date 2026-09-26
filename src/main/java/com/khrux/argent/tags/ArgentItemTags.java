package com.khrux.argent.tags;

import com.khrux.argent.Argent;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ArgentItemTags {
	public static final TagKey<Item> REPAIRS_SILVER_PARROT_ARMOR = TagKey.create(Registries.ITEM, Argent.id("repairs_silver_parrot_armor"));
	public static final TagKey<Item> SCRYING_ENCHANTABLE = TagKey.create(Registries.ITEM, Argent.id("enchantable/scrying"));
	public static final TagKey<Item> WITHER_ZOMBIE_TAKES = TagKey.create(Registries.ITEM, Argent.id("wither_zombie_takes"));
}
