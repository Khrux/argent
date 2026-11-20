package com.khrux.justscrying.datagen;

import com.khrux.justscrying.world.item.JustScryingItems;
import com.khrux.justscrying.world.item.enchantment.JustScryingEnchantments;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.util.Util;

public class JustScryingLanguageProvider extends FabricLanguageProvider {
	public JustScryingLanguageProvider(final FabricPackOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	public void generateTranslations(final HolderLookup.Provider registries, final FabricLanguageProvider.TranslationBuilder translationBuilder) {
		translationBuilder.add(Util.makeDescriptionId("enchantment", JustScryingEnchantments.SCRYING.identifier()), "Scrying");
		translationBuilder.add(JustScryingItems.PARROT_ARMOR, "Parrot Armor");
		translationBuilder.add(JustScryingItems.CAT_ARMOR, "Cat Armor");
		translationBuilder.add("options.justscrying.title", "Just Scrying Settings");
		translationBuilder.add("options.justscrying.scrying", "Scrying");
		translationBuilder.add("options.justscrying.scrying.tooltip", "Whether sneaking and using an empty hand on your pet wearing Scrying armour lets you see and move through it.");
		translationBuilder.add("options.justscrying.library_books", "Scrying in Libraries");
		translationBuilder.add("options.justscrying.library_books.tooltip", "A Scrying book can rarely turn up in stronghold library chests. Applies to chests opened after /reload or rejoining.");
		translationBuilder.add("options.justscrying.fishing_books", "Scrying from Fishing");
		translationBuilder.add("options.justscrying.fishing_books.tooltip", "A Scrying book can turn up as fishing treasure, as often as any other treasure. Applies after /reload or rejoining.");
		translationBuilder.add("options.justscrying.elder_guardian_book", "Elder Guardian Book");
		translationBuilder.add("options.justscrying.elder_guardian_book.tooltip", "Every elder guardian drops a Scrying book when it dies. Applies after /reload or rejoining.");
		translationBuilder.add("options.justscrying.parrot_armor_recipe", "Parrot Armor Recipe");
		translationBuilder.add("options.justscrying.cat_armor_recipe", "Cat Armor Recipe");
		translationBuilder.add("options.justscrying.recipe.tooltip", "Applies after /reload or rejoining. Items already made stay.");
	}
}
