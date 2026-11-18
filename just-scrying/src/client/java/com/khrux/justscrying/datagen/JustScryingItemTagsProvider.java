package com.khrux.justscrying.datagen;

import com.khrux.justscrying.references.JustScryingItemIds;
import com.khrux.justscrying.tags.JustScryingItemTags;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.references.ItemIds;
import net.minecraft.tags.ItemTags;

public class JustScryingItemTagsProvider extends FabricTagsProvider.ItemTagsProvider {
	public JustScryingItemTagsProvider(final FabricPackOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void addTags(final HolderLookup.Provider registries) {
		this.builder(JustScryingItemTags.SCRYING_ENCHANTABLE).add(ItemIds.WOLF_ARMOR, JustScryingItemIds.PARROT_ARMOR);
		this.builder(JustScryingItemTags.REPAIRS_PARROT_ARMOR).add(ItemIds.LEATHER);
		this.builder(ItemTags.CAULDRON_CAN_REMOVE_DYE).add(JustScryingItemIds.PARROT_ARMOR);
	}
}
