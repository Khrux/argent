package com.khrux.argent.client.gui.screens;

import com.khrux.argent.client.ArgentClient;
import com.khrux.argent.config.ArgentConfig;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class ArgentConfigScreen extends OptionsSubScreen {
	private static final Component TITLE = Component.translatable("options.argent.title");
	private static final Component OPEN_SKIN_EDITOR = Component.translatable("options.argent.open_skin_editor");
	private @Nullable Button skinEditorKeyButton;
	private boolean selectingSkinEditorKey;

	public ArgentConfigScreen(final Screen lastScreen) {
		super(lastScreen, Minecraft.getInstance().options, TITLE);
	}

	private static <T> OptionInstance.TooltipSupplier<T> tooltip(final String captionId) {
		return OptionInstance.cachedConstantTooltip(Component.translatable(captionId + ".tooltip"));
	}

	private Checkbox checkbox(final String captionId, final boolean initialValue, final Checkbox.OnValueChange onValueChange) {
		Checkbox checkbox = Checkbox.builder(Component.translatable(captionId), this.font)
			.maxWidth(Button.DEFAULT_WIDTH)
			.selected(initialValue)
			.onValueChange(onValueChange)
			.build();
		checkbox.setTooltip(Tooltip.create(Component.translatable(captionId + ".tooltip")));
		return checkbox;
	}

	private static <T> OptionInstance<T> choice(
		final String captionId, final T[] values, final Codec<T> codec, final Function<T, Component> displayName, final T initialValue, final OptionInstance.ValueUpdateListener<T> onValueUpdate
	) {
		return new OptionInstance<>(
			captionId,
			tooltip(captionId),
			(caption, value) -> displayName.apply(value),
			new OptionInstance.Enum<>(List.of(values), codec),
			initialValue,
			onValueUpdate
		);
	}

	@Override
	protected void addOptions() {
		ArgentConfig config = ArgentConfig.get();
		this.skinEditorKeyButton = Button.builder(this.skinEditorKeyMessage(), button -> {
			this.selectingSkinEditorKey = true;
			button.setMessage(this.skinEditorKeyMessage());
		}).build();
		this.list.addSmall(Button.builder(OPEN_SKIN_EDITOR, button -> ArgentClient.openSkinEditor(this.minecraft)).build(), this.skinEditorKeyButton);
		this.list
			.addSmall(
				choice(
					"options.argent.mirror_recipe",
					ArgentConfig.MirrorRecipe.values(),
					ArgentConfig.MirrorRecipe.CODEC,
					ArgentConfig.MirrorRecipe::getDisplayName,
					config.mirrorRecipe,
					value -> config.mirrorRecipe = value
				),
				choice(
					"options.argent.soul_lantern",
					ArgentConfig.SoulLantern.values(),
					ArgentConfig.SoulLantern.CODEC,
					ArgentConfig.SoulLantern::getDisplayName,
					config.soulLantern,
					value -> config.soulLantern = value
				),
				choice(
					"options.argent.reflections",
					ArgentConfig.Reflections.values(),
					ArgentConfig.Reflections.CODEC,
					ArgentConfig.Reflections::getDisplayName,
					config.reflections,
					value -> config.reflections = value
				)
			);
		this.list
			.addSmall(
				List.of(
					this.checkbox("options.argent.silver", config.silver, (checkbox, value) -> config.silver = value),
					this.checkbox("options.argent.mirror_screen", config.mirrorScreen, (checkbox, value) -> config.mirrorScreen = value),
					this.checkbox("options.argent.wither_zombies", config.witherZombies, (checkbox, value) -> config.witherZombies = value),
					this.checkbox("options.argent.scrying", config.scrying, (checkbox, value) -> config.scrying = value),
					this.checkbox("options.argent.scrying_library_loot", config.scryingLibraryLoot, (checkbox, value) -> config.scryingLibraryLoot = value)
				)
			);
	}

	private Component skinEditorKeyMessage() {
		Component key = ArgentClient.openSkinEditorKey().getTranslatedKeyMessage();
		if (this.selectingSkinEditorKey) {
			key = Component.literal("> ").append(key.copy().withStyle(ChatFormatting.WHITE, ChatFormatting.UNDERLINE)).append(" <").withStyle(ChatFormatting.YELLOW);
		}

		return Component.translatable("options.argent.skin_editor_key", key);
	}

	private void selectSkinEditorKey(final InputConstants.Key key) {
		ArgentClient.openSkinEditorKey().setKey(key);
		KeyMapping.resetMapping();
		this.selectingSkinEditorKey = false;
		if (this.skinEditorKeyButton != null) {
			this.skinEditorKeyButton.setMessage(this.skinEditorKeyMessage());
		}
	}

	@Override
	public boolean keyPressed(final KeyEvent event) {
		if (!this.selectingSkinEditorKey) {
			return super.keyPressed(event);
		}

		this.selectSkinEditorKey(event.isEscape() ? InputConstants.UNKNOWN : InputConstants.getKey(event));
		return true;
	}

	@Override
	public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
		if (!this.selectingSkinEditorKey) {
			return super.mouseClicked(event, doubleClick);
		}

		this.selectSkinEditorKey(InputConstants.Type.MOUSE.getOrCreate(event.button()));
		return true;
	}

	@Override
	public void removed() {
		super.removed();
		ArgentConfig.get().save();
	}
}
