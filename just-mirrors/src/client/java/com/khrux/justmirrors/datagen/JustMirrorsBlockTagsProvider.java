package com.khrux.justmirrors.datagen;

import com.khrux.justmirrors.world.level.block.JustMirrorsBlocks;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;

public class JustMirrorsBlockTagsProvider extends FabricTagsProvider.BlockTagsProvider {
	public JustMirrorsBlockTagsProvider(final FabricPackOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void addTags(final HolderLookup.Provider registries) {
		this.builder(BlockTags.MINEABLE_WITH_AXE).add(JustMirrorsBlocks.MIRROR_ID);
	}
}
