package com.khrux.justscrying;

import com.khrux.justscrying.config.JustScryingConfig;
import com.khrux.justscrying.world.entity.animal.ParrotArmor;
import com.khrux.justscrying.world.entity.animal.Scrying;
import com.khrux.justscrying.world.item.JustScryingItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;

public class JustScrying implements ModInitializer {
	public static final String MOD_ID = "justscrying";

	@Override
	public void onInitialize() {
		JustScryingConfig.load();
		JustScryingItems.bootstrap();
		BuiltInRegistries.ITEM.addAlias(Identifier.fromNamespaceAndPath("argent", "silver_parrot_armor"), BuiltInRegistries.ITEM.getKey(JustScryingItems.PARROT_ARMOR));
		UseEntityCallback.EVENT.register(ParrotArmor::interact);
		Scrying.bootstrap();
		ServerLivingEntityEvents.ALLOW_DAMAGE.register(ParrotArmor::allowDamage);
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
			.register(combat -> {
				if (JustScryingConfig.get().isEnabled()) {
					combat.insertAfter(Items.WOLF_ARMOR, JustScryingItems.PARROT_ARMOR);
				}
			});
	}

	public static Identifier id(final String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
