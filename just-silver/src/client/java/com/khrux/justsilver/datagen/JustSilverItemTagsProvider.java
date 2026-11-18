package com.khrux.justsilver.datagen;

import com.khrux.justsilver.JustSilver;
import com.khrux.justsilver.references.JustSilverItemIds;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.references.ItemIds;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class JustSilverItemTagsProvider extends FabricTagsProvider.ItemTagsProvider {
	private static final TagKey<Item> RAW_MATERIALS_SILVER = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "raw_materials/silver"));
	private static final TagKey<Item> INGOTS_SILVER = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ingots/silver"));
	private static final TagKey<Item> NUGGETS_SILVER = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "nuggets/silver"));
	public static final TagKey<Item> SOUL_LANTERN_NUGGETS = TagKey.create(Registries.ITEM, JustSilver.id("soul_lantern_nuggets"));

	public JustSilverItemTagsProvider(
		final FabricPackOutput output, final CompletableFuture<HolderLookup.Provider> registries, final FabricTagsProvider.BlockTagsProvider blockTagsProvider
	) {
		super(output, registries, blockTagsProvider);
	}

	@Override
	protected void addTags(final HolderLookup.Provider registries) {
		this.copy(JustSilverBlockTagsProvider.ORES_SILVER);
		this.copy(BlockItemTags.ORES);
		this.copy(JustSilverBlockTagsProvider.STORAGE_BLOCKS_SILVER);
		this.copy(JustSilverBlockTagsProvider.STORAGE_BLOCKS_RAW_SILVER);
		this.builder(RAW_MATERIALS_SILVER).add(JustSilverItemIds.RAW_SILVER);
		this.builder(INGOTS_SILVER).add(JustSilverItemIds.SILVER_INGOT);
		this.builder(NUGGETS_SILVER).add(JustSilverItemIds.SILVER_NUGGET);
		this.builder(ItemTags.METAL_NUGGETS).add(JustSilverItemIds.SILVER_NUGGET);
		this.builder(ItemTags.BEACON_PAYMENT_ITEMS).add(JustSilverItemIds.SILVER_INGOT);
		this.builder(SOUL_LANTERN_NUGGETS).add(ItemIds.IRON_NUGGET, JustSilverItemIds.SILVER_NUGGET);
	}
}
