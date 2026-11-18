package com.khrux.justscrying.config;

import com.khrux.justscrying.JustScrying;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.StringRepresentable;

public record JustScryingCondition(Option option) implements ResourceCondition {
	public static final MapCodec<JustScryingCondition> CODEC = Option.CODEC.fieldOf("option").xmap(JustScryingCondition::new, JustScryingCondition::option);
	public static final ResourceConditionType<JustScryingCondition> TYPE = ResourceConditionType.create(JustScrying.id("config"), CODEC);

	@Override
	public ResourceConditionType<?> getType() {
		return TYPE;
	}

	@Override
	public boolean test(final RegistryOps.RegistryInfoLookup registryInfo) {
		JustScryingConfig config = JustScryingConfig.get();
		return switch (this.option) {
			case PARROT_ARMOR -> config.isEnabled() && config.parrotArmorRecipe;
			case CAT_ARMOR -> config.isEnabled() && config.catArmorRecipe;
		};
	}

	public enum Option implements StringRepresentable {
		PARROT_ARMOR("parrot_armor"),
		CAT_ARMOR("cat_armor");

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
