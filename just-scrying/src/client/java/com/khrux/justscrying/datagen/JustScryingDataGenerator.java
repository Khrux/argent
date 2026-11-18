package com.khrux.justscrying.datagen;

import com.khrux.justscrying.world.item.enchantment.JustScryingEnchantments;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

public class JustScryingDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(final FabricDataGenerator generator) {
		FabricDataGenerator.Pack pack = generator.createPack();
		pack.addProvider(JustScryingModelProvider::new);
		pack.addProvider(JustScryingItemTagsProvider::new);
		pack.addProvider(JustScryingRecipeProvider::new);
		pack.addProvider(JustScryingLanguageProvider::new);
		pack.addProvider(JustScryingWorldgenProvider::new);
	}

	@Override
	public void buildRegistry(final RegistrySetBuilder registryBuilder) {
		registryBuilder.add(Registries.ENCHANTMENT, JustScryingEnchantments::bootstrap);
	}
}
