package com.khrux.justmirrors.client.skin;

import com.khrux.argent.Argent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public enum SkinTool {
	PENCIL("pencil"),
	FILL("fill"),
	ERASER("eraser"),
	PICKER("picker");

	private final String name;

	SkinTool(final String name) {
		this.name = name;
	}

	public Identifier sprite() {
		return JustMirrors.id(this.name);
	}

	public Component getName() {
		return Component.translatable("gui.justmirrors.tool." + this.name);
	}
}
