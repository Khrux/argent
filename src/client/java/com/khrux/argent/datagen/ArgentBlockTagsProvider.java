package com.khrux.argent.datagen;

import com.khrux.argent.references.ArgentBlockItemIds;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockItemTagId;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.tags.BlockTags;

public class ArgentBlockTagsProvider extends FabricTagsProvider.BlockTagsProvider {
	public static final BlockItemTagId ORES_SILVER = BlockItemTagId.create(
		Identifier.fromNamespaceAndPath("c", "ores/silver"), Identifier.fromNamespaceAndPath("c", "ores/silver")
	);
	public static final BlockItemTagId STORAGE_BLOCKS_SILVER = BlockItemTagId.create(
		Identifier.fromNamespaceAndPath("c", "storage_blocks/silver"), Identifier.fromNamespaceAndPath("c", "storage_blocks/silver")
	);
	public static final BlockItemTagId STORAGE_BLOCKS_RAW_SILVER = BlockItemTagId.create(
		Identifier.fromNamespaceAndPath("c", "storage_blocks/raw_silver"), Identifier.fromNamespaceAndPath("c", "storage_blocks/raw_silver")
	);

	public ArgentBlockTagsProvider(final FabricPackOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void addTags(final HolderLookup.Provider registries) {
		this.builder(ORES_SILVER.block()).add(ArgentBlockItemIds.SILVER_ORE, ArgentBlockItemIds.DEEPSLATE_SILVER_ORE);
		this.builder(BlockItemTags.ORES.block()).add(ArgentBlockItemIds.SILVER_ORE, ArgentBlockItemIds.DEEPSLATE_SILVER_ORE);
		this.builder(STORAGE_BLOCKS_SILVER.block()).add(ArgentBlockItemIds.SILVER_BLOCK);
		this.builder(STORAGE_BLOCKS_RAW_SILVER.block()).add(ArgentBlockItemIds.RAW_SILVER_BLOCK);
		this.builder(BlockTags.BEACON_BASE_BLOCKS).add(ArgentBlockItemIds.SILVER_BLOCK);
		this.builder(BlockTags.BLOCKS_MOTION_NO_LEAVES).add(ArgentBlockItemIds.SILVER_BLOCK, ArgentBlockItemIds.RAW_SILVER_BLOCK, ArgentBlockItemIds.SILVER_BELL);
		this.builder(BlockTags.MINEABLE_WITH_PICKAXE)
			.add(
				ArgentBlockItemIds.SILVER_ORE,
				ArgentBlockItemIds.DEEPSLATE_SILVER_ORE,
				ArgentBlockItemIds.SILVER_BLOCK,
				ArgentBlockItemIds.RAW_SILVER_BLOCK,
				ArgentBlockItemIds.SILVER_BELL
			);
		this.builder(BlockTags.NEEDS_IRON_TOOL)
			.add(ArgentBlockItemIds.SILVER_ORE, ArgentBlockItemIds.DEEPSLATE_SILVER_ORE, ArgentBlockItemIds.SILVER_BLOCK, ArgentBlockItemIds.RAW_SILVER_BLOCK);
		this.builder(BlockTags.MINEABLE_WITH_AXE).add(ArgentBlockItemIds.MIRROR);
	}
}
