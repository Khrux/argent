package com.khrux.justsilver.datagen;

import com.khrux.justsilver.world.item.JustSilverItems;
import com.khrux.justsilver.world.level.block.JustSilverBlocks;
import java.util.Optional;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.properties.BellAttachType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class JustSilverModelProvider extends FabricModelProvider {
	private static final ModelTemplate BELL_FLOOR = bellTemplate("_floor");
	private static final ModelTemplate BELL_CEILING = bellTemplate("_ceiling");
	private static final ModelTemplate BELL_WALL = bellTemplate("_wall");
	private static final ModelTemplate BELL_BETWEEN_WALLS = bellTemplate("_between_walls");

	public JustSilverModelProvider(final FabricPackOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(final BlockModelGenerators blockModelGenerators) {
		blockModelGenerators.createTrivialCube(JustSilverBlocks.SILVER_ORE);
		blockModelGenerators.createTrivialCube(JustSilverBlocks.DEEPSLATE_SILVER_ORE);
		blockModelGenerators.createTrivialCube(JustSilverBlocks.SILVER_BLOCK);
		blockModelGenerators.createTrivialCube(JustSilverBlocks.RAW_SILVER_BLOCK);
		createSilverBell(blockModelGenerators);
	}

	private static ModelTemplate bellTemplate(final String suffix) {
		return new ModelTemplate(Optional.of(Identifier.withDefaultNamespace("block/bell" + suffix)), Optional.of(suffix), TextureSlot.PARTICLE);
	}

	private static void createSilverBell(final BlockModelGenerators blockModelGenerators) {
		TextureMapping particle = TextureMapping.particle(JustSilverBlocks.SILVER_BELL);
		MultiVariant floor = BlockModelGenerators.plainVariant(BELL_FLOOR.create(JustSilverBlocks.SILVER_BELL, particle, blockModelGenerators.modelOutput));
		MultiVariant ceiling = BlockModelGenerators.plainVariant(BELL_CEILING.create(JustSilverBlocks.SILVER_BELL, particle, blockModelGenerators.modelOutput));
		MultiVariant wall = BlockModelGenerators.plainVariant(BELL_WALL.create(JustSilverBlocks.SILVER_BELL, particle, blockModelGenerators.modelOutput));
		MultiVariant betweenWalls = BlockModelGenerators.plainVariant(BELL_BETWEEN_WALLS.create(JustSilverBlocks.SILVER_BELL, particle, blockModelGenerators.modelOutput));
		blockModelGenerators.blockStateOutput
			.accept(
				MultiVariantGenerator.dispatch(JustSilverBlocks.SILVER_BELL)
					.with(
						PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.BELL_ATTACHMENT)
							.select(Direction.NORTH, BellAttachType.FLOOR, floor)
							.select(Direction.SOUTH, BellAttachType.FLOOR, floor.with(BlockModelGenerators.Y_ROT_180))
							.select(Direction.EAST, BellAttachType.FLOOR, floor.with(BlockModelGenerators.Y_ROT_90))
							.select(Direction.WEST, BellAttachType.FLOOR, floor.with(BlockModelGenerators.Y_ROT_270))
							.select(Direction.NORTH, BellAttachType.CEILING, ceiling)
							.select(Direction.SOUTH, BellAttachType.CEILING, ceiling.with(BlockModelGenerators.Y_ROT_180))
							.select(Direction.EAST, BellAttachType.CEILING, ceiling.with(BlockModelGenerators.Y_ROT_90))
							.select(Direction.WEST, BellAttachType.CEILING, ceiling.with(BlockModelGenerators.Y_ROT_270))
							.select(Direction.NORTH, BellAttachType.SINGLE_WALL, wall.with(BlockModelGenerators.Y_ROT_270))
							.select(Direction.SOUTH, BellAttachType.SINGLE_WALL, wall.with(BlockModelGenerators.Y_ROT_90))
							.select(Direction.EAST, BellAttachType.SINGLE_WALL, wall)
							.select(Direction.WEST, BellAttachType.SINGLE_WALL, wall.with(BlockModelGenerators.Y_ROT_180))
							.select(Direction.SOUTH, BellAttachType.DOUBLE_WALL, betweenWalls.with(BlockModelGenerators.Y_ROT_90))
							.select(Direction.NORTH, BellAttachType.DOUBLE_WALL, betweenWalls.with(BlockModelGenerators.Y_ROT_270))
							.select(Direction.EAST, BellAttachType.DOUBLE_WALL, betweenWalls)
							.select(Direction.WEST, BellAttachType.DOUBLE_WALL, betweenWalls.with(BlockModelGenerators.Y_ROT_180))
					)
			);
	}

	@Override
	public void generateItemModels(final ItemModelGenerators itemModelGenerators) {
		itemModelGenerators.generateFlatItem(JustSilverItems.RAW_SILVER, ModelTemplates.FLAT_ITEM);
		itemModelGenerators.generateFlatItem(JustSilverItems.SILVER_INGOT, ModelTemplates.FLAT_ITEM);
		itemModelGenerators.generateFlatItem(JustSilverItems.SILVER_NUGGET, ModelTemplates.FLAT_ITEM);
		itemModelGenerators.generateFlatItem(JustSilverItems.SILVER_BELL, ModelTemplates.FLAT_ITEM);
	}
}
