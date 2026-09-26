package com.khrux.argent.network.protocol;

import com.khrux.argent.Argent;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ScryingLinkPayload(int petId) implements CustomPacketPayload {
	public static final StreamCodec<ByteBuf, ScryingLinkPayload> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(ScryingLinkPayload::new, ScryingLinkPayload::petId);
	public static final CustomPacketPayload.Type<ScryingLinkPayload> TYPE = new CustomPacketPayload.Type<>(Argent.id("scrying_link"));

	@Override
	public CustomPacketPayload.Type<ScryingLinkPayload> type() {
		return TYPE;
	}
}
