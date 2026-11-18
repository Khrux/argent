package com.khrux.justwitherzombies.datagen;

import com.khrux.justwitherzombies.client.renderer.special.ReflectiveShieldSpecialRenderer;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class JustWitherZombiesDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(final FabricDataGenerator generator) {
		ReflectiveShieldSpecialRenderer.bootstrap();
		FabricDataGenerator.Pack pack = generator.createPack();
		pack.addProvider(JustWitherZombiesModelProvider::new);
		pack.addProvider(JustWitherZombiesEntityLoot::new);
		pack.addProvider(JustWitherZombiesItemTagsProvider::new);
		pack.addProvider(JustWitherZombiesRecipeProvider::new);
		pack.addProvider(JustWitherZombiesLanguageProvider::new);
		pack.addProvider(JustWitherZombiesSoundsProvider::new);
	}
}
