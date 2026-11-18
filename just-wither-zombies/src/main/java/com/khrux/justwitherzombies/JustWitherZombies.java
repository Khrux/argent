package com.khrux.justwitherzombies;

import com.khrux.justwitherzombies.config.JustWitherZombiesConfig;
import com.khrux.justwitherzombies.sounds.JustWitherZombiesSoundEvents;
import com.khrux.justwitherzombies.world.entity.JustWitherZombiesEntityTypes;
import com.khrux.justwitherzombies.world.item.JustWitherZombiesItems;
import java.util.List;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biomes;

public class JustWitherZombies implements ModInitializer {
	public static final String MOD_ID = "justwitherzombies";
	public static final ResourceKey<Enchantment> SCRYING = ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath("justscrying", "scrying"));

	@Override
	public void onInitialize() {
		JustWitherZombiesConfig.load();
		JustWitherZombiesSoundEvents.bootstrap();
		JustWitherZombiesEntityTypes.bootstrap();
		JustWitherZombiesItems.bootstrap();
		addLegacyAliases(List.of(BuiltInRegistries.ITEM, BuiltInRegistries.ENTITY_TYPE, BuiltInRegistries.SOUND_EVENT));
		BiomeModifications.addSpawn(
			BiomeSelectors.includeByKey(Biomes.NETHER_WASTES, Biomes.CRIMSON_FOREST, Biomes.WARPED_FOREST, Biomes.BASALT_DELTAS)
				.and(context -> JustWitherZombiesConfig.get().isEnabled()),
			MobCategory.MONSTER,
			JustWitherZombiesEntityTypes.WITHER_ZOMBIE,
			1,
			1,
			1
		);
		BiomeModifications.addSpawn(
			BiomeSelectors.includeByKey(Biomes.SOUL_SAND_VALLEY).and(context -> JustWitherZombiesConfig.get().isEnabled()),
			MobCategory.MONSTER,
			JustWitherZombiesEntityTypes.WITHER_ZOMBIE,
			2,
			1,
			1
		);
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT).register(combat -> combat.insertAfter(Items.SHIELD, JustWitherZombiesItems.REFLECTIVE_SHIELD));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS)
			.register(spawnEggs -> spawnEggs.insertAfter(Items.WITHER_SKELETON_SPAWN_EGG, JustWitherZombiesItems.WITHER_ZOMBIE_SPAWN_EGG));
	}

	private static void addLegacyAliases(final List<Registry<?>> registries) {
		for (Registry<?> registry : registries) {
			for (Identifier id : List.copyOf(registry.keySet())) {
				if (id.getNamespace().equals(MOD_ID)) {
					registry.addAlias(Identifier.fromNamespaceAndPath("argent", id.getPath()), id);
				}
			}
		}
	}

	public static Identifier id(final String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
