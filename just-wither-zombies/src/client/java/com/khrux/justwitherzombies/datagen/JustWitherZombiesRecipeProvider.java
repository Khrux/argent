package com.khrux.justwitherzombies.datagen;

import com.khrux.justmirrors.config.JustMirrorsCondition;
import com.khrux.justmirrors.world.item.JustMirrorsItems;
import com.khrux.justwitherzombies.world.item.JustWitherZombiesItems;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.TransmuteRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public class JustWitherZombiesRecipeProvider extends FabricRecipeProvider {
	public JustWitherZombiesRecipeProvider(final FabricPackOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected RecipeProvider createRecipeProvider(
		final HolderLookup.Provider registries, final BootstrapContext<Recipe<?>> recipeOutput, final BootstrapContext<Advancement> advancementOutput
	) {
		return new RecipeProvider(recipeOutput, advancementOutput) {
			@Override
			public void buildRecipes() {
				TransmuteRecipeBuilder.transmute(
						RecipeCategory.COMBAT, Ingredient.of(Items.SHIELD), Ingredient.of(JustMirrorsItems.MIRROR), JustWitherZombiesItems.REFLECTIVE_SHIELD
					)
					.unlockedBy("has_mirror", this.has(JustMirrorsItems.MIRROR))
					.save(JustWitherZombiesRecipeProvider.this.withConditions(this.output, JustMirrorsCondition.INSTANCE), "reflective_shield");
			}
		};
	}

	@Override
	public String getName() {
		return "Recipes";
	}
}
