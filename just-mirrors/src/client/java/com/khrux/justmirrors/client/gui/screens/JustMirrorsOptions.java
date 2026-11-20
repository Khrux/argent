package com.khrux.justmirrors.client.gui.screens;

import com.khrux.justmirrors.config.JustMirrorsConfig;
import java.util.List;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;

public class JustMirrorsOptions {
	private static final Component MIRROR_SCREEN_TOOLTIP = Component.translatable("options.justmirrors.mirror_screen.tooltip");

	public static OptionInstance<JustMirrorsConfig.Reflections> reflections() {
		JustMirrorsConfig config = JustMirrorsConfig.get();
		return new OptionInstance<>(
			"options.justmirrors.reflections",
			OptionInstance.cachedConstantTooltip(Component.translatable("options.justmirrors.reflections.tooltip")),
			(caption, value) -> value.getDisplayName(),
			new OptionInstance.Enum<>(List.of(JustMirrorsConfig.Reflections.values()), JustMirrorsConfig.Reflections.CODEC),
			config.reflections,
			value -> config.reflections = value
		);
	}

	public static OptionInstance<Integer> reflectionDistance() {
		JustMirrorsConfig config = JustMirrorsConfig.get();
		return new OptionInstance<>(
			"options.justmirrors.reflection_distance",
			OptionInstance.cachedConstantTooltip(Component.translatable("options.justmirrors.reflection_distance.tooltip")),
			(caption, value) -> Options.genericValueLabel(caption, value * JustMirrorsConfig.REFLECTION_DISTANCE_STEP),
			new OptionInstance.IntRange(
				JustMirrorsConfig.MIN_REFLECTION_DISTANCE / JustMirrorsConfig.REFLECTION_DISTANCE_STEP,
				JustMirrorsConfig.MAX_REFLECTION_DISTANCE / JustMirrorsConfig.REFLECTION_DISTANCE_STEP
			),
			config.reflectionDistance / JustMirrorsConfig.REFLECTION_DISTANCE_STEP,
			value -> config.reflectionDistance = value * JustMirrorsConfig.REFLECTION_DISTANCE_STEP
		);
	}

	public static OptionInstance<Boolean> mirrorScreen() {
		JustMirrorsConfig config = JustMirrorsConfig.get();
		return OptionInstance.createBoolean(
			"options.justmirrors.mirror_screen",
			OptionInstance.cachedConstantTooltip(MIRROR_SCREEN_TOOLTIP),
			config.mirrorScreen,
			value -> config.mirrorScreen = value
		);
	}
}
