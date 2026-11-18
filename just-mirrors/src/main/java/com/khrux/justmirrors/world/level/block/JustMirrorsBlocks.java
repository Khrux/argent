package com.khrux.justmirrors.world.level.block;

import com.khrux.justmirrors.JustMirrors;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public class JustMirrorsBlocks {
	public static final ResourceKey<Block> MIRROR_ID = ResourceKey.create(Registries.BLOCK, JustMirrors.id("mirror"));
	public static final Block MIRROR = Registry.register(
		BuiltInRegistries.BLOCK,
		MIRROR_ID,
		new MirrorBlock(
			BlockBehaviour.Properties.of()
				.mapColor(MapColor.WOOD)
				.instrument(NoteBlockInstrument.BASS)
				.strength(2.5F)
				.sound(SoundType.WOOD)
				.noOcclusion()
				.ignitedByLava()
				.setId(MIRROR_ID)
		)
	);

	public static void bootstrap() {
	}
}
