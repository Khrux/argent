package com.khrux.argent;

import com.khrux.argent.config.ArgentConfig;
import com.khrux.argent.config.ConfigCondition;
import com.khrux.argent.data.worldgen.placement.ArgentOrePlacements;
import com.khrux.argent.network.SkinRelay;
import com.khrux.argent.sounds.ArgentSoundEvents;
import com.khrux.argent.world.entity.ArgentEntityTypes;
import com.khrux.argent.world.entity.animal.ParrotArmor;
import com.khrux.argent.world.entity.animal.Scrying;
import com.khrux.argent.world.item.ArgentItems;
import com.khrux.argent.world.level.block.ArgentBlocks;
import com.khrux.argent.world.level.block.entity.ArgentBlockEntityTypes;
import java.util.List;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;

public class Argent implements ModInitializer {
	public static final String MOD_ID = "argent";
	private static final String LEGACY_MOD_ID = "mirror";

	@Override
	public void onInitialize() {
		ArgentConfig.load();
		ResourceConditions.register(ConfigCondition.TYPE);
		ArgentSoundEvents.bootstrap();
		ArgentBlocks.bootstrap();
		ArgentEntityTypes.bootstrap();
		ArgentItems.bootstrap();
		ArgentBlockEntityTypes.bootstrap();
		SkinRelay.bootstrap();
		addLegacyAliases(List.of(
			BuiltInRegistries.BLOCK, BuiltInRegistries.ITEM, BuiltInRegistries.BLOCK_ENTITY_TYPE, BuiltInRegistries.ENTITY_TYPE, BuiltInRegistries.SOUND_EVENT
		));
		UseEntityCallback.EVENT.register(ParrotArmor::interact);
		Scrying.bootstrap();
		ServerLivingEntityEvents.ALLOW_DAMAGE.register(ParrotArmor::allowDamage);
		BiomeModifications.addFeature(
			context -> ArgentConfig.get().silver && context.hasPlacedFeature(OrePlacements.ORE_GOLD),
			GenerationStep.Decoration.UNDERGROUND_ORES,
			ArgentOrePlacements.ORE_SILVER
		);
		BiomeModifications.addSpawn(
			BiomeSelectors.includeByKey(Biomes.NETHER_WASTES, Biomes.CRIMSON_FOREST, Biomes.WARPED_FOREST, Biomes.BASALT_DELTAS)
				.and(context -> ArgentConfig.get().witherZombies),
			MobCategory.MONSTER,
			ArgentEntityTypes.WITHER_ZOMBIE,
			1,
			1,
			1
		);
		BiomeModifications.addSpawn(
			BiomeSelectors.includeByKey(Biomes.SOUL_SAND_VALLEY).and(context -> ArgentConfig.get().witherZombies),
			MobCategory.MONSTER,
			ArgentEntityTypes.WITHER_ZOMBIE,
			2,
			1,
			1
		);
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS)
			.register(naturalBlocks -> {
				if (!ArgentConfig.get().silver) {
					return;
				}

				naturalBlocks.insertAfter(Items.DEEPSLATE_GOLD_ORE, ArgentItems.SILVER_ORE, ArgentItems.DEEPSLATE_SILVER_ORE);
				naturalBlocks.insertAfter(Items.RAW_GOLD_BLOCK, ArgentItems.RAW_SILVER_BLOCK);
			});
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS)
			.register(buildingBlocks -> {
				if (ArgentConfig.get().silver) {
					buildingBlocks.insertAfter(Items.GOLD_BLOCK, ArgentItems.SILVER_BLOCK);
				}
			});
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(ingredients -> {
			if (!ArgentConfig.get().silver) {
				return;
			}

			ingredients.insertAfter(Items.RAW_GOLD, ArgentItems.RAW_SILVER);
			ingredients.insertAfter(Items.GOLD_INGOT, ArgentItems.SILVER_INGOT);
			ingredients.insertAfter(Items.GOLD_NUGGET, ArgentItems.SILVER_NUGGET);
		});
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS)
			.register(functionalBlocks -> {
				if (ArgentConfig.get().mirrors()) {
					functionalBlocks.insertAfter(Items.GLOW_ITEM_FRAME, ArgentItems.MIRROR);
				}

				if (ArgentConfig.get().silver) {
					functionalBlocks.insertAfter(Items.BELL, ArgentItems.SILVER_BELL);
				}
			});
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
			.register(combat -> {
				if (ArgentConfig.get().mirrors()) {
					combat.insertAfter(Items.SHIELD, ArgentItems.REFLECTIVE_SHIELD);
				}

				if (ArgentConfig.get().silver) {
					combat.insertAfter(Items.WOLF_ARMOR, ArgentItems.SILVER_PARROT_ARMOR);
				}
			});
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.REDSTONE_BLOCKS)
			.register(redstoneBlocks -> {
				if (ArgentConfig.get().silver) {
					redstoneBlocks.insertAfter(Items.BELL, ArgentItems.SILVER_BELL);
				}
			});
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS)
			.register(spawnEggs -> spawnEggs.insertAfter(Items.WITHER_SKELETON_SPAWN_EGG, ArgentItems.WITHER_ZOMBIE_SPAWN_EGG));
	}

	private static void addLegacyAliases(final List<Registry<?>> registries) {
		for (Registry<?> registry : registries) {
			for (Identifier id : List.copyOf(registry.keySet())) {
				if (id.getNamespace().equals(MOD_ID)) {
					registry.addAlias(Identifier.fromNamespaceAndPath(LEGACY_MOD_ID, id.getPath()), id);
				}
			}
		}
	}

	public static Identifier id(final String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
