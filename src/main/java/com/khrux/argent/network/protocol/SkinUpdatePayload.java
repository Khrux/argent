package com.khrux.argent.network.protocol;

import com.khrux.argent.Argent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.PlayerModelType;

public record SkinUpdatePayload(byte[] skin, PlayerModelType model) implements CustomPacketPayload {
	public static final int MAX_SKIN_SIZE = 24576;
	public static final StreamCodec<FriendlyByteBuf, SkinUpdatePayload> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.byteArray(MAX_SKIN_SIZE), SkinUpdatePayload::skin, PlayerModelType.STREAM_CODEC, SkinUpdatePayload::model, SkinUpdatePayload::new
	);
	public static final CustomPacketPayload.Type<SkinUpdatePayload> TYPE = new CustomPacketPayload.Type<>(Argent.id("skin_update"));

	@Override
	public CustomPacketPayload.Type<SkinUpdatePayload> type() {
		return TYPE;
	}
}
