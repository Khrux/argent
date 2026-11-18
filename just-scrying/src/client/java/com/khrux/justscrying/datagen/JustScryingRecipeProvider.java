package com.khrux.justscrying.datagen;

import com.khrux.justscrying.config.JustScryingCondition;
import com.khrux.justscrying.world.item.JustScryingItems;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.Items;

public class JustScryingRecipeProvider extends FabricRecipeProvider {
	public JustScryingRecipeProvider(final FabricPackOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected RecipeProvider createRecipeProvider(
		final HolderLookup.Provider registries, final BootstrapContext<Recipe<?>> recipeOutput, final BootstrapContext<Advancement> advancementOutput
	) {
		return new RecipeProvider(recipeOutput, advancementOutput) {
			@Override
			public void buildRecipes() {
				this.shaped(RecipeCategory.COMBAT, JustScryingItems.PARROT_ARMOR)
					.define('F', Items.FEATHER)
					.define('L', Items.LEATHER)
					.pattern(" F ")
					.pattern(" L ")
					.pattern("LL ")
					.unlockedBy("has_leather", this.has(Items.LEATHER))
					.save(JustScryingRecipeProvider.this.withConditions(this.output, new JustScryingCondition(JustScryingCondition.Option.PARROT_ARMOR)));
				this.dyedItem(JustScryingItems.PARROT_ARMOR, "dyed_armor");
			}
		};
	}

	@Override
	public String getName() {
		return "Recipes";
	}
}
