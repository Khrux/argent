package com.khrux.justscrying.datagen;

import com.khrux.justscrying.world.item.JustScryingItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;

public class JustScryingModelProvider extends FabricModelProvider {
	public JustScryingModelProvider(final FabricPackOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(final BlockModelGenerators blockModelGenerators) {
	}

	@Override
	public void generateItemModels(final ItemModelGenerators itemModelGenerators) {
		itemModelGenerators.generateFlatItem(JustScryingItems.PARROT_ARMOR, ModelTemplates.FLAT_ITEM);
	}
}
