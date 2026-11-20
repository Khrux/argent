package com.khrux.argent.client.gui.screens;

import com.khrux.argent.client.gui.components.SectionPicture;
import com.khrux.argent.config.ArgentConfig;
import com.khrux.justmirrors.client.gui.components.SkinEditorKeyBinding;
import com.khrux.justmirrors.client.gui.screens.JustMirrorsOptions;
import com.khrux.justmirrors.world.item.JustMirrorsItems;
import com.khrux.justscrying.client.gui.components.PetArmorRecipeGrid;
import com.khrux.justscrying.client.gui.screens.JustScryingConfigScreen;
import com.khrux.justscrying.client.gui.screens.JustScryingOptions;
import com.khrux.justscrying.config.JustScryingConfig;
import com.khrux.justsilver.client.gui.components.RecipeGrid;
import com.khrux.justsilver.client.gui.screens.JustSilverOptions;
import com.khrux.justsilver.config.JustSilverConfig;
import com.khrux.justsilver.world.item.JustSilverItems;
import com.khrux.justspawnlanterns.client.gui.screens.JustSpawnLanternsOptions;
import com.khrux.justwitherzombies.client.gui.screens.JustWitherZombiesOptions;
import com.khrux.justwitherzombies.config.JustWitherZombiesConfig;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Checkbox;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

public class ArgentConfigScreen extends OptionsSubScreen {
	private static final Component TITLE = Component.translatable("options.argent.title");
	private static final Component MODE_TOOLTIP = Component.translatable("options.argent.mode.tooltip");
	private static final Component RECIPE_TOOLTIP = Component.translatable("options.argent.recipe.tooltip");
	private static final Component MIRROR_RECIPE = Component.translatable("options.argent.mirror_recipe").withStyle(ChatFormatting.GRAY);
	private static final Component SOUL_LANTERN_RECIPE = Component.translatable("options.justsilver.soul_lantern_recipe").withStyle(ChatFormatting.GRAY);
	private static final Component IRON = Component.translatable("options.justsilver.soul_lantern_recipe.iron");
	private static final Component SILVER = Component.translatable("options.justsilver.soul_lantern_recipe.silver");
	private static final Component WITHER_ZOMBIE = Component.translatable("options.argent.books_looted_from.wither_zombie");
	private static final Component WITHER_ZOMBIE_TOOLTIP = Component.translatable("options.justwitherzombies.scrying_books.tooltip");
	private static final int TILE_WIDTH = 72;
	private static final int PICTURE_HEIGHT = 50;
	private static final int TILE_SPACING = 2;
	private static final int TILE_GAP = 6;
	private static final int HEADER_PADDING = 8;
	private static final int TITLE_SPACING = 6;
	private final SkinEditorKeyBinding skinEditorKey = new SkinEditorKeyBinding(Minecraft.getInstance());
	private final Map<AbstractWidget, Supplier<List<ClientTooltipComponent>>> recipeGrids = new HashMap<>();
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
					this.minecraft.gui.setScreen(new ArgentConfigScreen(this.lastScreen));
				})
		);
		return tile;
	}

	@Override
	protected void repositionElements() {
		if (columns(this.width) != this.columns) {
			this.minecraft.gui.setScreen(new ArgentConfigScreen(this.lastScreen));
			return;
		}

		super.repositionElements();
	}

	@Override
	protected void addOptions() {
		this.recipeGrids.clear();
		ArgentConfig config = ArgentConfig.get();
		for (ArgentConfig.Section section : ArgentConfig.Section.values()) {
			if (config.mode(section) == ArgentConfig.Mode.CUSTOM) {
				this.list.addHeader(section.getDisplayName());
				this.addCustomOptions(section);
			}
		}
	}

	private void addCustomOptions(final ArgentConfig.Section section) {
		ArgentConfig config = ArgentConfig.get();
		JustSilverConfig silver = JustSilverConfig.get();
		JustScryingConfig scrying = JustScryingConfig.get();
		switch (section) {
			case SILVER -> this.list
				.addSmall(
					List.of(
						this.button(JustSilverOptions.oreGeneration()),
						this.recipe(this.button(JustSilverOptions.silverBellRecipe(OptionInstance.noTooltip())), () -> this.grid(RecipeGrid.silverBell()))
					)
				);
			case MIRRORS -> {
				this.list
					.addSmall(
						List.of(
							this.skinEditorKey.openButton(),
							this.skinEditorKey.keyButton(),
							this.button(JustMirrorsOptions.reflections()),
							this.button(JustMirrorsOptions.reflectionDistance()),
							this.button(JustMirrorsOptions.mirrorScreen())
						)
					);
				this.list.addHeader(MIRROR_RECIPE);
				this.list
					.addSmall(
						List.of(
							this.recipe(
								this.silverCheckbox(ArgentConfig.MirrorRecipe.SILVER.getDisplayName(), config.silverMirrorRecipe, value -> config.silverMirrorRecipe = value),
								() -> config.silverMirrorRecipe ? this.grid(mirrorGrid(ArgentConfig.MirrorRecipe.SILVER)) : null
							),
							this.recipe(
								this.checkbox(ArgentConfig.MirrorRecipe.DIAMOND.getDisplayName(), null, config.diamondMirrorRecipe, value -> config.diamondMirrorRecipe = value),
								() -> config.diamondMirrorRecipe ? this.grid(mirrorGrid(ArgentConfig.MirrorRecipe.DIAMOND)) : null
							)
						)
					);
			}
			case SPAWN_LANTERNS -> {
				this.list.addBig(JustSpawnLanternsOptions.markers());
				this.list.addHeader(SOUL_LANTERN_RECIPE);
				this.list
					.addSmall(
						List.of(
							this.recipe(this.checkbox(IRON, null, silver.soulLanternIron, value -> silver.soulLanternIron = value), () -> this.grid(RecipeGrid.soulLantern())),
							this.recipe(
								this.silverCheckbox(SILVER, silver.soulLanternSilver, value -> silver.soulLanternSilver = value), () -> this.grid(RecipeGrid.soulLantern())
							)
						)
					);
			}
			case WITHER_ZOMBIE -> this.list.addSmall(JustWitherZombiesOptions.meleeDamage(), JustWitherZombiesOptions.itemStealing());
			case SCRYING -> {
				this.list.addBig(JustScryingOptions.effect());
				this.list.addHeader(JustScryingOptions.BOOKS_LOOTED_FROM);
				JustWitherZombiesConfig witherZombies = JustWitherZombiesConfig.get();
				this.list
					.addSmall(
						List.of(
							this.checkbox(WITHER_ZOMBIE, WITHER_ZOMBIE_TOOLTIP, witherZombies.scryingBooks, value -> witherZombies.scryingBooks = value),
							this.checkbox(JustScryingOptions.ELDER_GUARDIAN, JustScryingOptions.ELDER_GUARDIAN_TOOLTIP, scrying.elderGuardianBook, value -> scrying.elderGuardianBook = value),
							this.checkbox(JustScryingOptions.FISHING_LOOT, JustScryingOptions.FISHING_LOOT_TOOLTIP, scrying.fishingBooks, value -> scrying.fishingBooks = value)
						)
					);
				this.list.addHeader(JustScryingOptions.ARMOR_RECIPES);
				this.list
					.addSmall(
						List.of(
							this.recipe(
								this.checkbox(JustScryingOptions.PARROT, null, scrying.parrotArmorRecipe, value -> scrying.parrotArmorRecipe = value), () -> this.grid(PetArmorRecipeGrid.parrotArmor())
							),
							this.recipe(this.checkbox(JustScryingOptions.CAT, null, scrying.catArmorRecipe, value -> scrying.catArmorRecipe = value), () -> this.grid(PetArmorRecipeGrid.catArmor()))
						)
					);
			}
		}
	}

	private AbstractWidget button(final OptionInstance<?> option) {
		return option.createButton(this.options);
	}

	private Checkbox checkbox(final Component label, final @Nullable Component tooltip, final boolean selected, final BooleanConsumer onChange) {
		return JustScryingConfigScreen.checkbox(this, label, tooltip, selected, onChange);
	}

	private Checkbox silverCheckbox(final Component label, final boolean selected, final BooleanConsumer onChange) {
		if (JustSilverConfig.get().isEnabled()) {
			return this.checkbox(label, null, selected, onChange);
		}

		Checkbox checkbox = this.checkbox(label.copy().withStyle(ChatFormatting.STRIKETHROUGH), null, selected, onChange);
		checkbox.active = false;
		return checkbox;
	}

	private AbstractWidget recipe(final AbstractWidget widget, final Supplier<@Nullable List<ClientTooltipComponent>> grid) {
		this.recipeGrids.put(widget, grid);
		return widget;
	}

	private @Nullable List<ClientTooltipComponent> grid(final @Nullable RecipeGrid grid) {
		return grid == null ? null : grid.tooltip(this.font, RECIPE_TOOLTIP);
	}

	private @Nullable List<ClientTooltipComponent> grid(final @Nullable PetArmorRecipeGrid grid) {
		return grid == null ? null : grid.tooltip(this.font, RECIPE_TOOLTIP);
	}

	@Override
	public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float partialTicks) {
		super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
		for (Map.Entry<AbstractWidget, Supplier<List<ClientTooltipComponent>>> entry : this.recipeGrids.entrySet()) {
			if (entry.getKey().isHovered()) {
				List<ClientTooltipComponent> grid = entry.getValue().get();
				if (grid != null) {
					graphics.tooltip(this.font, grid, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null, false);
				}

				return;
			}
		}
	}

	private static RecipeGrid mirrorGrid(final ArgentConfig.MirrorRecipe recipe) {
		Item s = Items.STICK;
		Item core = recipe == ArgentConfig.MirrorRecipe.SILVER ? JustSilverItems.SILVER_INGOT : Items.DIAMOND;
		return RecipeGrid.of(JustMirrorsItems.MIRROR, recipe == ArgentConfig.MirrorRecipe.SILVER, s, s, s, s, core, s, s, s, s);
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
}
