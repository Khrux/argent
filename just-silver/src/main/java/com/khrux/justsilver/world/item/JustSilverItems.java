package com.khrux.justsilver.world.item;

import com.khrux.justsilver.references.JustSilverBlockItemIds;
import com.khrux.justsilver.references.JustSilverItemIds;
import com.khrux.justsilver.world.level.block.JustSilverBlocks;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class JustSilverItems {
	public static final Item SILVER_ORE = registerBlock(JustSilverBlockItemIds.SILVER_ORE, JustSilverBlocks.SILVER_ORE);
	public static final Item DEEPSLATE_SILVER_ORE = registerBlock(JustSilverBlockItemIds.DEEPSLATE_SILVER_ORE, JustSilverBlocks.DEEPSLATE_SILVER_ORE);
	public static final Item SILVER_BLOCK = registerBlock(JustSilverBlockItemIds.SILVER_BLOCK, JustSilverBlocks.SILVER_BLOCK);
	public static final Item RAW_SILVER_BLOCK = registerBlock(JustSilverBlockItemIds.RAW_SILVER_BLOCK, JustSilverBlocks.RAW_SILVER_BLOCK);
	public static final Item SILVER_BELL = registerBlock(JustSilverBlockItemIds.SILVER_BELL, JustSilverBlocks.SILVER_BELL);
	public static final Item RAW_SILVER = registerItem(JustSilverItemIds.RAW_SILVER);
	public static final Item SILVER_INGOT = registerItem(JustSilverItemIds.SILVER_INGOT);
	public static final Item SILVER_NUGGET = registerItem(JustSilverItemIds.SILVER_NUGGET);

	private static Item registerBlock(final BlockItemId id, final Block block) {
		return register(id.item(), new BlockItem(block, new Item.Properties().useBlockDescriptionPrefix().setId(id.item())));
	}

	private static Item registerItem(final ResourceKey<Item> id) {
		return registerItem(id, Item::new, new Item.Properties());
	}

	private static Item registerItem(final ResourceKey<Item> id, final Function<Item.Properties, Item> itemFactory, final Item.Properties properties) {
		return register(id, itemFactory.apply(properties.setId(id)));
	}

	private static Item register(final ResourceKey<Item> id, final Item item) {
		if (item instanceof BlockItem blockItem) {
			blockItem.registerBlocks(Item.BY_BLOCK, item);
		}

		return Registry.register(BuiltInRegistries.ITEM, id, item);
	}

	public static void bootstrap() {
	}
}
