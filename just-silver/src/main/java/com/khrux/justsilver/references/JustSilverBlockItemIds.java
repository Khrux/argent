package com.khrux.justsilver.references;

import com.khrux.justsilver.JustSilver;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.Identifier;

public class JustSilverBlockItemIds {
	public static final BlockItemId SILVER_ORE = create("silver_ore");
	public static final BlockItemId DEEPSLATE_SILVER_ORE = create("deepslate_silver_ore");
	public static final BlockItemId SILVER_BLOCK = create("silver_block");
	public static final BlockItemId RAW_SILVER_BLOCK = create("raw_silver_block");
	public static final BlockItemId SILVER_BELL = create("silver_bell");

	private static BlockItemId create(final String name) {
		Identifier id = JustSilver.id(name);
		return BlockItemId.create(id, id);
	}
}
