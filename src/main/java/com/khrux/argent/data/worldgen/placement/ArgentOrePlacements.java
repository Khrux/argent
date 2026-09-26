package com.khrux.argent.data.worldgen.placement;

import com.khrux.argent.Argent;
import com.khrux.argent.data.worldgen.features.ArgentOreFeatures;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class ArgentOrePlacements {
	public static final ResourceKey<PlacedFeature> ORE_SILVER = ResourceKey.create(Registries.PLACED_FEATURE, Argent.id("ore_silver"));

	public static void bootstrap(final BootstrapContext<PlacedFeature> context) {
		PlacementUtils.register(
			context,
			ORE_SILVER,
			context.lookup(Registries.FEATURE).getOrThrow(ArgentOreFeatures.ORE_SILVER),
			CountPlacement.of(3),
			InSquarePlacement.spread(),
			HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(32)),
			BiomeFilter.biome()
		);
	}
}
