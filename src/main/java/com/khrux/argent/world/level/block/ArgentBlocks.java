package com.khrux.argent.world.level.block;

import com.khrux.argent.references.ArgentBlockItemIds;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.references.BlockItemId;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class ArgentBlocks {
	public static final Block SILVER_ORE = register(
		ArgentBlockItemIds.SILVER_ORE,
		p -> new DropExperienceBlock(ConstantInt.of(0), p),
		BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 3.0F)
	);
	public static final Block DEEPSLATE_SILVER_ORE = register(
		ArgentBlockItemIds.DEEPSLATE_SILVER_ORE,
		p -> new DropExperienceBlock(ConstantInt.of(0), p),
		BlockBehaviour.Properties.ofLegacyCopy(SILVER_ORE).mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE)
	);
	public static final Block SILVER_BLOCK = register(
		ArgentBlockItemIds.SILVER_BLOCK,
		Block::new,
		BlockBehaviour.Properties.of()
			.mapColor(MapColor.METAL)
			.instrument(NoteBlockInstrument.BELL)
			.requiresCorrectToolForDrops()
			.strength(3.0F, 6.0F)
			.sound(SoundType.METAL)
	);
	public static final Block RAW_SILVER_BLOCK = register(
		ArgentBlockItemIds.RAW_SILVER_BLOCK,
		Block::new,
		BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(5.0F, 6.0F)
	);
	public static final Block MIRROR = register(
		ArgentBlockItemIds.MIRROR,
		MirrorBlock::new,
		BlockBehaviour.Properties.of()
			.mapColor(MapColor.WOOD)
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.5F)
			.sound(SoundType.WOOD)
			.noOcclusion()
			.ignitedByLava()
	);
	public static final Block SILVER_BELL = register(
		ArgentBlockItemIds.SILVER_BELL,
		SilverBellBlock::new,
		BlockBehaviour.Properties.of().mapColor(MapColor.METAL).forceSolidOn().strength(5.0F).sound(SoundType.ANVIL).pushReaction(PushReaction.POPPED)
	);

	private static Block register(final BlockItemId id, final Function<BlockBehaviour.Properties, Block> factory, final BlockBehaviour.Properties properties) {
		Block block = factory.apply(properties.setId(id.block()));
		return Registry.register(BuiltInRegistries.BLOCK, id.block(), block);
	}

	public static void bootstrap() {
	}
}
