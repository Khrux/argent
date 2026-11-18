package com.khrux.argent;

import com.khrux.argent.config.ArgentConfig;
import com.khrux.argent.config.ConfigCondition;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.resources.Identifier;

public class Argent implements ModInitializer {
	public static final String MOD_ID = "argent";

	@Override
	public void onInitialize() {
		ArgentConfig.load();
		ResourceConditions.register(ConfigCondition.TYPE);
	}

	public static Identifier id(final String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
