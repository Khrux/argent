package com.khrux.argent.client;

import com.khrux.argent.Argent;
import com.khrux.argent.client.gui.screens.MirrorScreen;
import com.khrux.argent.client.model.geom.ArgentModelLayers;
import com.khrux.argent.client.model.monster.WitherZombieModel;
import com.khrux.argent.client.particle.SoulLanternParticles;
import com.khrux.argent.client.player.ScryingView;
import com.khrux.argent.client.player.WitherZombieGaze;
import com.khrux.argent.client.renderer.MirrorReflections;
import com.khrux.argent.client.renderer.blockentity.MirrorRenderer;
import com.khrux.argent.client.renderer.blockentity.SilverBellRenderer;
import com.khrux.argent.client.renderer.entity.ArmoredParrotRenderer;
import com.khrux.argent.client.renderer.entity.PlainArmorWolfRenderer;
import com.khrux.argent.client.renderer.entity.WitherZombieRenderer;
import com.khrux.argent.client.renderer.entity.layers.SilverParrotArmorLayer;
import com.khrux.argent.client.renderer.special.ReflectiveShieldSpecialRenderer;
import com.khrux.argent.client.skin.SharedSkins;
import com.khrux.argent.client.skin.SkinPresets;
import com.khrux.argent.config.ArgentConfig;
import com.khrux.argent.network.protocol.PlayerSkinUpdatePayload;
import com.khrux.argent.network.protocol.ScryingLinkPayload;
import com.khrux.argent.world.entity.ArgentEntityTypes;
import com.khrux.argent.world.level.block.ArgentBlocks;
import com.khrux.argent.world.level.block.entity.ArgentBlockEntityTypes;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class ArgentClient implements ClientModInitializer {
	private static final SharedSkins SHARED_SKINS = new SharedSkins();
	private static final KeyMapping.Category KEY_CATEGORY = KeyMapping.Category.register(Argent.id("argent"));
	private static final KeyMapping OPEN_SKIN_EDITOR = KeyMappingHelper.registerKeyMapping(
		new KeyMapping("key.argent.open_skin_editor", InputConstants.UNKNOWN.getValue(), KEY_CATEGORY)
	);
	private static final WitherZombieGaze WITHER_ZOMBIE_GAZE = new WitherZombieGaze();
	private static final ScryingView SCRYING_VIEW = new ScryingView();
	private static SkinPresets presets;
	private static MirrorReflections reflections;

	@Override
	public void onInitializeClient() {
		ClientLifecycleEvents.CLIENT_STARTED.register(minecraft -> {
			presets = SkinPresets.load(minecraft);
			reflections = new MirrorReflections();
		});
		ReflectiveShieldSpecialRenderer.bootstrap();
		UseBlockCallback.EVENT.register(ArgentClient::useMirror);
		ClientTickEvents.END_LEVEL_TICK.register(SoulLanternParticles::tick);
		ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
			while (OPEN_SKIN_EDITOR.consumeClick()) {
				openSkinEditor(minecraft);
			}
		});
		BlockEntityRenderers.register(ArgentBlockEntityTypes.MIRROR, MirrorRenderer::new);
		BlockEntityRenderers.register(ArgentBlockEntityTypes.SILVER_BELL, SilverBellRenderer::new);
		ModelLayerRegistry.registerModelLayer(ArgentModelLayers.WITHER_ZOMBIE, WitherZombieModel::createBodyLayer);
		EntityRenderers.register(ArgentEntityTypes.WITHER_ZOMBIE, WitherZombieRenderer::new);
		ModelLayerRegistry.registerModelLayer(ArgentModelLayers.SILVER_PARROT_ARMOR, SilverParrotArmorLayer::createArmorLayer);
		EntityRenderers.register(EntityTypes.PARROT, ArmoredParrotRenderer::new);
		EntityRenderers.register(EntityTypes.WOLF, PlainArmorWolfRenderer::new);
		LevelExtractionEvents.END_EXTRACTION.register(context -> reflections.extract(context));
		LevelExtractionEvents.END_EXTRACTION.register(WITHER_ZOMBIE_GAZE::extract);
		LevelExtractionEvents.END_EXTRACTION.register(SCRYING_VIEW::extract);
		ClientTickEvents.START_CLIENT_TICK.register(SCRYING_VIEW::startTick);
		ClientTickEvents.END_CLIENT_TICK.register(SCRYING_VIEW::endTick);
		ClientPlayNetworking.registerGlobalReceiver(ScryingLinkPayload.TYPE, (payload, context) -> SCRYING_VIEW.link(context.client(), payload.petId()));
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

	public static ScryingView scryingView() {
		return SCRYING_VIEW;
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
		if (!level.isClientSide() || !ArgentConfig.get().mirrorScreen || !level.getBlockState(hitResult.getBlockPos()).is(ArgentBlocks.MIRROR)) {
			return InteractionResult.PASS;
		}

		if (player.isSecondaryUseActive() && (!player.getMainHandItem().isEmpty() || !player.getOffhandItem().isEmpty())) {
			return InteractionResult.PASS;
		}

		openSkinEditor(Minecraft.getInstance());
		return InteractionResult.SUCCESS;
	}
}
