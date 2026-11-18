package com.khrux.argent.client.gui.components;

import com.khrux.argent.config.ArgentConfig;
import com.khrux.justmirrors.world.item.JustMirrorsItems;
import com.khrux.justscrying.client.renderer.entity.ArmoredParrotRenderer;
import com.khrux.justscrying.world.item.JustScryingItems;
import com.khrux.justsilver.world.item.JustSilverItems;
import com.khrux.justwitherzombies.client.renderer.entity.state.WitherZombieRenderState;
import com.khrux.justwitherzombies.world.entity.JustWitherZombiesEntityTypes;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.model.animal.parrot.ParrotModel;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.ParrotRenderState;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SectionPicture extends AbstractWidget {
	private static final int LABEL_HEIGHT = 10;
	private static final float ITEM_SCALE = 2.0F;
	private static final float BODY_TURN = 30.0F;
	private static final float TILT = 10.0F;
	private static final float WITHER_ZOMBIE_SCALE = 18.0F;
	private static final float PARROT_SCALE = 34.0F;
	private final ArgentConfig.Section section;
	private final Font font;

	public SectionPicture(final int width, final int height, final ArgentConfig.Section section, final Font font) {
		super(0, 0, width, height, section.getDisplayName());
		this.section = section;
		this.font = font;
		this.active = false;
	}

	@Override
	protected void extractWidgetRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float partialTicks) {
		int pictureBottom = this.getBottom() - LABEL_HEIGHT;
		switch (this.section) {
			case SILVER -> this.item(graphics, new ItemStack(JustSilverItems.SILVER_INGOT), pictureBottom);
			case MIRRORS -> this.item(graphics, new ItemStack(JustMirrorsItems.MIRROR), pictureBottom);
			case SPAWN_LANTERNS -> this.item(graphics, new ItemStack(Items.SOUL_LANTERN), pictureBottom);
			case WITHER_ZOMBIE -> this.entity(graphics, new WitherZombieRenderState(), JustWitherZombiesEntityTypes.WITHER_ZOMBIE, WITHER_ZOMBIE_SCALE, pictureBottom);
			case SCRYING -> this.entity(graphics, this.armoredParrot(), EntityTypes.PARROT, PARROT_SCALE, pictureBottom);
		}

		graphics.centeredText(this.font, this.getMessage(), this.getX() + this.getWidth() / 2, pictureBottom + 1, -1);
	}

	private void item(final GuiGraphicsExtractor graphics, final ItemStack stack, final int pictureBottom) {
		float size = 16.0F * ITEM_SCALE;
		graphics.pose().pushMatrix();
		graphics.pose().translate(this.getX() + (this.getWidth() - size) / 2.0F, this.getY() + (pictureBottom - this.getY() - size) / 2.0F);
		graphics.pose().scale(ITEM_SCALE, ITEM_SCALE);
		graphics.item(stack, 0, 0);
		graphics.pose().popMatrix();
	}

	private ParrotRenderState armoredParrot() {
		ParrotRenderState state = new ParrotRenderState();
		state.variant = Parrot.Variant.RED_BLUE;
		state.pose = ParrotModel.Pose.STANDING;
		((FabricRenderState)state).setData(ArmoredParrotRenderer.BODY_ARMOR, new ItemStack(JustScryingItems.PARROT_ARMOR));
		return state;
	}

	private void entity(final GuiGraphicsExtractor graphics, final LivingEntityRenderState state, final EntityType<?> type, final float scale, final int pictureBottom) {
		state.entityType = type;
		state.boundingBoxWidth = type.getWidth();
		state.boundingBoxHeight = type.getHeight();
		state.lightCoords = LightCoordsUtil.FULL_BRIGHT;
		state.bodyRot = 180.0F + BODY_TURN;
		state.yRot = 0.0F;
		state.xRot = 0.0F;
		Quaternionf tilt = new Quaternionf().rotateX(TILT * Mth.DEG_TO_RAD);
		Quaternionf rotation = new Quaternionf().rotateZ((float)Math.PI).mul(tilt);
		Vector3f translation = new Vector3f(0.0F, state.boundingBoxHeight / 2.0F + 0.0625F, 0.0F);
		graphics.entity(state, scale, translation, rotation, tilt, this.getX(), this.getY(), this.getRight(), pictureBottom);
	}

	@Override
	protected void updateWidgetNarration(final NarrationElementOutput output) {
		this.defaultButtonNarrationText(output);
	}
}
