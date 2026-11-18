package com.khrux.justsilver;

import com.khrux.justsilver.config.JustSilverCondition;
import com.khrux.justsilver.config.JustSilverConfig;
import com.khrux.justsilver.data.worldgen.placement.JustSilverOrePlacements;
import com.khrux.justsilver.world.item.JustSilverItems;
import com.khrux.justsilver.world.level.block.JustSilverBlocks;
import com.khrux.justsilver.world.level.block.entity.JustSilverBlockEntityTypes;
import java.util.List;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.GenerationStep;

public class JustSilver implements ModInitializer {
	public static final String MOD_ID = "justsilver";
	private static final List<String> LEGACY_MOD_IDS = List.of("argent", "mirror");

	@Override
	public void onInitialize() {
		JustSilverConfig.load();
		ResourceConditions.register(JustSilverCondition.TYPE);
		JustSilverBlocks.bootstrap();
		JustSilverItems.bootstrap();
		JustSilverBlockEntityTypes.bootstrap();
		addLegacyAliases(List.of(BuiltInRegistries.BLOCK, BuiltInRegistries.ITEM, BuiltInRegistries.BLOCK_ENTITY_TYPE));
		BiomeModifications.addFeature(
			context -> JustSilverConfig.get().generatesOre() && context.hasPlacedFeature(OrePlacements.ORE_GOLD),
			GenerationStep.Decoration.UNDERGROUND_ORES,
			JustSilverOrePlacements.ORE_SILVER
		);
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS)
			.register(naturalBlocks -> {
				if (!JustSilverConfig.get().isEnabled()) {
					return;
				}

				naturalBlocks.insertAfter(Items.DEEPSLATE_GOLD_ORE, JustSilverItems.SILVER_ORE, JustSilverItems.DEEPSLATE_SILVER_ORE);
				naturalBlocks.insertAfter(Items.RAW_GOLD_BLOCK, JustSilverItems.RAW_SILVER_BLOCK);
			});
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS)
			.register(buildingBlocks -> {
				if (JustSilverConfig.get().isEnabled()) {
					buildingBlocks.insertAfter(Items.GOLD_BLOCK, JustSilverItems.SILVER_BLOCK);
				}
			});
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(ingredients -> {
			if (!JustSilverConfig.get().isEnabled()) {
				return;
			}

			ingredients.insertAfter(Items.RAW_GOLD, JustSilverItems.RAW_SILVER);
			ingredients.insertAfter(Items.GOLD_INGOT, JustSilverItems.SILVER_INGOT);
			ingredients.insertAfter(Items.GOLD_NUGGET, JustSilverItems.SILVER_NUGGET);
		});
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS)
			.register(functionalBlocks -> {
				if (JustSilverConfig.get().isEnabled()) {
					functionalBlocks.insertAfter(Items.BELL, JustSilverItems.SILVER_BELL);
				}
			});
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.REDSTONE_BLOCKS)
			.register(redstoneBlocks -> {
				if (JustSilverConfig.get().isEnabled()) {
					redstoneBlocks.insertAfter(Items.BELL, JustSilverItems.SILVER_BELL);
				}
			});
	}

	private static void addLegacyAliases(final List<Registry<?>> registries) {
		for (Registry<?> registry : registries) {
			for (Identifier id : List.copyOf(registry.keySet())) {
				if (!id.getNamespace().equals(MOD_ID)) {
					continue;
				}

				for (String legacyModId : LEGACY_MOD_IDS) {
					registry.addAlias(Identifier.fromNamespaceAndPath(legacyModId, id.getPath()), id);
				}
			}
		}
	}

	public static Identifier id(final String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
