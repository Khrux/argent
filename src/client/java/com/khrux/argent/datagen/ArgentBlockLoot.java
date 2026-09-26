package com.khrux.argent.datagen;

import com.khrux.argent.world.item.ArgentItems;
import com.khrux.argent.world.level.block.ArgentBlocks;
import com.khrux.argent.world.level.block.MirrorBlock;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class ArgentBlockLoot extends FabricBlockLootSubProvider {
	public ArgentBlockLoot(final FabricPackOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	public void generate() {
		this.add(ArgentBlocks.SILVER_ORE, block -> this.createOreDrop(block, ArgentItems.RAW_SILVER));
		this.add(ArgentBlocks.DEEPSLATE_SILVER_ORE, block -> this.createOreDrop(block, ArgentItems.RAW_SILVER));
		this.dropSelf(ArgentBlocks.SILVER_BLOCK);
		this.dropSelf(ArgentBlocks.RAW_SILVER_BLOCK);
		this.add(ArgentBlocks.MIRROR, block -> this.createSinglePropConditionTable(block, MirrorBlock.HALF, DoubleBlockHalf.LOWER));
		this.dropSelf(ArgentBlocks.SILVER_BELL);
	}
}
