package com.khrux.justspawnlanterns.datagen;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

public class JustSpawnLanternsLanguageProvider extends FabricLanguageProvider {
	public JustSpawnLanternsLanguageProvider(final FabricPackOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	public void generateTranslations(final HolderLookup.Provider registries, final FabricLanguageProvider.TranslationBuilder translationBuilder) {
		translationBuilder.add("options.justspawnlanterns.title", "Just Spawn Lanterns Settings");
		translationBuilder.add("options.justspawnlanterns.markers", "Markers");
		translationBuilder.add("options.justspawnlanterns.markers.tooltip", "Where a soul lantern must be for soul particles to rise where monsters can spawn.");
		translationBuilder.add("options.justspawnlanterns.markers.off", "Off");
		translationBuilder.add("options.justspawnlanterns.markers.hand", "In Hand");
		translationBuilder.add("options.justspawnlanterns.markers.inventory", "In Inventory");
	}
}
