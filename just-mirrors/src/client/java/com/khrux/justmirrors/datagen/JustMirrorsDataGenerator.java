package com.khrux.justmirrors.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class JustMirrorsDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(final FabricDataGenerator generator) {
		FabricDataGenerator.Pack pack = generator.createPack();
		pack.addProvider(JustMirrorsModelProvider::new);
		pack.addProvider(MirrorRotatedModelProvider::new);
		pack.addProvider(JustMirrorsBlockLoot::new);
		pack.addProvider(JustMirrorsBlockTagsProvider::new);
		pack.addProvider(JustMirrorsRecipeProvider::new);
		pack.addProvider(JustMirrorsLanguageProvider::new);
	}
}
