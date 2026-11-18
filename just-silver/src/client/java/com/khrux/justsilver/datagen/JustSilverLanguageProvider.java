package com.khrux.justsilver.datagen;

import com.khrux.justsilver.world.item.JustSilverItems;
import com.khrux.justsilver.world.level.block.JustSilverBlocks;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

public class JustSilverLanguageProvider extends FabricLanguageProvider {
	public JustSilverLanguageProvider(final FabricPackOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	public void generateTranslations(final HolderLookup.Provider registries, final FabricLanguageProvider.TranslationBuilder translationBuilder) {
		translationBuilder.add(JustSilverBlocks.SILVER_ORE, "Silver Ore");
		translationBuilder.add(JustSilverBlocks.DEEPSLATE_SILVER_ORE, "Deepslate Silver Ore");
		translationBuilder.add(JustSilverItems.RAW_SILVER, "Raw Silver");
		translationBuilder.add(JustSilverItems.SILVER_INGOT, "Silver Ingot");
		translationBuilder.add(JustSilverItems.SILVER_NUGGET, "Silver Nugget");
		translationBuilder.add(JustSilverBlocks.SILVER_BLOCK, "Block of Silver");
		translationBuilder.add(JustSilverBlocks.RAW_SILVER_BLOCK, "Block of Raw Silver");
		translationBuilder.add(JustSilverBlocks.SILVER_BELL, "Silver Bell");
		translationBuilder.add("options.justsilver.title", "Just Silver Settings");
		translationBuilder.add("options.justsilver.ore_generation", "Ore Generation");
		translationBuilder.add("options.justsilver.ore_generation.tooltip", "Silver ore generates in newly generated chunks, wherever gold does. Existing silver stays.");
		translationBuilder.add("options.justsilver.silver_bell_recipe", "Silver Bell Recipe");
		translationBuilder.add("options.justsilver.soul_lantern_recipe", "Soul Lantern from Silver Nuggets");
		translationBuilder.add("options.justsilver.recipe.tooltip", "Applies after /reload or rejoining. Items already made stay.");
	}
}
