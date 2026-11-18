package com.khrux.justmirrors.client.gui.components;

import com.khrux.justmirrors.client.JustMirrorsClient;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class SkinEditorKeyBinding {
	private static final Component OPEN_SKIN_EDITOR = Component.translatable("options.justmirrors.open_skin_editor");
	private final Minecraft minecraft;
	private final Button keyButton;
	private boolean selecting;

	public SkinEditorKeyBinding(final Minecraft minecraft) {
		this.minecraft = minecraft;
		this.keyButton = Button.builder(this.keyMessage(), button -> {
			this.selecting = true;
			button.setMessage(this.keyMessage());
		}).build();
	}

	public Button openButton() {
		return Button.builder(OPEN_SKIN_EDITOR, button -> JustMirrorsClient.openSkinEditor(this.minecraft)).build();
	}

	public Button keyButton() {
		return this.keyButton;
	}

	private Component keyMessage() {
		Component key = JustMirrorsClient.openSkinEditorKey().getTranslatedKeyMessage();
		if (this.selecting) {
			key = Component.literal("> ").append(key.copy().withStyle(ChatFormatting.WHITE, ChatFormatting.UNDERLINE)).append(" <").withStyle(ChatFormatting.YELLOW);
		}

		return Component.translatable("options.justmirrors.skin_editor_key", key);
	}

	private void select(final InputConstants.Key key) {
		JustMirrorsClient.openSkinEditorKey().setKey(key);
		KeyMapping.resetMapping();
		this.selecting = false;
		this.keyButton.setMessage(this.keyMessage());
	}

	public boolean keyPressed(final KeyEvent event) {
		if (!this.selecting) {
			return false;
		}

		this.select(event.isEscape() ? InputConstants.UNKNOWN : InputConstants.getKey(event));
		return true;
	}

	public boolean mouseClicked(final MouseButtonEvent event) {
		if (!this.selecting) {
			return false;
		}

		this.select(InputConstants.Type.MOUSE.getOrCreate(event.button()));
		return true;
	}
}
