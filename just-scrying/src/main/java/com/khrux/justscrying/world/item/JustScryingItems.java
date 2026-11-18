package com.khrux.justscrying.world.item;

import com.khrux.justscrying.references.JustScryingItemIds;
import com.khrux.justscrying.tags.JustScryingItemTags;
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

public class JustScryingItems {

	public static final Item PARROT_ARMOR = registerItem(
		JustScryingItemIds.PARROT_ARMOR,
		Item::new,
		new Item.Properties()
			.durability(ArmorType.BODY.getDurability(ArmorMaterials.ARMADILLO_SCUTE.durability()))
			.attributes(ArmorMaterials.ARMADILLO_SCUTE.createAttributes(ArmorType.BODY))
			.repairable(JustScryingItemTags.REPAIRS_PARROT_ARMOR)
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
