package com.khrux.justmirrors.datagen;

import com.khrux.justmirrors.world.level.block.JustMirrorsBlocks;
import com.khrux.justmirrors.world.level.block.MirrorBlock;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class JustMirrorsBlockLoot extends FabricBlockLootSubProvider {
	public JustMirrorsBlockLoot(final FabricPackOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	public void generate() {
		this.add(JustMirrorsBlocks.MIRROR, block -> this.createSinglePropConditionTable(block, MirrorBlock.HALF, DoubleBlockHalf.LOWER));
	}
}
