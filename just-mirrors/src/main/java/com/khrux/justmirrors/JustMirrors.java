package com.khrux.justmirrors;

import com.khrux.justmirrors.config.JustMirrorsCondition;
import com.khrux.justmirrors.config.JustMirrorsConfig;
import com.khrux.justmirrors.network.SkinRelay;
import com.khrux.justmirrors.world.item.JustMirrorsItems;
import com.khrux.justmirrors.world.level.block.JustMirrorsBlocks;
import com.khrux.justmirrors.world.level.block.entity.JustMirrorsBlockEntityTypes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;

public class JustMirrors implements ModInitializer {
	public static final String MOD_ID = "justmirrors";
	private static final String REFLECTIVE_SHIELD = "reflective_shield";

	@Override
	public void onInitialize() {
		JustMirrorsConfig.load();
		ResourceConditions.register(JustMirrorsCondition.TYPE);
		JustMirrorsBlocks.bootstrap();
		JustMirrorsItems.bootstrap();
		JustMirrorsBlockEntityTypes.bootstrap();
		SkinRelay.bootstrap();
		if (!FabricLoader.getInstance().isModLoaded("justwitherzombies")) {
			Identifier shield = BuiltInRegistries.ITEM.getKey(Items.SHIELD);
			BuiltInRegistries.ITEM.addAlias(Identifier.fromNamespaceAndPath("justwitherzombies", REFLECTIVE_SHIELD), shield);
		}

		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS)
			.register(functionalBlocks -> {
				if (JustMirrorsConfig.get().isEnabled()) {
					functionalBlocks.insertAfter(Items.GLOW_ITEM_FRAME, JustMirrorsItems.MIRROR);
				}
			});
	}


	public static Identifier id(final String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
