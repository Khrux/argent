package com.khrux.justmirrors.datagen;

import com.khrux.justmirrors.world.item.JustMirrorsItems;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

public class JustMirrorsRecipeProvider extends FabricRecipeProvider {
	public JustMirrorsRecipeProvider(final FabricPackOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected RecipeProvider createRecipeProvider(
		final HolderLookup.Provider registries, final BootstrapContext<Recipe<?>> recipeOutput, final BootstrapContext<Advancement> advancementOutput
	) {
		return new RecipeProvider(recipeOutput, advancementOutput) {
			@Override
			public void buildRecipes() {
				this.shaped(RecipeCategory.DECORATIONS, JustMirrorsItems.MIRROR)
					.define('#', Items.STICK)
					.define('X', Items.DIAMOND)
					.pattern("###")
					.pattern("#X#")
					.pattern("###")
					.unlockedBy("has_diamond", this.has(Items.DIAMOND))
					.save(JustMirrorsRecipeProvider.this.withConditions(this.output, ResourceConditions.not(ResourceConditions.allModsLoaded("argent"))));
			}
		};
	}

	@Override
	public String getName() {
		return "Recipes";
	}
}
