package com.khrux.argent.client.skin;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.player.PlayerModelType;
import org.jspecify.annotations.Nullable;

public enum SkinPart {
	HEAD("head", null, false, 0, 0, 8, 8, 8, false, 4, 0),
	HAT("hat", "head", true, 32, 0, 8, 8, 8, false, 4, 0),
	BODY("body", null, false, 16, 16, 8, 12, 4, false, 4, 8),
	JACKET("jacket", "body", true, 16, 32, 8, 12, 4, false, 4, 8),
	RIGHT_ARM("right_arm", null, false, 40, 16, 4, 12, 4, true, 0, 8),
	RIGHT_SLEEVE("right_sleeve", "right_arm", true, 40, 32, 4, 12, 4, true, 0, 8),
	LEFT_ARM("left_arm", null, false, 32, 48, 4, 12, 4, true, 12, 8),
	LEFT_SLEEVE("left_sleeve", "left_arm", true, 48, 48, 4, 12, 4, true, 12, 8),
	RIGHT_LEG("right_leg", null, false, 0, 16, 4, 12, 4, false, 4, 20),
	RIGHT_PANTS("right_pants", "right_leg", true, 0, 32, 4, 12, 4, false, 4, 20),
	LEFT_LEG("left_leg", null, false, 16, 48, 4, 12, 4, false, 8, 20),
	LEFT_PANTS("left_pants", "left_leg", true, 0, 48, 4, 12, 4, false, 8, 20);

	private static final int SLIM_ARM_WIDTH = 3;
	private static final float HAT_GROW = 0.5F;
	private static final float OVERLAY_GROW = 0.25F;
	private final String name;
	private final @Nullable String parent;
	private final boolean overlay;
	private final int u;
	private final int v;
	private final int width;
	private final int height;
	private final int depth;
	private final boolean arm;
	private final int dollX;
	private final int dollY;

	SkinPart(
		final String name,
		final @Nullable String parent,
		final boolean overlay,
		final int u,
		final int v,
		final int width,
		final int height,
		final int depth,
		final boolean arm,
		final int dollX,
		final int dollY
	) {
		this.name = name;
		this.parent = parent;
		this.overlay = overlay;
		this.u = u;
		this.v = v;
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.arm = arm;
		this.dollX = dollX;
		this.dollY = dollY;
	}

	public static @Nullable SkinPart byName(final String name) {
		for (SkinPart part : values()) {
			if (part.name.equals(name)) {
				return part;
			}
		}

		return null;
	}

	public boolean isOverlay() {
		return this.overlay;
	}

	public float grow() {
		if (!this.overlay) {
			return 0.0F;
		}

		return this == HAT ? HAT_GROW : OVERLAY_GROW;
	}

	public String getName() {
		return this.name;
	}

	public int u() {
		return this.u;
	}

	public int v() {
		return this.v;
	}

	public int depth() {
		return this.depth;
	}

	public SkinPart base() {
		return this.overlay ? values()[this.ordinal() - 1] : this;
	}

	public int width(final PlayerModelType model) {
		return this.arm && model == PlayerModelType.SLIM ? SLIM_ARM_WIDTH : this.width;
	}

	public int height() {
		return this.height;
	}

	public int frontU() {
		return this.u + this.depth;
	}

	public int frontV() {
		return this.v + this.depth;
	}

	public int dollX(final PlayerModelType model) {
		return this == RIGHT_ARM || this == RIGHT_SLEEVE ? this.dollX + this.width - this.width(model) : this.dollX;
	}

	public int dollY() {
		return this.dollY;
	}

	public ModelPart modelPart(final ModelPart root) {
		return this.parent == null ? root.getChild(this.name) : root.getChild(this.parent).getChild(this.name);
	}
}
