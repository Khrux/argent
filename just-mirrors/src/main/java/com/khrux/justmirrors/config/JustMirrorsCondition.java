package com.khrux.justmirrors.config;

import com.khrux.justmirrors.JustMirrors;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.minecraft.resources.RegistryOps;

public record JustMirrorsCondition() implements ResourceCondition {
	public static final JustMirrorsCondition INSTANCE = new JustMirrorsCondition();
	public static final MapCodec<JustMirrorsCondition> CODEC = MapCodec.unit(INSTANCE);
	public static final ResourceConditionType<JustMirrorsCondition> TYPE = ResourceConditionType.create(JustMirrors.id("enabled"), CODEC);

	@Override
	public ResourceConditionType<?> getType() {
		return TYPE;
	}

	@Override
	public boolean test(final RegistryOps.RegistryInfoLookup registryInfo) {
		return JustMirrorsConfig.get().isEnabled();
	}
}
