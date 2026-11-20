package com.khrux.argent.config;

import com.khrux.argent.Argent;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.minecraft.resources.RegistryOps;

public record ConfigCondition(ArgentConfig.MirrorRecipe mirrorRecipe) implements ResourceCondition {
	public static final MapCodec<ConfigCondition> CODEC = ArgentConfig.MirrorRecipe.CODEC
		.fieldOf("mirror_recipe")
		.xmap(ConfigCondition::new, ConfigCondition::mirrorRecipe);
	public static final ResourceConditionType<ConfigCondition> TYPE = ResourceConditionType.create(Argent.id("config"), CODEC);

	@Override
	public ResourceConditionType<?> getType() {
		return TYPE;
	}

	@Override
	public boolean test(final RegistryOps.RegistryInfoLookup registryInfo) {
		return ArgentConfig.get().usesMirrorRecipe(this.mirrorRecipe);
	}
}
