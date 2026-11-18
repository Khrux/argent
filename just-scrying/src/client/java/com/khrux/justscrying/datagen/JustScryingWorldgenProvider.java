package com.khrux.justscrying.datagen;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;

public class JustScryingWorldgenProvider extends FabricDynamicRegistryProvider {
	public JustScryingWorldgenProvider(final FabricPackOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void configure(final HolderLookup.Provider registries, final FabricDynamicRegistryProvider.Entries entries) {
		entries.addAll(registries.lookupOrThrow(Registries.ENCHANTMENT));
	}

	@Override
	public String getName() {
		return "Worldgen";
	}
}
