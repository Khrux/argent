package com.khrux.argent.network.protocol;

import com.khrux.argent.Argent;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ScryingEndPayload() implements CustomPacketPayload {
	public static final ScryingEndPayload INSTANCE = new ScryingEndPayload();
	public static final StreamCodec<ByteBuf, ScryingEndPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);
	public static final CustomPacketPayload.Type<ScryingEndPayload> TYPE = new CustomPacketPayload.Type<>(Argent.id("scrying_end"));

	@Override
	public CustomPacketPayload.Type<ScryingEndPayload> type() {
		return TYPE;
	}
}
