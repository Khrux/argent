package com.khrux.justmirrors.world.item;

import com.khrux.justmirrors.world.level.block.JustMirrorsBlocks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class JustMirrorsItems {
	public static final Item MIRROR = registerBlock(JustMirrorsBlocks.MIRROR);

	private static Item registerBlock(final Block block) {
		ResourceKey<Item> id = ResourceKey.create(Registries.ITEM, BuiltInRegistries.BLOCK.getKey(block));
		BlockItem item = new BlockItem(block, new Item.Properties().useBlockDescriptionPrefix().setId(id));
		item.registerBlocks(Item.BY_BLOCK, item);
		return Registry.register(BuiltInRegistries.ITEM, id, item);
	}

	public static void bootstrap() {
	}
}
