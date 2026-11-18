package com.khrux.justscrying.world.item.enchantment;

import com.khrux.justscrying.JustScrying;
import com.khrux.justscrying.tags.JustScryingItemTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

public class JustScryingEnchantments {
	public static final ResourceKey<Enchantment> SCRYING = ResourceKey.create(Registries.ENCHANTMENT, JustScrying.id("scrying"));

	public static void bootstrap(final BootstrapContext<Enchantment> context) {
		HolderGetter<Item> items = context.lookup(Registries.ITEM);
		register(
			context,
			SCRYING,
			Enchantment.enchantment(
				Enchantment.definition(
					items.getOrThrow(JustScryingItemTags.SCRYING_ENCHANTABLE), 1, 1, Enchantment.dynamicCost(25, 25), Enchantment.dynamicCost(75, 25), 8, EquipmentSlotGroup.BODY
				)
			)
		);
	}

	private static void register(final BootstrapContext<Enchantment> context, final ResourceKey<Enchantment> key, final Enchantment.Builder builder) {
		context.register(key, builder.build(key.identifier()));
	}
}
