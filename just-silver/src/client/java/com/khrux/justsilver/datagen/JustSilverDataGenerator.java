package com.khrux.justsilver.datagen;

import com.khrux.justsilver.data.worldgen.features.JustSilverOreFeatures;
import com.khrux.justsilver.data.worldgen.placement.JustSilverOrePlacements;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

public class JustSilverDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(final FabricDataGenerator generator) {
		FabricDataGenerator.Pack pack = generator.createPack();
		pack.addProvider(JustSilverModelProvider::new);
		pack.addProvider(JustSilverBlockLoot::new);
		pack.addProvider(JustSilverRecipeProvider::new);
		JustSilverBlockTagsProvider blockTags = pack.addProvider(JustSilverBlockTagsProvider::new);
		pack.addProvider((output, registries) -> new JustSilverItemTagsProvider(output, registries, blockTags));
		pack.addProvider(JustSilverLanguageProvider::new);
		pack.addProvider(JustSilverWorldgenProvider::new);
	}

	@Override
	public void buildRegistry(final RegistrySetBuilder registryBuilder) {
		registryBuilder.add(Registries.FEATURE, JustSilverOreFeatures::bootstrap);
		registryBuilder.add(Registries.PLACED_FEATURE, JustSilverOrePlacements::bootstrap);
	}
}
