package com.khrux.argent.client.gui.screens;

import com.khrux.argent.client.gui.components.RecipeGrid;
import com.khrux.argent.client.gui.components.SectionPicture;
import com.khrux.argent.config.ArgentConfig;
import com.khrux.justmirrors.client.gui.components.SkinEditorKeyBinding;
import com.khrux.justmirrors.client.gui.screens.JustMirrorsOptions;
import com.khrux.justscrying.client.gui.screens.JustScryingOptions;
import com.khrux.justscrying.config.JustScryingConfig;
import com.khrux.justsilver.client.gui.screens.JustSilverOptions;
import com.khrux.justsilver.config.JustSilverConfig;
import com.khrux.justspawnlanterns.client.gui.screens.JustSpawnLanternsOptions;
import com.khrux.justwitherzombies.client.gui.screens.JustWitherZombiesOptions;
import com.khrux.justwitherzombies.config.JustWitherZombiesConfig;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;

public class ArgentConfigScreen extends OptionsSubScreen {
	private static final Component TITLE = Component.translatable("options.argent.title");
	private static final Component MODE_TOOLTIP = Component.translatable("options.argent.mode.tooltip");
	private static final Component RECIPE_TOOLTIP = Component.translatable("options.argent.recipe.tooltip");
	private static final Component SILVER_WARNING = Component.translatable("options.argent.silver_warning").withStyle(ChatFormatting.GOLD);
	private static final int TILE_WIDTH = 72;
	private static final int PICTURE_HEIGHT = 50;
	private static final int TILE_SPACING = 2;
	private static final int TILE_GAP = 6;
	private static final int HEADER_PADDING = 8;
	private static final int TITLE_SPACING = 6;
	private static final int TOOLTIP_WIDTH = 200;
	private final SkinEditorKeyBinding skinEditorKey = new SkinEditorKeyBinding(Minecraft.getInstance());
	private final Map<AbstractWidget, Supplier<RecipeGrid>> recipeGrids = new HashMap<>();
	private int columns;

	public ArgentConfigScreen(final Screen lastScreen) {
		super(lastScreen, Minecraft.getInstance().options, TITLE);
	}

	private static int columns(final int width) {
		int sections = ArgentConfig.Section.values().length;
		return Mth.clamp((width - HEADER_PADDING * 2 + TILE_GAP) / (TILE_WIDTH + TILE_GAP), 1, sections);
	}

	@Override
	protected void addTitle() {
		this.columns = columns(this.width);
		ArgentConfig.Section[] sections = ArgentConfig.Section.values();
		int rows = Mth.positiveCeilDiv(sections.length, this.columns);
		LinearLayout header = LinearLayout.vertical().spacing(TITLE_SPACING);
		header.defaultCellSetting().alignHorizontallyCenter();
		header.addChild(new StringWidget(TITLE, this.font));
		LinearLayout tiles = header.addChild(LinearLayout.vertical().spacing(TILE_GAP));
		tiles.defaultCellSetting().alignHorizontallyCenter();
		LinearLayout row = null;
		for (int i = 0; i < sections.length; i++) {
			if (i % this.columns == 0) {
				row = tiles.addChild(LinearLayout.horizontal().spacing(TILE_GAP));
			}

			row.addChild(this.tile(sections[i]));
		}

		int tileHeight = PICTURE_HEIGHT + TILE_SPACING + 20;
		this.layout.setHeaderHeight(HEADER_PADDING * 2 + this.font.lineHeight + TITLE_SPACING + rows * tileHeight + (rows - 1) * TILE_GAP);
		this.layout.addToHeader(header);
	}

	private LinearLayout tile(final ArgentConfig.Section section) {
		ArgentConfig config = ArgentConfig.get();
		LinearLayout tile = LinearLayout.vertical().spacing(TILE_SPACING);
		tile.addChild(new SectionPicture(TILE_WIDTH, PICTURE_HEIGHT, section, this.font));
		tile.addChild(
			CycleButton.builder(ArgentConfig.Mode::getDisplayName, config.mode(section))
				.withValues(ArgentConfig.Mode.values())
				.displayOnlyValue()
				.withTooltip(value -> Tooltip.create(MODE_TOOLTIP))
				.create(0, 0, TILE_WIDTH, 20, section.getDisplayName(), (button, value) -> {
					config.setMode(section, value);
					this.rebuildWidgets();
				})
		);
		return tile;
	}

	@Override
	protected void repositionElements() {
		if (columns(this.width) != this.columns) {
			this.rebuildWidgets();
			return;
		}

		super.repositionElements();
	}

	@Override
	protected void addOptions() {
		this.recipeGrids.clear();
		ArgentConfig config = ArgentConfig.get();
		for (ArgentConfig.Section section : ArgentConfig.Section.values()) {
			if (config.mode(section) != ArgentConfig.Mode.CUSTOM) {
				continue;
			}

			this.list.addHeader(section.getDisplayName());
			this.list.addSmall(this.customOptions(section));
		}
	}

	private List<AbstractWidget> customOptions(final ArgentConfig.Section section) {
		List<AbstractWidget> widgets = new ArrayList<>();
		switch (section) {
			case SILVER -> {
				widgets.add(this.button(JustSilverOptions.oreGeneration()));
				widgets.add(this.recipe(JustSilverOptions.silverBellRecipe(OptionInstance.noTooltip()), RecipeGrid::silverBell));
				widgets.add(this.recipe(JustSilverOptions.soulLanternRecipe(OptionInstance.noTooltip()), RecipeGrid::soulLantern));
			}
			case MIRRORS -> {
				widgets.add(this.skinEditorKey.openButton());
				widgets.add(this.skinEditorKey.keyButton());
				widgets.add(this.button(JustMirrorsOptions.reflections()));
				widgets.add(this.button(JustMirrorsOptions.reflectionDistance()));
				widgets.add(this.recipe(mirrorRecipe(), () -> RecipeGrid.mirror(ArgentConfig.get().mirrorRecipe)));
				widgets.add(this.button(JustMirrorsOptions.mirrorScreen()));
			}
			case SPAWN_LANTERNS -> widgets.add(this.button(JustSpawnLanternsOptions.markers()));
			case WITHER_ZOMBIE -> {
				widgets.add(this.button(JustWitherZombiesOptions.meleeDamage()));
				widgets.add(this.button(JustWitherZombiesOptions.itemStealing()));
			}
			case SCRYING -> {
				widgets.add(this.button(JustScryingOptions.effect()));
				widgets.add(this.button(bookSource()));
				widgets.add(this.recipe(JustScryingOptions.parrotArmorRecipe(OptionInstance.noTooltip()), RecipeGrid::parrotArmor));
			}
		}

		return widgets;
	}

	private AbstractWidget button(final OptionInstance<?> option) {
		return option.createButton(this.options);
	}

	private AbstractWidget recipe(final OptionInstance<?> option, final Supplier<RecipeGrid> grid) {
		AbstractWidget widget = this.button(option);
		this.recipeGrids.put(widget, grid);
		return widget;
	}

	private static OptionInstance<ArgentConfig.MirrorRecipe> mirrorRecipe() {
		ArgentConfig config = ArgentConfig.get();
		return new OptionInstance<>(
			"options.argent.mirror_recipe",
			OptionInstance.noTooltip(),
			(caption, value) -> Options.genericValueLabel(caption, value.getDisplayName()),
			new OptionInstance.Enum<>(List.of(ArgentConfig.MirrorRecipe.values()), ArgentConfig.MirrorRecipe.CODEC),
			config.mirrorRecipe,
			value -> config.mirrorRecipe = value
		);
	}

	private static OptionInstance<BookSource> bookSource() {
		JustWitherZombiesConfig witherZombies = JustWitherZombiesConfig.get();
		JustScryingConfig scrying = JustScryingConfig.get();
		return new OptionInstance<>(
			"options.argent.book_source",
			OptionInstance.cachedConstantTooltip(Component.translatable("options.argent.book_source.tooltip")),
			(caption, value) -> Options.genericValueLabel(caption, value.getDisplayName()),
			new OptionInstance.Enum<>(List.of(BookSource.values()), BookSource.CODEC),
			BookSource.of(witherZombies.scryingBooks, scrying.libraryBooks),
			value -> {
				witherZombies.scryingBooks = value.witherZombie;
				scrying.libraryBooks = value.library;
			}
		);
	}

	@Override
	public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float partialTicks) {
		super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
		for (Map.Entry<AbstractWidget, Supplier<RecipeGrid>> entry : this.recipeGrids.entrySet()) {
			if (entry.getKey().isHovered()) {
				graphics.tooltip(this.font, this.recipeTooltip(entry.getValue().get()), mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null, false);
				return;
			}
		}
	}

	private List<ClientTooltipComponent> recipeTooltip(final RecipeGrid grid) {
		List<ClientTooltipComponent> lines = new ArrayList<>();
		this.font.split(RECIPE_TOOLTIP, TOOLTIP_WIDTH).forEach(line -> lines.add(ClientTooltipComponent.create(line)));
		if (grid.needsSilver() && !JustSilverConfig.get().generatesOre()) {
			this.font.split(SILVER_WARNING, TOOLTIP_WIDTH).forEach(line -> lines.add(ClientTooltipComponent.create(line)));
		}

		lines.add(grid);
		return lines;
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
		ArgentConfig.get().saveAll();
	}

	private enum BookSource implements StringRepresentable {
		WITHER_ZOMBIE("wither_zombie", true, false),
		LIBRARY("library", false, true),
		BOTH("both", true, true),
		NEITHER("neither", false, false);

		public static final Codec<BookSource> CODEC = StringRepresentable.fromEnum(BookSource::values);
		private final String name;
		private final boolean witherZombie;
		private final boolean library;

		BookSource(final String name, final boolean witherZombie, final boolean library) {
			this.name = name;
			this.witherZombie = witherZombie;
			this.library = library;
		}

		private static BookSource of(final boolean witherZombie, final boolean library) {
			for (BookSource source : values()) {
				if (source.witherZombie == witherZombie && source.library == library) {
					return source;
				}
			}

			return NEITHER;
		}

		public Component getDisplayName() {
			return Component.translatable("options.argent.book_source." + this.name);
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}
	}
}
