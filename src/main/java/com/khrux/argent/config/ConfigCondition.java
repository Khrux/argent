package com.khrux.argent.config;

import com.khrux.argent.Argent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.StringRepresentable;

public record ConfigCondition(Option option) implements ResourceCondition {
	public static final MapCodec<ConfigCondition> CODEC = Option.CODEC.fieldOf("option").xmap(ConfigCondition::new, ConfigCondition::option);
	public static final ResourceConditionType<ConfigCondition> TYPE = ResourceConditionType.create(Argent.id("config"), CODEC);

	@Override
	public ResourceConditionType<?> getType() {
		return TYPE;
	}

	@Override
	public boolean test(final RegistryOps.RegistryInfoLookup registryInfo) {
		ArgentConfig config = ArgentConfig.get();
		return switch (this.option) {
			case MIRRORS -> config.mirrors();
			case SILVER_MIRROR -> config.mirrorRecipe == ArgentConfig.MirrorRecipe.SILVER;
			case DIAMOND_MIRROR -> config.mirrorRecipe == ArgentConfig.MirrorRecipe.DIAMOND;
			case SOUL_LANTERN -> config.soulLantern != ArgentConfig.SoulLantern.OFF;
		};
	}

	public enum Option implements StringRepresentable {
		MIRRORS("mirrors"),
		SILVER_MIRROR("silver_mirror"),
		DIAMOND_MIRROR("diamond_mirror"),
		SOUL_LANTERN("soul_lantern");

		public static final Codec<Option> CODEC = StringRepresentable.fromEnum(Option::values);
		private final String name;

		Option(final String name) {
			this.name = name;
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}
	}
}
