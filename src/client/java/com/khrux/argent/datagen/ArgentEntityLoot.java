package com.khrux.argent.datagen;

import com.khrux.argent.world.entity.ArgentEntityTypes;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricEntityLootSubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

public class ArgentEntityLoot extends FabricEntityLootSubProvider {
	public ArgentEntityLoot(final FabricPackOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	public void generate() {
		this.add(
			ArgentEntityTypes.WITHER_ZOMBIE,
			LootTable.lootTable()
				.withPool(
					LootPool.lootPool()
						.setRolls(ContextIntProviders.exactly(1))
						.add(
							LootItem.lootTableItem(Items.ROTTEN_FLESH)
								.apply(SetItemCountFunction.setCount(ContextIntProviders.between(0, 2)))
								.apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.enchantments, ContextFloatProviders.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.lootPool()
						.setRolls(ContextIntProviders.exactly(1))
						.add(
							LootItem.lootTableItem(Items.BONE)
								.apply(SetItemCountFunction.setCount(ContextIntProviders.between(0, 1)))
								.apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.enchantments, ContextFloatProviders.between(0.0F, 1.0F)))
						)
				)
		);
	}
}
