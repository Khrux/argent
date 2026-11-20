package com.khrux.justmirrors.datagen;

import com.khrux.justmirrors.world.level.block.JustMirrorsBlocks;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

public class JustMirrorsLanguageProvider extends FabricLanguageProvider {
	public JustMirrorsLanguageProvider(final FabricPackOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	public void generateTranslations(final HolderLookup.Provider registries, final FabricLanguageProvider.TranslationBuilder translationBuilder) {
		translationBuilder.add(JustMirrorsBlocks.MIRROR, "Mirror");
		translationBuilder.add("key.category.justmirrors.mirrors", "Just Mirrors");
		translationBuilder.add("key.justmirrors.open_skin_editor", "Open Skin Editor");
		translationBuilder.add("options.justmirrors.title", "Just Mirrors Settings");
		translationBuilder.add("options.justmirrors.open_skin_editor", "Open Skin Editor...");
		translationBuilder.add("options.justmirrors.skin_editor_key", "Editor Key: %s");
		translationBuilder.add("options.justmirrors.reflections", "Reflections");
		translationBuilder.add("options.justmirrors.reflections.tooltip", "What mirrors reflect. Entities Only is faster: players, mobs and items over plain glass.");
		translationBuilder.add("options.justmirrors.reflections.off", "Off");
		translationBuilder.add("options.justmirrors.reflections.entities", "Entities Only");
		translationBuilder.add("options.justmirrors.reflections.all", "Everything");
		translationBuilder.add("options.justmirrors.reflection_distance", "Reflection Range");
		translationBuilder.add("options.justmirrors.reflection_distance.tooltip", "How many blocks away things still show in mirrors. Shorter is faster.");
		translationBuilder.add("options.justmirrors.mirror_screen", "Mirror Screen");
		translationBuilder.add("options.justmirrors.mirror_screen.tooltip", "Off makes mirrors props: right-clicking does nothing.");
		translationBuilder.add("gui.justmirrors.preset", "Preset %s");
		translationBuilder.add("gui.justmirrors.layer.base", "Base layer: click a part to hide or show it");
		translationBuilder.add("gui.justmirrors.layer.overlay", "Overlay layer: click a part to hide or show it");
		translationBuilder.add("gui.justmirrors.arm_width.slim", "Arms: Slim (3px)");
		translationBuilder.add("gui.justmirrors.arm_width.wide", "Arms: Wide (4px)");
		translationBuilder.add("gui.justmirrors.reset_view", "Reset view");
		translationBuilder.add("gui.justmirrors.face", "%s - %s");
		translationBuilder.add("gui.justmirrors.face.none", "Click the model");
		translationBuilder.add("gui.justmirrors.part.head", "Head");
		translationBuilder.add("gui.justmirrors.part.hat", "Hat");
		translationBuilder.add("gui.justmirrors.part.body", "Body");
		translationBuilder.add("gui.justmirrors.part.jacket", "Jacket");
		translationBuilder.add("gui.justmirrors.part.right_arm", "Right arm");
		translationBuilder.add("gui.justmirrors.part.right_sleeve", "Right sleeve");
		translationBuilder.add("gui.justmirrors.part.left_arm", "Left arm");
		translationBuilder.add("gui.justmirrors.part.left_sleeve", "Left sleeve");
		translationBuilder.add("gui.justmirrors.part.right_leg", "Right leg");
		translationBuilder.add("gui.justmirrors.part.right_pants", "Right trouser leg");
		translationBuilder.add("gui.justmirrors.part.left_leg", "Left leg");
		translationBuilder.add("gui.justmirrors.part.left_pants", "Left trouser leg");
		translationBuilder.add("gui.justmirrors.side.down", "top");
		translationBuilder.add("gui.justmirrors.side.up", "bottom");
		translationBuilder.add("gui.justmirrors.side.north", "front");
		translationBuilder.add("gui.justmirrors.side.south", "back");
		translationBuilder.add("gui.justmirrors.side.west", "right");
		translationBuilder.add("gui.justmirrors.side.east", "left");
		translationBuilder.add("gui.justmirrors.tool.pencil", "Pencil");
		translationBuilder.add("gui.justmirrors.tool.fill", "Fill");
		translationBuilder.add("gui.justmirrors.tool.eraser", "Eraser (overlay layer only)");
		translationBuilder.add("gui.justmirrors.import.field", "Import a skin");
		translationBuilder.add("gui.justmirrors.import.hint", "Skin URL, username or UUID");
		translationBuilder.add("gui.justmirrors.import", "Import");
		translationBuilder.add("gui.justmirrors.import.failed", "Couldn't import skin");
		translationBuilder.add("gui.justmirrors.file", "File");
		translationBuilder.add("gui.justmirrors.file.tooltip", "Open the presets folder. To import a PNG, drop it onto this screen.");
		translationBuilder.add("gui.justmirrors.reset", "Reset");
		translationBuilder.add("gui.justmirrors.upload", "Upload");
		translationBuilder.add("gui.justmirrors.upload.tooltip", "Upload the selected preset as your Minecraft skin");
		translationBuilder.add("gui.justmirrors.upload.title", "Upload preset %s?");
		translationBuilder.add("gui.justmirrors.upload.message", "This replaces your skin on your Minecraft account, for every server and launcher.");
		translationBuilder.add("gui.justmirrors.upload.done", "Skin uploaded");
		translationBuilder.add("gui.justmirrors.upload.failed", "Couldn't upload skin");
		translationBuilder.add("gui.justmirrors.rejoin.title", "Rejoin to show your new skin?");
		translationBuilder.add("gui.justmirrors.rejoin.message", "Other players on %s see your new skin after you rejoin.");
		translationBuilder.add("gui.justmirrors.reset.title", "Reset preset %s?");
		translationBuilder.add("gui.justmirrors.reset.message", "It goes back to its default skin. Undo can bring your painting back while this screen stays open.");
		translationBuilder.add("gui.justmirrors.tool.picker", "Colour picker");
		translationBuilder.add("gui.justmirrors.undo", "Undo (Ctrl+Z)");
		translationBuilder.add("gui.justmirrors.redo", "Redo (Ctrl+Y)");
		translationBuilder.add("gui.justmirrors.colour", "Colour");
		translationBuilder.add("gui.justmirrors.palette", "Recent colours");
	}
}
