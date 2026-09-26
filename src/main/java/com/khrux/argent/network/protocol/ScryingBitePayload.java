package com.khrux.argent.network.protocol;

import com.khrux.argent.Argent;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ScryingBitePayload(int targetId) implements CustomPacketPayload {
	public static final StreamCodec<ByteBuf, ScryingBitePayload> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(ScryingBitePayload::new, ScryingBitePayload::targetId);
	public static final CustomPacketPayload.Type<ScryingBitePayload> TYPE = new CustomPacketPayload.Type<>(Argent.id("scrying_bite"));

	@Override
	public CustomPacketPayload.Type<ScryingBitePayload> type() {
		return TYPE;
	}
}
