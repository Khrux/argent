package com.khrux.justsilver.config;

import com.khrux.justsilver.JustSilver;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.StringRepresentable;

public record JustSilverCondition(Option option) implements ResourceCondition {
	public static final MapCodec<JustSilverCondition> CODEC = Option.CODEC.fieldOf("option").xmap(JustSilverCondition::new, JustSilverCondition::option);
	public static final ResourceConditionType<JustSilverCondition> TYPE = ResourceConditionType.create(JustSilver.id("config"), CODEC);

	@Override
	public ResourceConditionType<?> getType() {
		return TYPE;
	}

	@Override
	public boolean test(final RegistryOps.RegistryInfoLookup registryInfo) {
		JustSilverConfig config = JustSilverConfig.get();
		return switch (this.option) {
			case SILVER_BELL -> config.isEnabled() && config.silverBellRecipe;
			case SOUL_LANTERN_IRON -> config.soulLanternIron;
			case SOUL_LANTERN_SILVER -> config.soulLanternFromSilver();
			case SOUL_LANTERN_MIXED -> config.soulLanternIron && config.soulLanternFromSilver();
		};
	}

	public enum Option implements StringRepresentable {
		SILVER_BELL("silver_bell"),
		SOUL_LANTERN_IRON("soul_lantern_iron"),
		SOUL_LANTERN_SILVER("soul_lantern_silver"),
		SOUL_LANTERN_MIXED("soul_lantern_mixed");

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
