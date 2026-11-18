package com.khrux.justsilver.datagen;

import com.khrux.justsilver.world.item.JustSilverItems;
import com.khrux.justsilver.world.level.block.JustSilverBlocks;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;

public class JustSilverBlockLoot extends FabricBlockLootSubProvider {
	public JustSilverBlockLoot(final FabricPackOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	public void generate() {
		this.add(JustSilverBlocks.SILVER_ORE, block -> this.createOreDrop(block, JustSilverItems.RAW_SILVER));
		this.add(JustSilverBlocks.DEEPSLATE_SILVER_ORE, block -> this.createOreDrop(block, JustSilverItems.RAW_SILVER));
		this.dropSelf(JustSilverBlocks.SILVER_BLOCK);
		this.dropSelf(JustSilverBlocks.RAW_SILVER_BLOCK);
		this.dropSelf(JustSilverBlocks.SILVER_BELL);
	}
}
