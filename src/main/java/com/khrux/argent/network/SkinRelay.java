package com.khrux.argent.network;

import com.khrux.argent.network.protocol.PlayerSkinUpdatePayload;
import com.khrux.argent.network.protocol.SkinUpdatePayload;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class SkinRelay {
	private static final Map<UUID, PlayerSkinUpdatePayload> SKINS = new HashMap<>();

	public static void bootstrap() {
		PayloadTypeRegistry.serverboundPlay().register(SkinUpdatePayload.TYPE, SkinUpdatePayload.STREAM_CODEC);
		PayloadTypeRegistry.clientboundPlay().register(PlayerSkinUpdatePayload.TYPE, PlayerSkinUpdatePayload.STREAM_CODEC);
		ServerPlayNetworking.registerGlobalReceiver(SkinUpdatePayload.TYPE, (payload, context) -> update(context.server(), context.player(), payload));
		ServerPlayConnectionEvents.JOIN.register((listener, sender, server) -> sendAll(listener.player));
		ServerPlayConnectionEvents.DISCONNECT.register((listener, server) -> remove(server, listener.player));
	}

	private static void update(final MinecraftServer server, final ServerPlayer player, final SkinUpdatePayload payload) {
		PlayerSkinUpdatePayload update = new PlayerSkinUpdatePayload(player.getUUID(), payload.skin(), payload.model());
		SKINS.put(player.getUUID(), update);
		broadcast(server, player, update);
	}

	private static void remove(final MinecraftServer server, final ServerPlayer player) {
		if (SKINS.remove(player.getUUID()) != null) {
			broadcast(server, player, PlayerSkinUpdatePayload.removed(player.getUUID()));
		}
	}

	private static void broadcast(final MinecraftServer server, final ServerPlayer source, final PlayerSkinUpdatePayload update) {
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			if (player != source && ServerPlayNetworking.canSend(player, PlayerSkinUpdatePayload.TYPE)) {
				ServerPlayNetworking.send(player, update);
			}
		}
	}

	private static void sendAll(final ServerPlayer player) {
		if (!ServerPlayNetworking.canSend(player, PlayerSkinUpdatePayload.TYPE)) {
			return;
		}

		for (PlayerSkinUpdatePayload update : SKINS.values()) {
			if (!update.player().equals(player.getUUID())) {
				ServerPlayNetworking.send(player, update);
			}
		}
	}
}
