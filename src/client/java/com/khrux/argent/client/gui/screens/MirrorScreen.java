package com.khrux.argent.client.gui.screens;

import com.khrux.argent.Argent;
import com.khrux.argent.client.ArgentClient;
import com.khrux.argent.client.gui.components.ColourPaletteWidget;
import com.khrux.argent.client.gui.components.ColourPickerWidget;
import com.khrux.argent.client.gui.components.SkinFaceWidget;
import com.khrux.argent.client.gui.components.SkinLayerDoll;
import com.khrux.argent.client.gui.components.SkinModelWidget;
import com.khrux.argent.client.gui.components.SkinPresetTab;
import com.khrux.argent.client.skin.SkinFace;
import com.khrux.argent.client.skin.SkinImporter;
import com.khrux.argent.client.skin.SkinPart;
import com.khrux.argent.client.skin.SkinPreset;
import com.khrux.argent.client.skin.SkinPresets;
import com.khrux.argent.client.skin.SkinTool;
import com.khrux.argent.client.skin.SkinUploader;
import com.khrux.argent.world.level.block.ArgentBlocks;
import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.InputConstants;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.player.PlayerModelType;
import org.jspecify.annotations.Nullable;

public class MirrorScreen extends Screen {
	private static final SystemToast.SystemToastId IMPORT_FAILURE = new SystemToast.SystemToastId();
	private static final Component IMPORT_FAILED = Component.translatable("gui.argent.import.failed");
	private static final SystemToast.SystemToastId UPLOAD_RESULT = new SystemToast.SystemToastId();
	private static final Identifier BACKGROUND_LOCATION = Argent.id("textures/gui/mirror_screen.png");
	private static final int IMAGE_WIDTH = 320;
	private static final int IMAGE_HEIGHT = 256;
	private static final int TITLE_X = 8;
	private static final int TITLE_Y = 6;
	private static final int VIEWPORT_SIZE = 136;
	private static final int MODEL_X = 8;
	private static final int MODEL_Y = 18;
	private static final int FACE_X = 176;
	private static final int FACE_Y = 18;
	private static final int COLOUR_X = 294;
	private static final int COLOUR_Y = 158;
	private static final int COLOUR_SIZE = 18;
	private static final int COLOUR_BORDER = 0xFF373737;
	private static final int TOOL_X = 176;
	private static final int UNDO_X = 256;
	private static final int REDO_X = 275;
	private static final int TOOL_Y = 158;
	private static final int TOOL_STEP = 19;
	private static final int TOOL_SIZE = 18;
	private static final int PALETTE_X = 176;
	private static final int PALETTE_Y = 180;
	private static final int PRESET_X = 8;
	private static final int PRESET_Y = 204;
	private static final int PRESET_STEP = 34;
	private static final int BASE_DOLL_X = 12;
	private static final int OVERLAY_DOLL_X = 36;
	private static final int DOLL_Y = 160;
	private static final int ARM_WIDTH_X = 68;
	private static final int RESET_VIEW_X = 116;
	private static final int MODEL_BUTTON_Y = 164;
	private static final int BUTTON_SIZE = 20;
	private static final int SPRITE_SIZE = 16;
	private static final int IMPORT_Y = 234;
	private static final int IMPORT_FIELD_X = 8;
	private static final int IMPORT_FIELD_WIDTH = 180;
	private static final int FILE_X = 192;
	private static final int IMPORT_X = 232;
	private static final int RESET_X = 272;
	private static final int IMPORT_BUTTON_WIDTH = 36;
	private static final int RESET_WIDTH = 40;
	private static final int IMPORT_ROW_HEIGHT = 16;
	private static final int IMPORT_MAX_LENGTH = 512;
	private static final int UPLOAD_X = 256;
	private static final int UPLOAD_Y = 2;
	private static final int UPLOAD_WIDTH = 56;
	private static final int UPLOAD_HEIGHT = 14;
	private static final int AUTOSAVE_TICKS = 100;
	private final SkinPresets presets;
	private final Set<SkinPart> hiddenParts = EnumSet.noneOf(SkinPart.class);
	private @Nullable SkinFace selectedFace;
	private int colour = CommonColors.BLACK;
	private SkinTool tool = SkinTool.PENCIL;
	private SkinTool paintTool = SkinTool.PENCIL;
	private SkinModelWidget model;
	private SkinFaceWidget face;
	private ColourPickerWidget colourPicker;
	private Button eraserButton;
	private Button importButton;
	private Button uploadButton;
	private EditBox importField;
	private Button undoButton;
	private Button redoButton;
	private Button slimArmsButton;
	private Button wideArmsButton;
	private int ticksSinceSave;
	private boolean importing;
	private boolean uploading;

	public MirrorScreen(final SkinPresets presets) {
		super(ArgentBlocks.MIRROR.getName());
		this.presets = presets;
	}

	private int backgroundLeft() {
		return (this.width - IMAGE_WIDTH) / 2;
	}

	private int backgroundTop() {
		return (this.height - IMAGE_HEIGHT) / 2;
	}

	@Override
	protected void init() {
		int left = this.backgroundLeft();
		int top = this.backgroundTop();
		this.model = new SkinModelWidget(VIEWPORT_SIZE, VIEWPORT_SIZE, this.presets::selected, this.hiddenParts, this::selectFace);
		this.model.setPosition(left + MODEL_X, top + MODEL_Y);
		this.addRenderableWidget(this.model);
		this.face = this.addRenderableWidget(
			new SkinFaceWidget(
				left + FACE_X,
				top + FACE_Y,
				VIEWPORT_SIZE,
				VIEWPORT_SIZE,
				this.presets::selected,
				() -> this.selectedFace,
				() -> this.tool,
				() -> this.colour,
				this::pickColour,
				this.presets::useColour
			)
		);
		this.colourPicker = this.addRenderableWidget(new ColourPickerWidget(left + FACE_X, top + FACE_Y, VIEWPORT_SIZE, colour -> this.colour = colour));
		this.colourPicker.setColour(this.colour);
		this.setColourPickerOpen(false);
		for (SkinTool tool : SkinTool.values()) {
			Button button = this.addToolButton(left + TOOL_X + tool.ordinal() * TOOL_STEP, top + TOOL_Y, tool);
			if (tool == SkinTool.ERASER) {
				this.eraserButton = button;
			}
		}

		this.undoButton = this.addPaintButton(left + UNDO_X, top + TOOL_Y, Argent.id("undo"), Component.translatable("gui.argent.undo"), button -> this.presets.selected().undo());
		this.redoButton = this.addPaintButton(left + REDO_X, top + TOOL_Y, Argent.id("redo"), Component.translatable("gui.argent.redo"), button -> this.presets.selected().redo());
		this.addRenderableWidget(new ColourPaletteWidget(left + PALETTE_X, top + PALETTE_Y, this.presets.colours(), this::setColour));
		this.addLayerDoll(left + BASE_DOLL_X, top + DOLL_Y, Component.translatable("gui.argent.layer.base"), false);
		this.addLayerDoll(left + OVERLAY_DOLL_X, top + DOLL_Y, Component.translatable("gui.argent.layer.overlay"), true);
		this.slimArmsButton = this.addModelButton(left + ARM_WIDTH_X, top, "arm_slim", Component.translatable("gui.argent.arm_width.slim"), button -> this.setArmModel(PlayerModelType.WIDE));
		this.wideArmsButton = this.addModelButton(left + ARM_WIDTH_X, top, "arm_wide", Component.translatable("gui.argent.arm_width.wide"), button -> this.setArmModel(PlayerModelType.SLIM));
		this.addModelButton(left + RESET_VIEW_X, top, "reset_view", Component.translatable("gui.argent.reset_view"), button -> this.model.resetView());
		for (int i = 0; i < SkinPresets.COUNT; i++) {
			this.addRenderableWidget(new SkinPresetTab(left + PRESET_X + i * PRESET_STEP, top + PRESET_Y, this.presets, i));
		}

		this.importField = new EditBox(this.font, left + IMPORT_FIELD_X, top + IMPORT_Y, IMPORT_FIELD_WIDTH, IMPORT_ROW_HEIGHT, Component.translatable("gui.argent.import.field"));
		this.importField.setMaxLength(IMPORT_MAX_LENGTH);
		this.importField.setHint(Component.translatable("gui.argent.import.hint"));
		this.addRenderableWidget(this.importField);
		this.addRenderableWidget(
			Button.builder(Component.translatable("gui.argent.file"), button -> this.openPresetFolder())
				.bounds(left + FILE_X, top + IMPORT_Y, IMPORT_BUTTON_WIDTH, IMPORT_ROW_HEIGHT)
				.tooltip(Tooltip.create(Component.translatable("gui.argent.file.tooltip")))
				.build()
		);
		this.importButton = this.addRenderableWidget(
			Button.builder(Component.translatable("gui.argent.import"), button -> this.importInput())
				.bounds(left + IMPORT_X, top + IMPORT_Y, IMPORT_BUTTON_WIDTH, IMPORT_ROW_HEIGHT)
				.build()
		);
		this.addRenderableWidget(
			Button.builder(Component.translatable("gui.argent.reset"), button -> this.confirmReset())
				.bounds(left + RESET_X, top + IMPORT_Y, RESET_WIDTH, IMPORT_ROW_HEIGHT)
				.build()
		);
		this.uploadButton = this.addRenderableWidget(
			Button.builder(Component.translatable("gui.argent.upload"), button -> this.confirmUpload())
				.bounds(left + UPLOAD_X, top + UPLOAD_Y, UPLOAD_WIDTH, UPLOAD_HEIGHT)
				.tooltip(Tooltip.create(Component.translatable("gui.argent.upload.tooltip")))
				.build()
		);
		this.updateButtons();
	}

	private void confirmUpload() {
		int index = this.presets.selectedIndex();
		this.minecraft.gui.setScreen(new ConfirmScreen(confirmed -> {
			this.minecraft.gui.setScreen(this);
			if (confirmed) {
				this.upload(index);
			}
		}, Component.translatable("gui.argent.upload.title", index + 1), Component.translatable("gui.argent.upload.message")));
	}

	private void upload(final int index) {
		this.save();
		this.uploading = true;
		this.updateButtons();
		SkinUploader.upload(this.minecraft, this.presets.imagePath(index), this.presets.get(index).model()).thenAcceptAsync(uploaded -> {
			this.uploading = false;
			this.updateButtons();
			if (!uploaded) {
				SystemToast.add(this.minecraft.gui.toastManager(), UPLOAD_RESULT, Component.translatable("gui.argent.upload.failed"), null);
				return;
			}

			SystemToast.add(this.minecraft.gui.toastManager(), UPLOAD_RESULT, Component.translatable("gui.argent.upload.done"), null);
			ServerData server = this.minecraft.getCurrentServer();
			if (server != null && !server.isRealm() && !this.minecraft.isLocalServer() && this.minecraft.gui.screen() == this) {
				this.confirmRejoin(server);
			}
		}, this.minecraft);
	}

	private void confirmRejoin(final ServerData server) {
		this.minecraft.gui.setScreen(new ConfirmScreen(confirmed -> {
			if (!confirmed) {
				this.minecraft.gui.setScreen(this);
				return;
			}

			this.minecraft.disconnectFromWorld(ClientLevel.DEFAULT_QUIT_MESSAGE);
			ConnectScreen.startConnecting(new JoinMultiplayerScreen(new TitleScreen()), this.minecraft, ServerAddress.parseString(server.ip), server, false, null);
		}, Component.translatable("gui.argent.rejoin.title"), Component.translatable("gui.argent.rejoin.message", server.name)));
	}

	private void openPresetFolder() {
		this.save();
		Blaze3D.openPath(this.presets.directory());
	}

	private void importInput() {
		String input = this.importField.getValue();
		if (input.isBlank()) {
			return;
		}

		this.runImport(SkinImporter.importInput(this.minecraft, this.presets.selected(), input), input);
	}

	@Override
	public void onFilesDrop(final List<Path> files) {
		Path file = files.getFirst();
		this.runImport(SkinImporter.importFile(this.minecraft, this.presets.selected(), file), file.getFileName().toString());
	}

	private void runImport(final CompletableFuture<Boolean> result, final String source) {
		this.importing = true;
		this.updateButtons();
		result.exceptionally(throwable -> false).thenAcceptAsync(imported -> {
			this.importing = false;
			this.updateButtons();
			if (imported) {
				this.importField.setValue("");
			} else {
				SystemToast.add(this.minecraft.gui.toastManager(), IMPORT_FAILURE, IMPORT_FAILED, Component.literal(source));
			}
		}, this.minecraft);
	}

	private void confirmReset() {
		int index = this.presets.selectedIndex();
		this.minecraft.gui.setScreen(new ConfirmScreen(confirmed -> {
			if (confirmed) {
				this.presets.reset(this.minecraft, index);
			}

			this.minecraft.gui.setScreen(this);
		}, Component.translatable("gui.argent.reset.title", index + 1), Component.translatable("gui.argent.reset.message")));
	}

	private void addLayerDoll(final int x, final int y, final Component message, final boolean overlay) {
		SkinLayerDoll doll = new SkinLayerDoll(x, y, message, overlay, this.presets::selected, this.hiddenParts);
		doll.setTooltip(Tooltip.create(message));
		this.addRenderableWidget(doll);
	}

	private Button addToolButton(final int x, final int y, final SkinTool tool) {
		Button button = this.addPaintButton(x, y, tool.sprite(), tool.getName(), pressed -> this.selectTool(tool));
		button.setOverrideRenderHighlightedSprite(() -> this.tool == tool);
		return button;
	}

	private Button addPaintButton(final int x, final int y, final Identifier sprite, final Component message, final Button.OnPress onPress) {
		SpriteIconButton button = SpriteIconButton.builder(message, onPress, true).sprite(sprite, SPRITE_SIZE, SPRITE_SIZE).size(TOOL_SIZE, TOOL_SIZE).build();
		button.setPosition(x, y);
		button.setTooltip(Tooltip.create(message));
		return this.addRenderableWidget(button);
	}

	private void selectTool(final SkinTool tool) {
		this.tool = tool;
		if (tool != SkinTool.PICKER) {
			this.paintTool = tool;
		}

		this.setColourPickerOpen(false);
	}

	private void selectFace(final SkinFace face) {
		this.selectedFace = face;
		this.setColourPickerOpen(false);
		this.updateButtons();
	}

	private boolean canErase() {
		return this.selectedFace == null || this.selectedFace.part().isOverlay();
	}

	private void pickColour(final int colour) {
		this.setColour(colour);
		this.tool = this.paintTool;
	}

	private void setColour(final int colour) {
		this.colour = colour;
		this.colourPicker.setColour(colour);
	}

	private void setColourPickerOpen(final boolean open) {
		this.colourPicker.visible = open;
		this.face.visible = !open;
	}

	private void updateButtons() {
		SkinPreset preset = this.presets.selected();
		boolean slim = preset.model() == PlayerModelType.SLIM;
		this.slimArmsButton.visible = slim;
		this.wideArmsButton.visible = !slim;
		this.undoButton.active = preset.canUndo();
		this.redoButton.active = preset.canRedo();
		this.importButton.active = !this.importing;
		this.uploadButton.active = !this.uploading;
		this.eraserButton.active = this.canErase();
		if (this.canErase()) {
			return;
		}

		if (this.tool == SkinTool.ERASER) {
			this.tool = SkinTool.PENCIL;
		}

		if (this.paintTool == SkinTool.ERASER) {
			this.paintTool = SkinTool.PENCIL;
		}
	}

	private Button addModelButton(final int x, final int top, final String sprite, final Component message, final Button.OnPress onPress) {
		SpriteIconButton button = SpriteIconButton.builder(message, onPress, true).sprite(Argent.id(sprite), SPRITE_SIZE, SPRITE_SIZE).size(BUTTON_SIZE, BUTTON_SIZE).build();
		button.setPosition(x, top + MODEL_BUTTON_Y);
		button.setTooltip(Tooltip.create(message));
		return this.addRenderableWidget(button);
	}

	private void setArmModel(final PlayerModelType model) {
		this.presets.selected().setModel(model);
		this.updateButtons();
	}

	@Override
	public void tick() {
		this.updateButtons();
		if (++this.ticksSinceSave >= AUTOSAVE_TICKS) {
			this.ticksSinceSave = 0;
			this.save();
		}
	}

	@Override
	public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
		int x = this.backgroundLeft() + COLOUR_X;
		int y = this.backgroundTop() + COLOUR_Y;
		if (event.button() == InputConstants.MOUSE_BUTTON_LEFT && event.x() >= x && event.x() < x + COLOUR_SIZE && event.y() >= y && event.y() < y + COLOUR_SIZE) {
			this.setColourPickerOpen(!this.colourPicker.visible);
			return true;
		}

		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public boolean keyPressed(final KeyEvent event) {
		if (event.isEscape() && this.colourPicker.visible) {
			this.setColourPickerOpen(false);
			return true;
		}

		if (this.getFocused() == this.importField) {
			return super.keyPressed(event);
		}

		if (event.hasControlDownWithQuirk() && event.shortcutKey() == InputConstants.KEYCODE_Z) {
			if (event.hasShiftDown()) {
				this.presets.selected().redo();
			} else {
				this.presets.selected().undo();
			}

			this.updateButtons();
			return true;
		}

		if (event.hasControlDownWithQuirk() && event.shortcutKey() == InputConstants.KEYCODE_Y) {
			this.presets.selected().redo();
			this.updateButtons();
			return true;
		}

		return super.keyPressed(event);
	}

	private void save() {
		this.presets.save();
		ArgentClient.sharedSkins().sendSelected(this.presets);
	}

	@Override
	public void removed() {
		this.save();
	}

	@Override
	public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
		super.extractRenderState(graphics, mouseX, mouseY, a);
		int left = this.backgroundLeft();
		int top = this.backgroundTop();
		graphics.text(this.font, this.title, left + TITLE_X, top + TITLE_Y, CommonColors.DARK_GRAY, false);
		int x = left + COLOUR_X;
		int y = top + COLOUR_Y;
		graphics.fill(x, y, x + COLOUR_SIZE, y + COLOUR_SIZE, COLOUR_BORDER);
		graphics.fill(x + 1, y + 1, x + COLOUR_SIZE - 1, y + COLOUR_SIZE - 1, CommonColors.WHITE);
		graphics.fill(x + 1, y + 1, x + COLOUR_SIZE - 1, y + COLOUR_SIZE - 1, this.colour);
	}

	@Override
	public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
		super.extractBackground(graphics, mouseX, mouseY, a);
		graphics.blit(
			RenderPipelines.GUI_TEXTURED, BACKGROUND_LOCATION, this.backgroundLeft(), this.backgroundTop(), 0.0F, 0.0F, IMAGE_WIDTH, IMAGE_HEIGHT, IMAGE_WIDTH, IMAGE_HEIGHT
		);
	}
}
