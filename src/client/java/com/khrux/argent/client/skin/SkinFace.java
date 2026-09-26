package com.khrux.argent.client.skin;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.PlayerModelType;

public record SkinFace(SkinPart part, Direction side) {
	public int u(final PlayerModelType model) {
		int u = this.part.u();
		int depth = this.part.depth();
		int width = this.part.width(model);
		return switch (this.side) {
			case WEST -> u;
			case DOWN, NORTH -> u + depth;
			case UP, EAST -> u + depth + width;
			case SOUTH -> u + depth + width + depth;
		};
	}

	public int v() {
		return this.side.getAxis() == Direction.Axis.Y ? this.part.v() : this.part.v() + this.part.depth();
	}

	public int width(final PlayerModelType model) {
		return this.side.getAxis() == Direction.Axis.X ? this.part.depth() : this.part.width(model);
	}

	public int height() {
		return this.side.getAxis() == Direction.Axis.Y ? this.part.depth() : this.part.height();
	}

	public Component getName() {
		return Component.translatable(
			"gui.argent.face", Component.translatable("gui.argent.part." + this.part.getName()), Component.translatable("gui.argent.side." + this.side.getName())
		);
	}
}
