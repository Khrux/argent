package com.khrux.argent.data.worldgen.features;

import com.khrux.argent.Argent;
import com.khrux.argent.world.level.block.ArgentBlocks;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.feature.BlockReplacement;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.HeightMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

public class ArgentOreFeatures {
	public static final ResourceKey<Feature> ORE_SILVER = ResourceKey.create(Registries.FEATURE, Argent.id("ore_silver"));

	public static void bootstrap(final BootstrapContext<Feature> context) {
		RuleTest stoneOreReplaceables = RuleTest.either(
			new TagMatchTest(BlockTags.HEIGHT_SPECIFIC_ORE_REPLACEABLES), HeightMatchTest.min(0), new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES)
		);
		RuleTest deepslateOreReplaceables = RuleTest.either(
			new TagMatchTest(BlockTags.HEIGHT_SPECIFIC_ORE_REPLACEABLES), HeightMatchTest.max(8), new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES)
		);
		List<BlockReplacement> oreSilverTargetList = List.of(
			BlockReplacement.replace(stoneOreReplaceables, ArgentBlocks.SILVER_ORE.defaultBlockState()),
			BlockReplacement.replace(deepslateOreReplaceables, ArgentBlocks.DEEPSLATE_SILVER_ORE.defaultBlockState())
		);
		context.register(ORE_SILVER, new OreFeature(oreSilverTargetList, 7, 0.5F));
	}
}
