package com.khrux.justscrying.tags;

import com.khrux.justscrying.JustScrying;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class JustScryingItemTags {
	public static final TagKey<Item> REPAIRS_PARROT_ARMOR = TagKey.create(Registries.ITEM, JustScrying.id("repairs_parrot_armor"));
	public static final TagKey<Item> SCRYING_ENCHANTABLE = TagKey.create(Registries.ITEM, JustScrying.id("enchantable/scrying"));
}
