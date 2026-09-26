package com.khrux.argent.datagen;

import com.khrux.argent.config.ConfigCondition;
import com.khrux.argent.world.item.ArgentItems;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.TransmuteRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public class ArgentRecipeProvider extends FabricRecipeProvider {
	private static final List<ItemLike> SILVER_SMELTABLES = List.of(ArgentItems.SILVER_ORE, ArgentItems.DEEPSLATE_SILVER_ORE, ArgentItems.RAW_SILVER);

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
				this.oreSmelting(SILVER_SMELTABLES, RecipeCategory.MISC, CookingBookCategory.MISC, ArgentItems.SILVER_INGOT, 1.0F, 200, "silver_ingot");
				this.oreBlasting(SILVER_SMELTABLES, RecipeCategory.MISC, CookingBookCategory.MISC, ArgentItems.SILVER_INGOT, 1.0F, 200, "silver_ingot");
				this.nineBlockStorageRecipesWithCustomPacking(
					RecipeCategory.MISC, ArgentItems.SILVER_NUGGET, RecipeCategory.MISC, ArgentItems.SILVER_INGOT, "silver_ingot_from_nuggets", "silver_ingot"
				);
				this.nineBlockStorageRecipesRecipesWithCustomUnpacking(
					RecipeCategory.MISC, ArgentItems.SILVER_INGOT, RecipeCategory.BUILDING_BLOCKS, ArgentItems.SILVER_BLOCK, "silver_ingot_from_silver_block", "silver_ingot"
				);
				this.nineBlockStorageRecipes(RecipeCategory.MISC, ArgentItems.RAW_SILVER, RecipeCategory.BUILDING_BLOCKS, ArgentItems.RAW_SILVER_BLOCK);
				this.oreSmelting(
					List.of(ArgentItems.RAW_SILVER_BLOCK), RecipeCategory.BUILDING_BLOCKS, CookingBookCategory.BLOCKS, ArgentItems.SILVER_BLOCK, 9.0F, 1800, "silver_block"
				);
				this.oreBlasting(
					List.of(ArgentItems.RAW_SILVER_BLOCK), RecipeCategory.BUILDING_BLOCKS, CookingBookCategory.BLOCKS, ArgentItems.SILVER_BLOCK, 9.0F, 900, "silver_block"
				);
				this.shaped(RecipeCategory.DECORATIONS, Blocks.SOUL_LANTERN)
					.define('#', Items.SOUL_TORCH)
					.define('X', ArgentItemTagsProvider.SOUL_LANTERN_NUGGETS)
					.pattern("XXX")
					.pattern("X#X")
					.pattern("XXX")
					.unlockedBy("has_soul_torch", this.has(Items.SOUL_TORCH))
					.save(ArgentRecipeProvider.this.withConditions(this.output, new ConfigCondition(ConfigCondition.Option.SOUL_LANTERN)), "soul_lantern_from_mixed_nuggets");
				this.shaped(RecipeCategory.DECORATIONS, ArgentItems.SILVER_BELL)
					.define('#', ItemTags.LOGS)
					.define('X', ArgentItems.SILVER_BLOCK)
					.pattern("###")
					.pattern(" X ")
					.unlockedBy("has_silver_block", this.has(ArgentItems.SILVER_BLOCK))
					.save(this.output);
				this.shaped(RecipeCategory.COMBAT, ArgentItems.SILVER_PARROT_ARMOR)
					.define('#', ArgentItems.SILVER_INGOT)
					.define('X', Items.WIND_CHARGE)
					.pattern(" ##")
					.pattern(" X ")
					.pattern("## ")
					.unlockedBy("has_silver_ingot", this.has(ArgentItems.SILVER_INGOT))
					.save(this.output);
				TransmuteRecipeBuilder.transmute(RecipeCategory.COMBAT, Ingredient.of(Items.SHIELD), Ingredient.of(ArgentItems.MIRROR), ArgentItems.REFLECTIVE_SHIELD)
					.unlockedBy("has_mirror", this.has(ArgentItems.MIRROR))
					.save(ArgentRecipeProvider.this.withConditions(this.output, new ConfigCondition(ConfigCondition.Option.MIRRORS)), "reflective_shield");
				this.shaped(RecipeCategory.DECORATIONS, ArgentItems.MIRROR)
					.define('#', Items.STICK)
					.define('X', ArgentItems.SILVER_INGOT)
					.pattern("###")
					.pattern("#X#")
					.pattern("###")
					.unlockedBy("has_silver_ingot", this.has(ArgentItems.SILVER_INGOT))
					.save(ArgentRecipeProvider.this.withConditions(this.output, new ConfigCondition(ConfigCondition.Option.SILVER_MIRROR)));
				this.shaped(RecipeCategory.DECORATIONS, ArgentItems.MIRROR)
					.define('#', Items.STICK)
					.define('X', Items.DIAMOND)
					.pattern("###")
					.pattern("#X#")
					.pattern("###")
					.unlockedBy("has_diamond", this.has(Items.DIAMOND))
					.save(ArgentRecipeProvider.this.withConditions(this.output, new ConfigCondition(ConfigCondition.Option.DIAMOND_MIRROR)), "mirror_from_diamond");
			}
		};
	}

	@Override
	public String getName() {
		return "Recipes";
	}
}
