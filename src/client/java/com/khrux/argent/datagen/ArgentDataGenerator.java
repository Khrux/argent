package com.khrux.argent.datagen;

import com.khrux.argent.client.renderer.special.ReflectiveShieldSpecialRenderer;
import com.khrux.argent.data.worldgen.features.ArgentOreFeatures;
import com.khrux.argent.data.worldgen.placement.ArgentOrePlacements;
import com.khrux.argent.world.item.enchantment.ArgentEnchantments;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

public class ArgentDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(final FabricDataGenerator generator) {
		ReflectiveShieldSpecialRenderer.bootstrap();
		FabricDataGenerator.Pack pack = generator.createPack();
		pack.addProvider(ArgentModelProvider::new);
		pack.addProvider(MirrorRotatedModelProvider::new);
		pack.addProvider(ArgentBlockLoot::new);
		pack.addProvider(ArgentEntityLoot::new);
		pack.addProvider(ArgentRecipeProvider::new);
		ArgentBlockTagsProvider blockTags = pack.addProvider(ArgentBlockTagsProvider::new);
		pack.addProvider((output, registries) -> new ArgentItemTagsProvider(output, registries, blockTags));
		pack.addProvider(ArgentLanguageProvider::new);
		pack.addProvider(ArgentWorldgenProvider::new);
		pack.addProvider(ArgentSoundsProvider::new);
	}

	@Override
	public void buildRegistry(final RegistrySetBuilder registryBuilder) {
		registryBuilder.add(Registries.FEATURE, ArgentOreFeatures::bootstrap);
		registryBuilder.add(Registries.PLACED_FEATURE, ArgentOrePlacements::bootstrap);
		registryBuilder.add(Registries.ENCHANTMENT, ArgentEnchantments::bootstrap);
	}
}
