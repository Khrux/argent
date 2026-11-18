package com.khrux.justscrying.world.entity.animal;

import com.khrux.justscrying.world.item.JustScryingItems;
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
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jspecify.annotations.Nullable;

public class PetArmor {
	private static final float REPAIR_FRACTION = 0.125F;
	private static final int DAMAGE_COOLDOWN = 20;

	public static InteractionResult interact(
		final Player player, final Level level, final InteractionHand hand, final Entity entity, final @Nullable EntityHitResult hitResult
	) {
		Item armorItem = armorFor(entity);
		if (armorItem == null || !(entity instanceof TamableAnimal pet) || !pet.isTame() || player.isSpectator()) {
			return InteractionResult.PASS;
		}

		ItemStack itemStack = player.getItemInHand(hand);
		ItemStack armor = pet.getBodyArmorItem();
		if (itemStack.is(Items.SHEARS) && armor.is(armorItem) && !pet.isOwnedBy(player)) {
			return InteractionResult.FAIL;
		}

		if (!pet.isOwnedBy(player) || pet.isBaby()) {
			return InteractionResult.PASS;
		}

		if (itemStack.is(armorItem) && !pet.isWearingBodyArmor()) {
			if (!level.isClientSide()) {
				pet.setItemSlot(EquipmentSlot.BODY, itemStack.copyWithCount(1));
				pet.setGuaranteedDrop(EquipmentSlot.BODY);
				itemStack.consume(1, player);
			}

			return InteractionResult.SUCCESS;
		}

		if (pet.isInSittingPose() && armor.is(armorItem) && armor.isDamaged() && armor.isValidRepairItem(itemStack)) {
			if (!level.isClientSide()) {
				itemStack.consume(1, player);
				pet.playSound(SoundEvents.WOLF_ARMOR_REPAIR);
				armor.setDamageValue(Math.max(0, armor.getDamageValue() - (int)(armor.getMaxDamage() * REPAIR_FRACTION)));
			}

			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	public static @Nullable Item armorFor(final Entity entity) {
		if (entity instanceof Parrot) {
			return JustScryingItems.PARROT_ARMOR;
		}

		return entity instanceof Cat ? JustScryingItems.CAT_ARMOR : null;
	}

	public static boolean allowDamage(final LivingEntity entity, final DamageSource source, final float damage) {
		Item armorItem = armorFor(entity);
		if (armorItem == null || !(entity.level() instanceof ServerLevel level)) {
			return true;
		}

		ItemStack armor = entity.getItemBySlot(EquipmentSlot.BODY);
		if (!armor.is(armorItem) || source.is(DamageTypeTags.BYPASSES_WOLF_ARMOR) || damage <= 0.0F) {
			return true;
		}

		if (entity.damageCooldownTime > DAMAGE_COOLDOWN / 2 && !source.is(DamageTypeTags.BYPASSES_COOLDOWN)) {
			return false;
		}

		entity.damageCooldownTime = DAMAGE_COOLDOWN;
		int damageBefore = armor.getDamageValue();
		int maxDamage = armor.getMaxDamage();
		armor.hurtAndBreak(Mth.ceil(damage), entity, EquipmentSlot.BODY);
		entity.playSound(SoundEvents.WOLF_ARMOR_DAMAGE);
		level.broadcastDamageEvent(entity, source);
		if (Crackiness.WOLF_ARMOR.byDamage(damageBefore, maxDamage) != Crackiness.WOLF_ARMOR.byDamage(entity.getItemBySlot(EquipmentSlot.BODY))) {
			entity.playSound(SoundEvents.WOLF_ARMOR_CRACK);
			Item shard = entity instanceof Cat ? Items.ARMADILLO_SCUTE : Items.LEATHER;
			level.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, shard), entity.getX(), entity.getY() + 0.5, entity.getZ(), 20, 0.2, 0.1, 0.2, 0.1);
		}

		return false;
	}
}
