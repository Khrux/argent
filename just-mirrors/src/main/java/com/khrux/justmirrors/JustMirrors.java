package com.khrux.justmirrors;

import com.khrux.justmirrors.config.JustMirrorsCondition;
import com.khrux.justmirrors.config.JustMirrorsConfig;
import com.khrux.justmirrors.network.SkinRelay;
import com.khrux.justmirrors.world.item.JustMirrorsItems;
import com.khrux.justmirrors.world.level.block.JustMirrorsBlocks;
import com.khrux.justmirrors.world.level.block.entity.JustMirrorsBlockEntityTypes;
import java.util.List;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;

public class JustMirrors implements ModInitializer {
	public static final String MOD_ID = "justmirrors";
	private static final List<String> LEGACY_MOD_IDS = List.of("argent", "mirror");
	private static final String REFLECTIVE_SHIELD = "reflective_shield";

	@Override
	public void onInitialize() {
		JustMirrorsConfig.load();
		ResourceConditions.register(JustMirrorsCondition.TYPE);
		JustMirrorsBlocks.bootstrap();
		JustMirrorsItems.bootstrap();
		JustMirrorsBlockEntityTypes.bootstrap();
		SkinRelay.bootstrap();
		addLegacyAliases(List.of(BuiltInRegistries.BLOCK, BuiltInRegistries.ITEM, BuiltInRegistries.BLOCK_ENTITY_TYPE));
		if (!FabricLoader.getInstance().isModLoaded("justwitherzombies")) {
			Identifier shield = BuiltInRegistries.ITEM.getKey(Items.SHIELD);
			BuiltInRegistries.ITEM.addAlias(Identifier.fromNamespaceAndPath("argent", REFLECTIVE_SHIELD), shield);
			BuiltInRegistries.ITEM.addAlias(Identifier.fromNamespaceAndPath("justwitherzombies", REFLECTIVE_SHIELD), shield);
		}

		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS)
			.register(functionalBlocks -> {
				if (JustMirrorsConfig.get().isEnabled()) {
					functionalBlocks.insertAfter(Items.GLOW_ITEM_FRAME, JustMirrorsItems.MIRROR);
				}
			});
	}

	private static void addLegacyAliases(final List<Registry<?>> registries) {
		for (Registry<?> registry : registries) {
			for (Identifier id : List.copyOf(registry.keySet())) {
				if (!id.getNamespace().equals(MOD_ID)) {
					continue;
				}

				for (String legacyModId : LEGACY_MOD_IDS) {
					registry.addAlias(Identifier.fromNamespaceAndPath(legacyModId, id.getPath()), id);
				}
			}
		}
	}

	public static Identifier id(final String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
