package com.khrux.argent.world.entity.animal;

import com.khrux.argent.config.ArgentConfig;
import com.khrux.argent.network.protocol.ScryingBitePayload;
import com.khrux.argent.network.protocol.ScryingEndPayload;
import com.khrux.argent.network.protocol.ScryingLinkPayload;
import com.khrux.argent.network.protocol.ScryingMovePayload;
import com.khrux.argent.world.item.enchantment.ArgentEnchantments;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class Scrying {
	public static final double RANGE = 64.0;
	private static final double MAX_STEP = 1.5;
	private static final double BITE_RANGE = 3.0;
	private static final int BITE_COOLDOWN = 10;
	private static final Map<UUID, Scrying.Link> LINKS = new HashMap<>();

	public static void bootstrap() {
		PayloadTypeRegistry.clientboundPlay().register(ScryingLinkPayload.TYPE, ScryingLinkPayload.STREAM_CODEC);
		PayloadTypeRegistry.serverboundPlay().register(ScryingMovePayload.TYPE, ScryingMovePayload.STREAM_CODEC);
		PayloadTypeRegistry.serverboundPlay().register(ScryingBitePayload.TYPE, ScryingBitePayload.STREAM_CODEC);
		PayloadTypeRegistry.serverboundPlay().register(ScryingEndPayload.TYPE, ScryingEndPayload.STREAM_CODEC);
		ServerPlayNetworking.registerGlobalReceiver(ScryingMovePayload.TYPE, (payload, context) -> move(context.player(), payload));
		ServerPlayNetworking.registerGlobalReceiver(ScryingBitePayload.TYPE, (payload, context) -> bite(context.player(), payload));
		ServerPlayNetworking.registerGlobalReceiver(ScryingEndPayload.TYPE, (payload, context) -> stop(context.player()));
		UseEntityCallback.EVENT.register(Scrying::interact);
		ServerTickEvents.END_SERVER_TICK.register(Scrying::tick);
		ServerLivingEntityEvents.AFTER_DAMAGE.register(Scrying::afterDamage);
		ServerPlayConnectionEvents.DISCONNECT.register((listener, server) -> LINKS.remove(listener.player.getUUID()));
	}

	public static boolean isLinkedPet(final Entity entity) {
		for (Scrying.Link link : LINKS.values()) {
			if (link.pet == entity) {
				return true;
			}
		}

		return false;
	}

	public static boolean canLink(final Player player, final InteractionHand hand, final Entity entity) {
		return ArgentConfig.get().scrying
			&& player.isSecondaryUseActive()
			&& hand == InteractionHand.MAIN_HAND
			&& player.getMainHandItem().isEmpty()
			&& !player.isSpectator()
			&& (entity instanceof Wolf || entity instanceof Parrot)
			&& entity instanceof TamableAnimal pet
			&& pet.isTame()
			&& pet.isOwnedBy(player)
			&& !pet.isBaby()
			&& hasScrying(pet)
			&& Leashable.leashableInArea(pet, leashable -> leashable.getLeashHolder() == player).isEmpty();
	}

	private static boolean hasScrying(final LivingEntity pet) {
		Holder<Enchantment> scrying = pet.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ArgentEnchantments.SCRYING);
		return EnchantmentHelper.getItemEnchantmentLevel(scrying, pet.getItemBySlot(EquipmentSlot.BODY)) > 0;
	}

	private static InteractionResult interact(
		final Player player, final Level level, final InteractionHand hand, final Entity entity, final @Nullable EntityHitResult hitResult
	) {
		if (!canLink(player, hand, entity)) {
			return InteractionResult.PASS;
		}

		if (player instanceof ServerPlayer serverPlayer && entity instanceof TamableAnimal pet && !LINKS.containsKey(player.getUUID())) {
			pet.setOrderedToSit(false);
			pet.setInSittingPose(false);
			pet.setTarget(null);
			pet.getNavigation().stop();
			LINKS.put(player.getUUID(), new Scrying.Link(pet));
			ServerPlayNetworking.send(serverPlayer, new ScryingLinkPayload(pet.getId()));
		}

		return InteractionResult.SUCCESS;
	}

	private static void tick(final MinecraftServer server) {
		for (UUID playerId : List.copyOf(LINKS.keySet())) {
			ServerPlayer player = server.getPlayerList().getPlayer(playerId);
			TamableAnimal pet = LINKS.get(playerId).pet;
			if (player == null) {
				LINKS.remove(playerId);
			} else if (!player.isAlive() || !pet.isAlive() || pet.level() != player.level() || pet.distanceTo(player) > RANGE) {
				stop(player);
			}
		}
	}

	private static void afterDamage(final LivingEntity entity, final DamageSource source, final float baseDamageTaken, final float damageTaken, final boolean blocked) {
		if (entity instanceof ServerPlayer player && LINKS.containsKey(player.getUUID())) {
			stop(player);
		}
	}

	private static void move(final ServerPlayer player, final ScryingMovePayload payload) {
		Scrying.Link link = LINKS.get(player.getUUID());
		if (link == null) {
			return;
		}

		TamableAnimal pet = link.pet;
		Vec3 position = payload.position();
		if (position.distanceTo(player.position()) > RANGE) {
			stop(player);
			return;
		}

		if (position.distanceTo(pet.position()) > MAX_STEP) {
			return;
		}

		pet.absSnapTo(position.x, position.y, position.z, payload.yRot(), payload.xRot());
		pet.setYHeadRot(payload.yRot());
		pet.setYBodyRot(payload.yRot());
		pet.setOnGround(payload.onGround());
		pet.resetFallDistance();
	}

	private static void bite(final ServerPlayer player, final ScryingBitePayload payload) {
		Scrying.Link link = LINKS.get(player.getUUID());
		if (link == null || !(link.pet instanceof Wolf wolf) || !(wolf.level() instanceof ServerLevel level)) {
			return;
		}

		Entity target = level.getEntity(payload.targetId());
		long time = level.getGameTime();
		if (target == null || target == player || target == wolf || wolf.distanceTo(target) > BITE_RANGE || time - link.lastBite < BITE_COOLDOWN) {
			return;
		}

		link.lastBite = time;
		wolf.doHurtTarget(level, target);
	}

	private static void stop(final ServerPlayer player) {
		if (LINKS.remove(player.getUUID()) != null) {
			ServerPlayNetworking.send(player, new ScryingLinkPayload(0));
		}
	}

	private static class Link {
		private final TamableAnimal pet;
		private long lastBite;

		private Link(final TamableAnimal pet) {
			this.pet = pet;
		}
	}
}
