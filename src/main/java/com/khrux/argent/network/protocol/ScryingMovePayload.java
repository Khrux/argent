package com.khrux.argent.network.protocol;

import com.khrux.argent.Argent;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;

public record ScryingMovePayload(Vec3 position, float yRot, float xRot, boolean onGround) implements CustomPacketPayload {
	public static final StreamCodec<ByteBuf, ScryingMovePayload> STREAM_CODEC = StreamCodec.composite(
		Vec3.STREAM_CODEC,
		ScryingMovePayload::position,
		ByteBufCodecs.FLOAT,
		ScryingMovePayload::yRot,
		ByteBufCodecs.FLOAT,
		ScryingMovePayload::xRot,
		ByteBufCodecs.BOOL,
		ScryingMovePayload::onGround,
		ScryingMovePayload::new
	);
	public static final CustomPacketPayload.Type<ScryingMovePayload> TYPE = new CustomPacketPayload.Type<>(Argent.id("scrying_move"));

	@Override
	public CustomPacketPayload.Type<ScryingMovePayload> type() {
		return TYPE;
	}
}
