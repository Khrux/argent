package com.khrux.justmirrors.client.gui.screens;

import com.khrux.justmirrors.client.gui.components.SkinEditorKeyBinding;
import com.khrux.justmirrors.config.JustMirrorsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class JustMirrorsConfigScreen extends OptionsSubScreen {
	private static final Component TITLE = Component.translatable("options.justmirrors.title");
	private final SkinEditorKeyBinding skinEditorKey = new SkinEditorKeyBinding(Minecraft.getInstance());

	public JustMirrorsConfigScreen(final Screen lastScreen) {
		super(lastScreen, Minecraft.getInstance().options, TITLE);
	}

	@Override
	protected void addOptions() {
		this.list.addSmall(this.skinEditorKey.openButton(), this.skinEditorKey.keyButton());
		this.list.addSmall(JustMirrorsOptions.reflections(), JustMirrorsOptions.reflectionDistance(), JustMirrorsOptions.mirrorScreen());
	}

	@Override
	public boolean keyPressed(final KeyEvent event) {
		return this.skinEditorKey.keyPressed(event) || super.keyPressed(event);
	}

	@Override
	public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
		return this.skinEditorKey.mouseClicked(event) || super.mouseClicked(event, doubleClick);
	}

	@Override
	public void removed() {
		super.removed();
		JustMirrorsConfig.get().save();
	}
}
