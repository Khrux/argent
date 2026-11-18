package com.khrux.argent.datagen;

import com.khrux.argent.config.ArgentConfig;
import com.khrux.argent.config.ConfigCondition;
import com.khrux.justmirrors.world.item.JustMirrorsItems;
import com.khrux.justsilver.world.item.JustSilverItems;
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

public class ArgentRecipeProvider extends FabricRecipeProvider {
	public ArgentRecipeProvider(final FabricPackOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
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
					.define('X', JustSilverItems.SILVER_INGOT)
					.pattern("###")
					.pattern("#X#")
					.pattern("###")
					.unlockedBy("has_silver_ingot", this.has(JustSilverItems.SILVER_INGOT))
					.save(ArgentRecipeProvider.this.withConditions(this.output, new ConfigCondition(ArgentConfig.MirrorRecipe.SILVER)), "argent:mirror_from_silver");
				this.shaped(RecipeCategory.DECORATIONS, JustMirrorsItems.MIRROR)
					.define('#', Items.STICK)
					.define('X', Items.DIAMOND)
					.pattern("###")
					.pattern("#X#")
					.pattern("###")
					.unlockedBy("has_diamond", this.has(Items.DIAMOND))
					.save(ArgentRecipeProvider.this.withConditions(this.output, new ConfigCondition(ArgentConfig.MirrorRecipe.DIAMOND)), "argent:mirror_from_diamond");
			}
		};
	}

	@Override
	public String getName() {
		return "Recipes";
	}
}
