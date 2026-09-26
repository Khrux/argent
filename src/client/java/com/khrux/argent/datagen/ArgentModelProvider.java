package com.khrux.argent.datagen;

import com.khrux.argent.client.renderer.special.ReflectiveShieldSpecialRenderer;
import com.khrux.argent.world.item.ArgentItems;
import com.khrux.argent.world.level.block.ArgentBlocks;
import com.khrux.argent.world.level.block.MirrorBlock;
import com.mojang.math.Quadrant;
import java.util.List;
import java.util.Optional;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.properties.select.DisplayContext;
import net.minecraft.client.renderer.special.ShieldSpecialRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BellAttachType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class ArgentModelProvider extends FabricModelProvider {
	private static final ModelTemplate SHIELD = new ModelTemplate(Optional.of(Identifier.withDefaultNamespace("item/shield")), Optional.empty());
	private static final ModelTemplate SHIELD_BLOCKING = new ModelTemplate(Optional.of(Identifier.withDefaultNamespace("item/shield_blocking")), Optional.of("_blocking"));
	private static final List<ItemDisplayContext> REFLECTIVE_CONTEXTS = List.of(
		ItemDisplayContext.THIRD_PERSON_LEFT_HAND, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, ItemDisplayContext.GROUND, ItemDisplayContext.FIXED
	);
	private static final ModelTemplate BELL_FLOOR = bellTemplate("_floor");
	private static final ModelTemplate BELL_CEILING = bellTemplate("_ceiling");
	private static final ModelTemplate BELL_WALL = bellTemplate("_wall");
	private static final ModelTemplate BELL_BETWEEN_WALLS = bellTemplate("_between_walls");

	public ArgentModelProvider(final FabricPackOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(final BlockModelGenerators blockModelGenerators) {
		blockModelGenerators.createTrivialCube(ArgentBlocks.SILVER_ORE);
		blockModelGenerators.createTrivialCube(ArgentBlocks.DEEPSLATE_SILVER_ORE);
		blockModelGenerators.createTrivialCube(ArgentBlocks.SILVER_BLOCK);
		blockModelGenerators.createTrivialCube(ArgentBlocks.RAW_SILVER_BLOCK);
		createSilverBell(blockModelGenerators);
		Identifier mirrorTop = ModelTemplates.PARTICLE_ONLY.createWithSuffix(
			ArgentBlocks.MIRROR, "_top", TextureMapping.particle(Blocks.SPRUCE_PLANKS), blockModelGenerators.modelOutput
		);
		blockModelGenerators.blockStateOutput
			.accept(
				MultiVariantGenerator.dispatch(ArgentBlocks.MIRROR)
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

	private static void createReflectiveShield(final ItemModelGenerators itemModelGenerators) {
		Identifier normal = SHIELD.create(ArgentItems.REFLECTIVE_SHIELD, new TextureMapping(), itemModelGenerators.modelOutput);
		Identifier blocking = SHIELD_BLOCKING.create(ArgentItems.REFLECTIVE_SHIELD, new TextureMapping(), itemModelGenerators.modelOutput);
		itemModelGenerators.itemModelOutput
			.accept(
				ArgentItems.REFLECTIVE_SHIELD,
				ItemModelUtils.conditional(ShieldSpecialRenderer.DEFAULT_TRANSFORMATION, ItemModelUtils.isUsingItem(), shieldModel(blocking), shieldModel(normal))
			);
	}

	private static ItemModel.Unbaked shieldModel(final Identifier base) {
		return ItemModelUtils.select(
			new DisplayContext(),
			ItemModelUtils.specialModel(base, new ReflectiveShieldSpecialRenderer.Unbaked(false)),
			ItemModelUtils.when(REFLECTIVE_CONTEXTS, ItemModelUtils.specialModel(base, new ReflectiveShieldSpecialRenderer.Unbaked(true)))
		);
	}

	private static ModelTemplate bellTemplate(final String suffix) {
		return new ModelTemplate(Optional.of(Identifier.withDefaultNamespace("block/bell" + suffix)), Optional.of(suffix), TextureSlot.PARTICLE);
	}

	private static void createSilverBell(final BlockModelGenerators blockModelGenerators) {
		TextureMapping particle = TextureMapping.particle(ArgentBlocks.SILVER_BELL);
		MultiVariant floor = BlockModelGenerators.plainVariant(BELL_FLOOR.create(ArgentBlocks.SILVER_BELL, particle, blockModelGenerators.modelOutput));
		MultiVariant ceiling = BlockModelGenerators.plainVariant(BELL_CEILING.create(ArgentBlocks.SILVER_BELL, particle, blockModelGenerators.modelOutput));
		MultiVariant wall = BlockModelGenerators.plainVariant(BELL_WALL.create(ArgentBlocks.SILVER_BELL, particle, blockModelGenerators.modelOutput));
		MultiVariant betweenWalls = BlockModelGenerators.plainVariant(BELL_BETWEEN_WALLS.create(ArgentBlocks.SILVER_BELL, particle, blockModelGenerators.modelOutput));
		blockModelGenerators.blockStateOutput
			.accept(
				MultiVariantGenerator.dispatch(ArgentBlocks.SILVER_BELL)
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
		itemModelGenerators.generateFlatItem(ArgentItems.RAW_SILVER, ModelTemplates.FLAT_ITEM);
		itemModelGenerators.generateFlatItem(ArgentItems.SILVER_INGOT, ModelTemplates.FLAT_ITEM);
		itemModelGenerators.generateFlatItem(ArgentItems.SILVER_NUGGET, ModelTemplates.FLAT_ITEM);
		itemModelGenerators.generateFlatItem(ArgentItems.SILVER_BELL, ModelTemplates.FLAT_ITEM);
		itemModelGenerators.generateFlatItem(ArgentItems.WITHER_ZOMBIE_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
		itemModelGenerators.generateFlatItem(ArgentItems.SILVER_PARROT_ARMOR, ModelTemplates.FLAT_ITEM);
		createReflectiveShield(itemModelGenerators);
	}
}
