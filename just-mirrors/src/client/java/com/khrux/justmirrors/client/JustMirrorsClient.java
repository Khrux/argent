package com.khrux.justmirrors.client;

import com.khrux.justmirrors.JustMirrors;
import com.khrux.justmirrors.client.gui.screens.MirrorScreen;
import com.khrux.justmirrors.client.renderer.MirrorReflections;
import com.khrux.justmirrors.client.renderer.blockentity.MirrorRenderer;
import com.khrux.justmirrors.client.skin.SharedSkins;
import com.khrux.justmirrors.client.skin.SkinPresets;
import com.khrux.justmirrors.config.JustMirrorsConfig;
import com.khrux.justmirrors.network.protocol.PlayerSkinUpdatePayload;
import com.khrux.justmirrors.world.level.block.JustMirrorsBlocks;
import com.khrux.justmirrors.world.level.block.entity.JustMirrorsBlockEntityTypes;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class JustMirrorsClient implements ClientModInitializer {
	private static final SharedSkins SHARED_SKINS = new SharedSkins();
	private static final KeyMapping.Category KEY_CATEGORY = KeyMapping.Category.register(JustMirrors.id("mirrors"));
	private static final KeyMapping OPEN_SKIN_EDITOR = KeyMappingHelper.registerKeyMapping(
		new KeyMapping("key.justmirrors.open_skin_editor", InputConstants.UNKNOWN.getValue(), KEY_CATEGORY)
	);
	private static SkinPresets presets;
	private static MirrorReflections reflections;

	@Override
	public void onInitializeClient() {
		ClientLifecycleEvents.CLIENT_STARTED.register(minecraft -> {
			presets = SkinPresets.load(minecraft);
			reflections = new MirrorReflections();
		});
		UseBlockCallback.EVENT.register(JustMirrorsClient::useMirror);
		ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
			while (OPEN_SKIN_EDITOR.consumeClick()) {
				openSkinEditor(minecraft);
			}
		});
		BlockEntityRenderers.register(JustMirrorsBlockEntityTypes.MIRROR, MirrorRenderer::new);
		LevelExtractionEvents.END_EXTRACTION.register(context -> reflections.extract(context));
		ClientPlayNetworking.registerGlobalReceiver(PlayerSkinUpdatePayload.TYPE, (payload, context) -> SHARED_SKINS.accept(payload));
		ClientPlayConnectionEvents.JOIN.register((listener, sender, minecraft) -> {
			presets.save();
			SHARED_SKINS.sendSelected(presets);
		});
		ClientPlayConnectionEvents.DISCONNECT.register((listener, minecraft) -> {
			SHARED_SKINS.clear();
			reflections.clear();
		});
	}

	public static KeyMapping openSkinEditorKey() {
		return OPEN_SKIN_EDITOR;
	}

	public static MirrorReflections reflections() {
		return reflections;
	}

	public static SharedSkins sharedSkins() {
		return SHARED_SKINS;
	}

	public static SkinPresets presets() {
		return presets;
	}

	public static void openSkinEditor(final Minecraft minecraft) {
		minecraft.gui.setScreen(new MirrorScreen(presets));
	}

	private static InteractionResult useMirror(final Player player, final Level level, final InteractionHand hand, final BlockHitResult hitResult) {
		if (!level.isClientSide() || !JustMirrorsConfig.get().mirrorScreen || !level.getBlockState(hitResult.getBlockPos()).is(JustMirrorsBlocks.MIRROR)) {
			return InteractionResult.PASS;
		}

		if (player.isSecondaryUseActive() && (!player.getMainHandItem().isEmpty() || !player.getOffhandItem().isEmpty())) {
			return InteractionResult.PASS;
		}

		openSkinEditor(Minecraft.getInstance());
		return InteractionResult.SUCCESS;
	}
}
