package com.khrux.justwitherzombies.datagen;

import com.khrux.justwitherzombies.world.entity.JustWitherZombiesEntityTypes;
import com.khrux.justwitherzombies.world.item.JustWitherZombiesItems;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

public class JustWitherZombiesLanguageProvider extends FabricLanguageProvider {
	public JustWitherZombiesLanguageProvider(final FabricPackOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	public void generateTranslations(final HolderLookup.Provider registries, final FabricLanguageProvider.TranslationBuilder translationBuilder) {
		translationBuilder.add(JustWitherZombiesItems.REFLECTIVE_SHIELD, "Reflective Shield");
		translationBuilder.add(JustWitherZombiesEntityTypes.WITHER_ZOMBIE, "Wither Zombie");
		translationBuilder.add(JustWitherZombiesItems.WITHER_ZOMBIE_SPAWN_EGG, "Wither Zombie Spawn Egg");
		translationBuilder.add("subtitles.entity.wither_zombie.ambient", "Wither Zombie groans");
		translationBuilder.add("subtitles.entity.wither_zombie.hurt", "Wither Zombie hurts");
		translationBuilder.add("subtitles.entity.wither_zombie.death", "Wither Zombie dies");
		translationBuilder.add("subtitles.entity.wither_zombie.notice", "Wither Zombie watches you");
		translationBuilder.add("subtitles.entity.wither_zombie.shatter", "Wither Zombie shatters");
		translationBuilder.add("options.justwitherzombies.title", "Just Wither Zombies Settings");
		translationBuilder.add("options.justwitherzombies.melee_damage", "Melee Damage");
		translationBuilder.add("options.justwitherzombies.melee_damage.tooltip", "Whether a wither zombie's swipe from behind hurts you as well as taking things. Off is recommended: it always stays behind you, where you can't look at it, so you can't fight back.");
		translationBuilder.add("options.justwitherzombies.item_stealing", "Item Stealing");
		translationBuilder.add("options.justwitherzombies.item_stealing.tooltip", "Whether wither zombies take food, plants, leather, feathers, wool and eggs from you.");
		translationBuilder.add("options.justwitherzombies.scrying_books", "Scrying Books");
		translationBuilder.add("options.justwitherzombies.scrying_books.tooltip", "A shattering wither zombie drops a Scrying enchanted book.");
	}
}
