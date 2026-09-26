package com.khrux.argent.datagen;

import com.khrux.argent.Argent;
import com.khrux.argent.references.ArgentItemIds;
import com.khrux.argent.tags.ArgentItemTags;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.references.ItemIds;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ArgentItemTagsProvider extends FabricTagsProvider.ItemTagsProvider {
	private static final TagKey<Item> RAW_MATERIALS_SILVER = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "raw_materials/silver"));
	private static final TagKey<Item> INGOTS_SILVER = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ingots/silver"));
	private static final TagKey<Item> NUGGETS_SILVER = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "nuggets/silver"));
	public static final TagKey<Item> SOUL_LANTERN_NUGGETS = TagKey.create(Registries.ITEM, Argent.id("soul_lantern_nuggets"));

	public ArgentItemTagsProvider(
		final FabricPackOutput output, final CompletableFuture<HolderLookup.Provider> registries, final FabricTagsProvider.BlockTagsProvider blockTagsProvider
	) {
		super(output, registries, blockTagsProvider);
	}

	@Override
	protected void addTags(final HolderLookup.Provider registries) {
		this.copy(ArgentBlockTagsProvider.ORES_SILVER);
		this.copy(BlockItemTags.ORES);
		this.copy(ArgentBlockTagsProvider.STORAGE_BLOCKS_SILVER);
		this.copy(ArgentBlockTagsProvider.STORAGE_BLOCKS_RAW_SILVER);
		this.builder(RAW_MATERIALS_SILVER).add(ArgentItemIds.RAW_SILVER);
		this.builder(INGOTS_SILVER).add(ArgentItemIds.SILVER_INGOT);
		this.builder(NUGGETS_SILVER).add(ArgentItemIds.SILVER_NUGGET);
		this.builder(ItemTags.METAL_NUGGETS).add(ArgentItemIds.SILVER_NUGGET);
		this.builder(ItemTags.DURABILITY_ENCHANTABLE).add(ArgentItemIds.REFLECTIVE_SHIELD);
		this.builder(ItemTags.BEACON_PAYMENT_ITEMS).add(ArgentItemIds.SILVER_INGOT);
		this.builder(SOUL_LANTERN_NUGGETS).add(ItemIds.IRON_NUGGET, ArgentItemIds.SILVER_NUGGET);
		this.builder(ArgentItemTags.SCRYING_ENCHANTABLE).add(ItemIds.WOLF_ARMOR, ArgentItemIds.SILVER_PARROT_ARMOR);
		this.builder(ArgentItemTags.REPAIRS_SILVER_PARROT_ARMOR).add(ArgentItemIds.SILVER_INGOT);
		this.builder(ArgentItemTags.WITHER_ZOMBIE_TAKES)
			.forceAddTag(ConventionalItemTags.FOODS)
			.forceAddTag(ConventionalItemTags.CROPS)
			.forceAddTag(ConventionalItemTags.SEEDS)
			.forceAddTag(BlockItemTags.FLOWERS.item())
			.forceAddTag(ItemTags.SAPLINGS)
			.forceAddTag(ItemTags.LEAVES)
			.forceAddTag(ConventionalItemTags.LEATHERS)
			.forceAddTag(ConventionalItemTags.FEATHERS)
			.forceAddTag(ItemTags.WOOL)
			.forceAddTag(ItemTags.EGGS);
	}
}
