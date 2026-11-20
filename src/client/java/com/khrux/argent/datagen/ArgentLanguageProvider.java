package com.khrux.argent.datagen;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

public class ArgentLanguageProvider extends FabricLanguageProvider {
	public ArgentLanguageProvider(final FabricPackOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	public void generateTranslations(final HolderLookup.Provider registries, final FabricLanguageProvider.TranslationBuilder translationBuilder) {
		translationBuilder.add("options.argent.title", "Argent Settings");
		translationBuilder.add("options.argent.open_skin_editor", "Open Skin Editor...");
		translationBuilder.add("options.argent.skin_editor_key", "Editor Key: %s");
		translationBuilder.add("options.argent.mode.enabled", "Enabled");
		translationBuilder.add("options.argent.mode.disabled", "Disabled");
		translationBuilder.add("options.argent.mode.custom", "Custom");
		translationBuilder.add("options.argent.mode.tooltip", "Enabled: as designed, with the best settings. Disabled: switched off. Custom: choose the settings below.");
		translationBuilder.add("options.argent.recipe.tooltip", "Applies after /reload or rejoining. Items already made stay.");
		translationBuilder.add("options.argent.book_source", "Scrying Books");
		translationBuilder.add("options.argent.book_source.tooltip", "Where Scrying enchanted books come from: a shattering wither zombie, rare stronghold library chests, both, or neither.");
		translationBuilder.add("options.argent.book_source.wither_zombie", "Zombies");
		translationBuilder.add("options.argent.book_source.library", "Library");
		translationBuilder.add("options.argent.book_source.both", "Both");
		translationBuilder.add("options.argent.book_source.neither", "Neither");
		translationBuilder.add("options.argent.silver", "Silver");
		translationBuilder.add("options.argent.mirrors", "Mirrors");
		translationBuilder.add("options.argent.spawn_lanterns", "Spawn Lanterns");
		translationBuilder.add("options.argent.wither_zombie", "Wither Zombie");
		translationBuilder.add("options.argent.scrying", "Scrying");
		translationBuilder.add("options.argent.mirror_recipe", "Mirror Recipe");
		translationBuilder.add("options.argent.mirror_recipe.tooltip", "What mirrors are crafted from. Applies after /reload or rejoining.");
		translationBuilder.add("options.argent.mirror_recipe.silver", "Silver");
		translationBuilder.add("options.argent.mirror_recipe.diamond", "Diamond");
	}
}
