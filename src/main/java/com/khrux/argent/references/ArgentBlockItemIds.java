package com.khrux.argent.references;

import com.khrux.argent.Argent;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.Identifier;

public class ArgentBlockItemIds {
	public static final BlockItemId SILVER_ORE = create("silver_ore");
	public static final BlockItemId DEEPSLATE_SILVER_ORE = create("deepslate_silver_ore");
	public static final BlockItemId SILVER_BLOCK = create("silver_block");
	public static final BlockItemId RAW_SILVER_BLOCK = create("raw_silver_block");
	public static final BlockItemId MIRROR = create("mirror");
	public static final BlockItemId SILVER_BELL = create("silver_bell");

	private static BlockItemId create(final String name) {
		Identifier id = Argent.id(name);
		return BlockItemId.create(id, id);
	}
}
