package com.khrux.justmirrors.datagen;

import com.khrux.justmirrors.world.level.block.JustMirrorsBlocks;
import com.khrux.justmirrors.world.level.block.MirrorBlock;
import com.mojang.math.Quadrant;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class JustMirrorsModelProvider extends FabricModelProvider {
	public JustMirrorsModelProvider(final FabricPackOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(final BlockModelGenerators blockModelGenerators) {
		Identifier mirrorTop = ModelTemplates.PARTICLE_ONLY.createWithSuffix(
			JustMirrorsBlocks.MIRROR, "_top", TextureMapping.particle(Blocks.SPRUCE_PLANKS), blockModelGenerators.modelOutput
		);
		blockModelGenerators.blockStateOutput
			.accept(
				MultiVariantGenerator.dispatch(JustMirrorsBlocks.MIRROR)
					.with(PropertyDispatch.initial(MirrorBlock.HALF, MirrorBlock.ROTATION).generate((half, rotation) -> {
						if (half == DoubleBlockHalf.UPPER) {
							return BlockModelGenerators.plainVariant(mirrorTop);
						}

						int turn = (rotation + 8) % 16;
						return BlockModelGenerators.plainVariant(MirrorRotatedModelProvider.modelLocation(turn % 4))
							.with(VariantMutator.Y_ROT.withValue(Quadrant.values()[turn / 4]));
					}))
			);
	}

	@Override
	public void generateItemModels(final ItemModelGenerators itemModelGenerators) {
	}
}
