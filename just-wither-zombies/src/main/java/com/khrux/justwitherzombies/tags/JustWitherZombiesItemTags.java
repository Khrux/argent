package com.khrux.justwitherzombies.tags;

import com.khrux.justwitherzombies.JustWitherZombies;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class JustWitherZombiesItemTags {
	public static final TagKey<Item> WITHER_ZOMBIE_TAKES = TagKey.create(Registries.ITEM, JustWitherZombies.id("wither_zombie_takes"));
}
