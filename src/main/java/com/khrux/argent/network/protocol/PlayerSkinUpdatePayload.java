package com.khrux.argent.network.protocol;

import com.khrux.argent.Argent;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.PlayerModelType;

public record PlayerSkinUpdatePayload(UUID player, byte[] skin, PlayerModelType model) implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, PlayerSkinUpdatePayload> STREAM_CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC,
		PlayerSkinUpdatePayload::player,
		ByteBufCodecs.byteArray(SkinUpdatePayload.MAX_SKIN_SIZE),
		PlayerSkinUpdatePayload::skin,
		PlayerModelType.STREAM_CODEC,
		PlayerSkinUpdatePayload::model,
		PlayerSkinUpdatePayload::new
	);
	public static final CustomPacketPayload.Type<PlayerSkinUpdatePayload> TYPE = new CustomPacketPayload.Type<>(Argent.id("player_skin_update"));

	public static PlayerSkinUpdatePayload removed(final UUID player) {
		return new PlayerSkinUpdatePayload(player, new byte[0], PlayerModelType.WIDE);
	}

	public boolean isRemoved() {
		return this.skin.length == 0;
	}

	@Override
	public CustomPacketPayload.Type<PlayerSkinUpdatePayload> type() {
		return TYPE;
	}
}
