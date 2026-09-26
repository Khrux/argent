package com.khrux.argent.world.entity.animal;

import com.khrux.argent.world.item.ArgentItems;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Crackiness;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jspecify.annotations.Nullable;

public class ParrotArmor {
	private static final float REPAIR_FRACTION = 0.125F;
	private static final int DAMAGE_COOLDOWN = 20;

	public static InteractionResult interact(
		final Player player, final Level level, final InteractionHand hand, final Entity entity, final @Nullable EntityHitResult hitResult
	) {
		if (!(entity instanceof Parrot parrot) || !parrot.isTame() || player.isSpectator()) {
			return InteractionResult.PASS;
		}

		ItemStack itemStack = player.getItemInHand(hand);
		ItemStack armor = parrot.getBodyArmorItem();
		if (itemStack.is(Items.SHEARS) && armor.is(ArgentItems.SILVER_PARROT_ARMOR) && !parrot.isOwnedBy(player)) {
			return InteractionResult.FAIL;
		}

		if (!parrot.isOwnedBy(player) || parrot.isBaby()) {
			return InteractionResult.PASS;
		}

		if (itemStack.is(ArgentItems.SILVER_PARROT_ARMOR) && !parrot.isWearingBodyArmor()) {
			if (!level.isClientSide()) {
				parrot.setItemSlot(EquipmentSlot.BODY, itemStack.copyWithCount(1));
				parrot.setGuaranteedDrop(EquipmentSlot.BODY);
				itemStack.consume(1, player);
			}

			return InteractionResult.SUCCESS;
		}

		if (parrot.isInSittingPose() && armor.is(ArgentItems.SILVER_PARROT_ARMOR) && armor.isDamaged() && armor.isValidRepairItem(itemStack)) {
			if (!level.isClientSide()) {
				itemStack.consume(1, player);
				parrot.playSound(SoundEvents.WOLF_ARMOR_REPAIR);
				armor.setDamageValue(Math.max(0, armor.getDamageValue() - (int)(armor.getMaxDamage() * REPAIR_FRACTION)));
			}

			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	public static boolean allowDamage(final LivingEntity entity, final DamageSource source, final float damage) {
		if (!(entity instanceof Parrot parrot) || !(parrot.level() instanceof ServerLevel level)) {
			return true;
		}

		ItemStack armor = parrot.getBodyArmorItem();
		if (!armor.is(ArgentItems.SILVER_PARROT_ARMOR) || source.is(DamageTypeTags.BYPASSES_WOLF_ARMOR) || damage <= 0.0F) {
			return true;
		}

		if (parrot.damageCooldownTime > DAMAGE_COOLDOWN / 2 && !source.is(DamageTypeTags.BYPASSES_COOLDOWN)) {
			return false;
		}

		parrot.damageCooldownTime = DAMAGE_COOLDOWN;
		int damageBefore = armor.getDamageValue();
		int maxDamage = armor.getMaxDamage();
		armor.hurtAndBreak(Mth.ceil(damage), parrot, EquipmentSlot.BODY);
		parrot.playSound(SoundEvents.WOLF_ARMOR_DAMAGE);
		level.broadcastDamageEvent(parrot, source);
		if (Crackiness.WOLF_ARMOR.byDamage(damageBefore, maxDamage) != Crackiness.WOLF_ARMOR.byDamage(parrot.getBodyArmorItem())) {
			parrot.playSound(SoundEvents.WOLF_ARMOR_CRACK);
			level.sendParticles(
				new ItemParticleOption(ParticleTypes.ITEM, ArgentItems.SILVER_NUGGET), parrot.getX(), parrot.getY() + 0.5, parrot.getZ(), 20, 0.2, 0.1, 0.2, 0.1
			);
		}

		return false;
	}
}
