package com.khrux.justscrying.datagen;

import com.khrux.justscrying.world.item.JustScryingItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.world.item.component.DyedItemColor;

public class JustScryingModelProvider extends FabricModelProvider {
	public JustScryingModelProvider(final FabricPackOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(final BlockModelGenerators blockModelGenerators) {
	}

	@Override
	public void generateItemModels(final ItemModelGenerators itemModelGenerators) {
		itemModelGenerators.generateDyedItem(JustScryingItems.PARROT_ARMOR, DyedItemColor.LEATHER_COLOR);
		itemModelGenerators.generateTwoLayerDyedItem(JustScryingItems.CAT_ARMOR);
	}
}
