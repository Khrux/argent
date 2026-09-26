package com.khrux.argent.world.item;

import com.khrux.argent.references.ArgentBlockItemIds;
import com.khrux.argent.references.ArgentItemIds;
import com.khrux.argent.tags.ArgentItemTags;
import com.khrux.argent.world.entity.ArgentEntityTypes;
import com.khrux.argent.world.level.block.ArgentBlocks;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.block.Block;

public class ArgentItems {
	public static final Item SILVER_ORE = registerBlock(ArgentBlockItemIds.SILVER_ORE, ArgentBlocks.SILVER_ORE);
	public static final Item DEEPSLATE_SILVER_ORE = registerBlock(ArgentBlockItemIds.DEEPSLATE_SILVER_ORE, ArgentBlocks.DEEPSLATE_SILVER_ORE);
	public static final Item SILVER_BLOCK = registerBlock(ArgentBlockItemIds.SILVER_BLOCK, ArgentBlocks.SILVER_BLOCK);
	public static final Item RAW_SILVER_BLOCK = registerBlock(ArgentBlockItemIds.RAW_SILVER_BLOCK, ArgentBlocks.RAW_SILVER_BLOCK);
	public static final Item MIRROR = registerBlock(ArgentBlockItemIds.MIRROR, ArgentBlocks.MIRROR);
	public static final Item SILVER_BELL = registerBlock(ArgentBlockItemIds.SILVER_BELL, ArgentBlocks.SILVER_BELL);
	public static final Item RAW_SILVER = registerItem(ArgentItemIds.RAW_SILVER);
	public static final Item SILVER_INGOT = registerItem(ArgentItemIds.SILVER_INGOT);
	public static final Item SILVER_NUGGET = registerItem(ArgentItemIds.SILVER_NUGGET);
	public static final Item REFLECTIVE_SHIELD = registerItem(
		ArgentItemIds.REFLECTIVE_SHIELD,
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

	public static final Item SILVER_PARROT_ARMOR = registerItem(
		ArgentItemIds.SILVER_PARROT_ARMOR,
		Item::new,
		new Item.Properties()
			.durability(ArmorType.BODY.getDurability(ArmorMaterials.ARMADILLO_SCUTE.durability()))
			.attributes(ArmorMaterials.ARMADILLO_SCUTE.createAttributes(ArmorType.BODY))
			.repairable(ArgentItemTags.REPAIRS_SILVER_PARROT_ARMOR)
			.component(
				DataComponents.EQUIPPABLE,
				Equippable.builder(EquipmentSlot.BODY)
					.setEquipSound(ArmorMaterials.ARMADILLO_SCUTE.equipSound())
					.setAllowedEntities(HolderSet.direct(EntityTypes.PARROT.builtInRegistryHolder()))
					.setCanBeSheared(true)
					.setShearingSound(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.ARMOR_UNEQUIP_WOLF))
					.build()
			)
			.component(DataComponents.BREAK_SOUND, SoundEvents.WOLF_ARMOR_BREAK)
			.stacksTo(1)
	);
	public static final Item WITHER_ZOMBIE_SPAWN_EGG = registerSpawnEgg(ArgentItemIds.WITHER_ZOMBIE_SPAWN_EGG, ArgentEntityTypes.WITHER_ZOMBIE);

	private static Item registerSpawnEgg(final ResourceKey<Item> id, final EntityType<?> type) {
		return registerItem(id, SpawnEggItem::new, new Item.Properties().spawnEgg(type));
	}

	private static Item registerBlock(final BlockItemId id, final Block block) {
		return register(id.item(), new BlockItem(block, new Item.Properties().useBlockDescriptionPrefix().setId(id.item())));
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
