package com.khrux.justsilver.datagen;

import com.khrux.justsilver.config.JustSilverCondition;
import com.khrux.justsilver.world.item.JustSilverItems;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public class JustSilverRecipeProvider extends FabricRecipeProvider {
	private static final List<ItemLike> SILVER_SMELTABLES = List.of(JustSilverItems.SILVER_ORE, JustSilverItems.DEEPSLATE_SILVER_ORE, JustSilverItems.RAW_SILVER);

	public JustSilverRecipeProvider(final FabricPackOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected RecipeProvider createRecipeProvider(
		final HolderLookup.Provider registries, final BootstrapContext<Recipe<?>> recipeOutput, final BootstrapContext<Advancement> advancementOutput
	) {
		return new RecipeProvider(recipeOutput, advancementOutput) {
			@Override
			public void buildRecipes() {
				this.oreSmelting(SILVER_SMELTABLES, RecipeCategory.MISC, CookingBookCategory.MISC, JustSilverItems.SILVER_INGOT, 1.0F, 200, "silver_ingot");
				this.oreBlasting(SILVER_SMELTABLES, RecipeCategory.MISC, CookingBookCategory.MISC, JustSilverItems.SILVER_INGOT, 1.0F, 200, "silver_ingot");
				this.nineBlockStorageRecipesWithCustomPacking(
					RecipeCategory.MISC, JustSilverItems.SILVER_NUGGET, RecipeCategory.MISC, JustSilverItems.SILVER_INGOT, "silver_ingot_from_nuggets", "silver_ingot"
				);
				this.nineBlockStorageRecipesRecipesWithCustomUnpacking(
					RecipeCategory.MISC, JustSilverItems.SILVER_INGOT, RecipeCategory.BUILDING_BLOCKS, JustSilverItems.SILVER_BLOCK, "silver_ingot_from_silver_block", "silver_ingot"
				);
				this.nineBlockStorageRecipes(RecipeCategory.MISC, JustSilverItems.RAW_SILVER, RecipeCategory.BUILDING_BLOCKS, JustSilverItems.RAW_SILVER_BLOCK);
				this.oreSmelting(
					List.of(JustSilverItems.RAW_SILVER_BLOCK), RecipeCategory.BUILDING_BLOCKS, CookingBookCategory.BLOCKS, JustSilverItems.SILVER_BLOCK, 9.0F, 1800, "silver_block"
				);
				this.oreBlasting(
					List.of(JustSilverItems.RAW_SILVER_BLOCK), RecipeCategory.BUILDING_BLOCKS, CookingBookCategory.BLOCKS, JustSilverItems.SILVER_BLOCK, 9.0F, 900, "silver_block"
				);
				this.shaped(RecipeCategory.DECORATIONS, Blocks.SOUL_LANTERN)
					.define('#', Items.SOUL_TORCH)
					.define('X', JustSilverItemTagsProvider.SOUL_LANTERN_NUGGETS)
					.pattern("XXX")
					.pattern("X#X")
					.pattern("XXX")
					.unlockedBy("has_soul_torch", this.has(Items.SOUL_TORCH))
					.save(JustSilverRecipeProvider.this.withConditions(this.output, new JustSilverCondition(JustSilverCondition.Option.SOUL_LANTERN)), "soul_lantern_from_mixed_nuggets");
				this.shaped(RecipeCategory.DECORATIONS, JustSilverItems.SILVER_BELL)
					.define('#', ItemTags.LOGS)
					.define('X', JustSilverItems.SILVER_BLOCK)
					.pattern("###")
					.pattern(" X ")
					.unlockedBy("has_silver_block", this.has(JustSilverItems.SILVER_BLOCK))
					.save(JustSilverRecipeProvider.this.withConditions(this.output, new JustSilverCondition(JustSilverCondition.Option.SILVER_BELL)));
			}
		};
	}

	@Override
	public String getName() {
		return "Recipes";
	}
}
