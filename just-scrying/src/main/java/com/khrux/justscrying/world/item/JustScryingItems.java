package com.khrux.justscrying.world.item;

import com.khrux.justscrying.references.JustScryingItemIds;
import com.khrux.justscrying.tags.JustScryingItemTags;
import java.util.function.Function;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;

public class JustScryingItems {

	public static final Item PARROT_ARMOR = registerItem(
		JustScryingItemIds.PARROT_ARMOR, Item::new, petArmorProperties(EntityTypes.PARROT, JustScryingItemTags.REPAIRS_PARROT_ARMOR)
	);
	public static final Item CAT_ARMOR = registerItem(
		JustScryingItemIds.CAT_ARMOR, Item::new, petArmorProperties(EntityTypes.CAT, JustScryingItemTags.REPAIRS_CAT_ARMOR)
	);

	private static Item.Properties petArmorProperties(final EntityType<?> entityType, final TagKey<Item> repairItems) {
		return new Item.Properties()
			.durability(ArmorType.BODY.getDurability(ArmorMaterials.ARMADILLO_SCUTE.durability()))
			.attributes(ArmorMaterials.ARMADILLO_SCUTE.createAttributes(ArmorType.BODY))
			.repairable(repairItems)
			.component(
				DataComponents.EQUIPPABLE,
				Equippable.builder(EquipmentSlot.BODY)
					.setEquipSound(ArmorMaterials.ARMADILLO_SCUTE.equipSound())
					.setAllowedEntities(HolderSet.direct(entityType.builtInRegistryHolder()))
					.setCanBeSheared(true)
					.setShearingSound(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.ARMOR_UNEQUIP_WOLF))
					.build()
			)
			.component(DataComponents.BREAK_SOUND, SoundEvents.WOLF_ARMOR_BREAK)
			.stacksTo(1);
	}

	private static Item registerItem(final ResourceKey<Item> id, final Function<Item.Properties, Item> itemFactory, final Item.Properties properties) {
		return Registry.register(BuiltInRegistries.ITEM, id, itemFactory.apply(properties.setId(id)));
	}

	public static void bootstrap() {
	}
}
