package com.khrux.justspawnlanterns.client.gui.screens;

import com.khrux.justspawnlanterns.config.JustSpawnLanternsConfig;
import java.util.List;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;

public class JustSpawnLanternsOptions {
	public static OptionInstance<JustSpawnLanternsConfig.Markers> markers() {
		JustSpawnLanternsConfig config = JustSpawnLanternsConfig.get();
		return new OptionInstance<>(
			"options.justspawnlanterns.markers",
			OptionInstance.cachedConstantTooltip(Component.translatable("options.justspawnlanterns.markers.tooltip")),
			(caption, value) -> value.getDisplayName(),
			new OptionInstance.Enum<>(List.of(JustSpawnLanternsConfig.Markers.HAND, JustSpawnLanternsConfig.Markers.INVENTORY), JustSpawnLanternsConfig.Markers.CODEC),
			config.markers == JustSpawnLanternsConfig.Markers.OFF ? JustSpawnLanternsConfig.Markers.HAND : config.markers,
			value -> config.markers = value
		);
	}
}
