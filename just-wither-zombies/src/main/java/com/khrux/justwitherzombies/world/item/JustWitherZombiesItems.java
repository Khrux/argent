package com.khrux.justwitherzombies.world.item;

import com.khrux.justwitherzombies.references.JustWitherZombiesItemIds;
import com.khrux.justwitherzombies.world.entity.JustWitherZombiesEntityTypes;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.BlocksAttacks;

public class JustWitherZombiesItems {
	public static final Item REFLECTIVE_SHIELD = registerItem(
		JustWitherZombiesItemIds.REFLECTIVE_SHIELD,
		ShieldItem::new,
		new Item.Properties()
			.durability(336)
			.repairable(ItemTags.WOODEN_TOOL_MATERIALS)
			.equippableUnswappable(EquipmentSlot.OFFHAND)
			.delayedComponent(
				DataComponents.BLOCKS_ATTACKS,
				context -> new BlocksAttacks(
					0.25F,
					1.0F,
					List.of(new BlocksAttacks.DamageReduction(90.0F, Optional.empty(), 0.0F, 1.0F)),
					new BlocksAttacks.ItemDamageFunction(3.0F, 1.0F, 1.0F),
					Optional.of(context.getOrThrow(DamageTypeTags.BYPASSES_SHIELD)),
					Optional.of(SoundEvents.SHIELD_BLOCK),
					Optional.of(SoundEvents.SHIELD_BREAK)
				)
			)
			.component(DataComponents.BREAK_SOUND, SoundEvents.SHIELD_BREAK)
	);

	public static final Item WITHER_ZOMBIE_SPAWN_EGG = registerSpawnEgg(JustWitherZombiesItemIds.WITHER_ZOMBIE_SPAWN_EGG, JustWitherZombiesEntityTypes.WITHER_ZOMBIE);

	private static Item registerSpawnEgg(final ResourceKey<Item> id, final EntityType<?> type) {
		return registerItem(id, SpawnEggItem::new, new Item.Properties().spawnEgg(type));
	}

	private static Item registerItem(final ResourceKey<Item> id) {
		return registerItem(id, Item::new, new Item.Properties());
	}

	private static Item registerItem(final ResourceKey<Item> id, final Function<Item.Properties, Item> itemFactory, final Item.Properties properties) {
		return register(id, itemFactory.apply(properties.setId(id)));
	}

	private static Item register(final ResourceKey<Item> id, final Item item) {
		if (item instanceof BlockItem blockItem) {
			blockItem.registerBlocks(Item.BY_BLOCK, item);
		}

		return Registry.register(BuiltInRegistries.ITEM, id, item);
	}

	public static void bootstrap() {
	}
}
