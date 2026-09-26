package com.khrux.argent.world.item.enchantment;

import com.khrux.argent.Argent;
import com.khrux.argent.tags.ArgentItemTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

public class ArgentEnchantments {
	public static final ResourceKey<Enchantment> SCRYING = ResourceKey.create(Registries.ENCHANTMENT, Argent.id("scrying"));

	public static void bootstrap(final BootstrapContext<Enchantment> context) {
		HolderGetter<Item> items = context.lookup(Registries.ITEM);
		register(
			context,
			SCRYING,
			Enchantment.enchantment(
				Enchantment.definition(
					items.getOrThrow(ArgentItemTags.SCRYING_ENCHANTABLE), 1, 1, Enchantment.dynamicCost(25, 25), Enchantment.dynamicCost(75, 25), 8, EquipmentSlotGroup.BODY
				)
			)
		);
	}

	private static void register(final BootstrapContext<Enchantment> context, final ResourceKey<Enchantment> key, final Enchantment.Builder builder) {
		context.register(key, builder.build(key.identifier()));
	}
}
