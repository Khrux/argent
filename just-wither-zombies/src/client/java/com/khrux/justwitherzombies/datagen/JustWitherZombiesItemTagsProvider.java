package com.khrux.justwitherzombies.datagen;

import com.khrux.justwitherzombies.references.JustWitherZombiesItemIds;
import com.khrux.justwitherzombies.tags.JustWitherZombiesItemTags;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.tags.ItemTags;

public class JustWitherZombiesItemTagsProvider extends FabricTagsProvider.ItemTagsProvider {
	public JustWitherZombiesItemTagsProvider(final FabricPackOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void addTags(final HolderLookup.Provider registries) {
		this.builder(ItemTags.DURABILITY_ENCHANTABLE).add(JustWitherZombiesItemIds.REFLECTIVE_SHIELD);
		this.builder(JustWitherZombiesItemTags.WITHER_ZOMBIE_TAKES)
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
