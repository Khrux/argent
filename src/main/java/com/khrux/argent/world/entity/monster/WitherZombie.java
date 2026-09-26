package com.khrux.argent.world.entity.monster;

import com.khrux.argent.sounds.ArgentSoundEvents;
import com.khrux.argent.tags.ArgentItemTags;
import com.khrux.argent.world.item.ArgentItems;
import com.khrux.argent.world.item.enchantment.ArgentEnchantments;
import com.khrux.argent.world.level.block.MirrorBlock;
import com.khrux.argent.world.level.block.entity.MirrorBlockEntity;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class WitherZombie extends Monster implements InventoryCarrier {
	private static final EntityDataAccessor<Integer> DATA_TRACKED_PLAYER = SynchedEntityData.defineId(WitherZombie.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> DATA_SHATTERING = SynchedEntityData.defineId(WitherZombie.class, EntityDataSerializers.BOOLEAN);
	public static final int SHATTER_DURATION = 40;
	private static final double NOTICE_CONE = Math.cos(Math.toRadians(45.0));
	private static final double HEARING_RANGE = 12.0;
	private static final double SNEAK_HEARING_RANGE = 1.5;
	private static final double SILENT_SPEED_SQR = 1.0E-4;
	private static final double REFLECTION_CONE = Math.cos(Math.toRadians(60.0));
	private static final double REFLECTION_FACING = 0.2;
	private static final double REFLECTION_RANGE = 16.0;
	private static final int REFLECTION_SCAN_INTERVAL = 10;
	private static final double TRACKER_SEARCH_RANGE = 48.0;
	private static final double WITHER_BURST_RADIUS = 2.0;
	private static final int WITHER_BURST_DURATION = 100;
	private static final float THEFT_BURST_CHANCE = 0.25F;
	private final SimpleContainer inventory = new SimpleContainer(27);
	private @Nullable BlockPos reflectingMirror;
	private @Nullable Entity reflectingEntity;
	private @Nullable Vec3 shatterPoint;
	private int shatterTicks;

	public WitherZombie(final EntityType<? extends WitherZombie> type, final Level level) {
		super(type, level);
		this.setPathfindingMalus(PathType.LAVA, 8.0F);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes()
			.add(Attributes.MAX_HEALTH, 30.0)
			.add(Attributes.MOVEMENT_SPEED, 0.25)
			.add(Attributes.FOLLOW_RANGE, 35.0)
			.add(Attributes.ATTACK_DAMAGE, 3.0);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new WitherZombie.SeekReflectionGoal(this));
		this.goalSelector.addGoal(2, new WitherZombie.WitherZombieAttackGoal(this));
		this.goalSelector.addGoal(3, new WitherZombie.StalkGoal(this));
		this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 16.0F));
		this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, WitherZombie.class, true));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, this::canNotice));
		this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
	}

	@Override
	protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
		super.defineSynchedData(entityData);
		entityData.define(DATA_TRACKED_PLAYER, 0);
		entityData.define(DATA_SHATTERING, false);
	}

	public int getTrackedPlayerId() {
		return this.entityData.get(DATA_TRACKED_PLAYER);
	}

	public boolean isShattering() {
		return this.entityData.get(DATA_SHATTERING);
	}

	public float getShatterProgress(final float partialTicks) {
		return this.isShattering() ? Math.min(1.0F, (this.shatterTicks + partialTicks) / SHATTER_DURATION) : 0.0F;
	}

	private boolean isFixated() {
		return this.reflectingMirror != null || this.reflectingEntity != null;
	}

	private boolean canNotice(final LivingEntity target, final ServerLevel level) {
		if (this.isFixated()) {
			return false;
		}

		if (!this.isInView(target) && !this.hears(target)) {
			return false;
		}

		List<WitherZombie> trackers = level.getEntitiesOfClass(
			WitherZombie.class, this.getBoundingBox().inflate(TRACKER_SEARCH_RANGE), zombie -> zombie != this && zombie.getTrackedPlayerId() == target.getId()
		);
		return trackers.isEmpty();
	}

	private boolean isInView(final LivingEntity target) {
		Vec3 view = Vec3.directionFromRotation(this.getXRot(), this.getYHeadRot());
		Vec3 toTarget = target.getEyePosition().subtract(this.getEyePosition()).normalize();
		return view.dot(toTarget) >= NOTICE_CONE;
	}

	private boolean hears(final LivingEntity target) {
		double range = target.isCrouching() ? SNEAK_HEARING_RANGE : HEARING_RANGE;
		boolean moving = target.getKnownMovement().horizontalDistanceSqr() > SILENT_SPEED_SQR;
		return moving && this.distanceToSqr(target) <= range * range;
	}

	@Override
	public void aiStep() {
		super.aiStep();
		if (!this.isShattering()) {
			return;
		}

		this.shatterTicks++;
		if (this.level().isClientSide()) {
			for (int i = 0; i < 2; i++) {
				this.level().addParticle(ParticleTypes.SMOKE, this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), 0.0, 0.02, 0.0);
			}
		}
	}

	@Override
	protected void customServerAiStep(final ServerLevel level) {
		super.customServerAiStep(level);
		if (this.isShattering()) {
			this.getNavigation().stop();
			if (this.shatterPoint != null) {
				this.getLookControl().setLookAt(this.shatterPoint);
			}

			if (this.shatterTicks >= SHATTER_DURATION) {
				this.kill(level);
			}

			return;
		}

		LivingEntity target = this.getTarget();
		this.entityData.set(DATA_TRACKED_PLAYER, target instanceof Player && !this.isFixated() ? target.getId() : 0);
		if (!this.isFixated() && this.tickCount % REFLECTION_SCAN_INTERVAL == 0) {
			this.findReflection(level);
		}
	}

	@Override
	public boolean hurtServer(final ServerLevel level, final DamageSource source, final float damage) {
		boolean hurt = super.hurtServer(level, source, damage);
		if (hurt && !this.isShattering() && !isSmiting(level, source)) {
			this.witherBurst(level);
		}

		return hurt;
	}

	private static boolean isSmiting(final ServerLevel level, final DamageSource source) {
		ItemStack weapon = source.getWeaponItem();
		if (weapon == null) {
			return false;
		}

		Holder<Enchantment> smite = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SMITE);
		return EnchantmentHelper.getItemEnchantmentLevel(smite, weapon) > 0;
	}

	private void witherBurst(final ServerLevel level) {
		AABB area = this.getBoundingBox().inflate(WITHER_BURST_RADIUS);
		for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, area, entity -> entity != this)) {
			entity.addEffect(new MobEffectInstance(MobEffects.WITHER, WITHER_BURST_DURATION), this);
		}

		level.sendParticles(ParticleTypes.SQUID_INK, this.getX(), this.getY(0.5), this.getZ(), 30, 0.8, 0.8, 0.8, 0.05);
		level.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY(0.5), this.getZ(), 15, 0.8, 0.8, 0.8, 0.02);
		this.playSound(SoundEvents.FIRE_EXTINGUISH, 1.0F, 0.5F);
	}

	@Override
	public boolean canBeAffected(final MobEffectInstance newEffect) {
		return newEffect.is(MobEffects.WITHER) ? false : super.canBeAffected(newEffect);
	}

	@Override
	protected boolean isImmobile() {
		return super.isImmobile() || this.isShattering();
	}

	private void startShattering(final Vec3 point) {
		this.shatterPoint = point;
		this.shatterTicks = 0;
		this.entityData.set(DATA_SHATTERING, true);
		this.entityData.set(DATA_TRACKED_PLAYER, 0);
		this.playSound(ArgentSoundEvents.WITHER_ZOMBIE_SHATTER);
	}

	@Override
	protected void tickDeath() {
		if (!this.isShattering() || this.level().isClientSide() || this.isRemoved()) {
			super.tickDeath();
			return;
		}

		if (this.level() instanceof ServerLevel serverLevel) {
			serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY(0.5), this.getZ(), 20, 0.3, 0.6, 0.3, 0.02);
			serverLevel.sendParticles(ParticleTypes.WHITE_ASH, this.getX(), this.getY(0.5), this.getZ(), 40, 0.4, 0.8, 0.4, 0.05);
		}

		this.playSound(SoundEvents.GLASS_BREAK, 1.0F, 0.5F);
		this.level().broadcastEntityEvent(this, (byte)60);
		this.remove(Entity.RemovalReason.KILLED);
	}

	private void findReflection(final ServerLevel level) {
		AABB range = this.getBoundingBox().inflate(REFLECTION_RANGE);
		Vec3 eye = this.getEyePosition();
		double closest = Double.MAX_VALUE;
		for (BlockPos pos : this.nearbyMirrors(level)) {
			double distance = pos.distToCenterSqr(eye);
			if (distance < closest && this.sees(mirrorSurface(level, pos))) {
				closest = distance;
				this.reflectingMirror = pos;
				this.reflectingEntity = null;
			}
		}

		List<Entity> holders = new ArrayList<>();
		holders.addAll(level.getEntitiesOfClass(ItemFrame.class, range, frame -> frame.getItem().is(ArgentItems.MIRROR)));
		holders.addAll(level.getEntitiesOfClass(Player.class, range, player -> player.isHolding(ArgentItems.REFLECTIVE_SHIELD)));
		for (Entity holder : holders) {
			double distance = holder.distanceToSqr(eye);
			if (distance < closest && this.sees(entitySurface(holder))) {
				closest = distance;
				this.reflectingMirror = null;
				this.reflectingEntity = holder;
			}
		}

		if (this.isFixated()) {
			this.setTarget(null);
		}
	}

	private List<BlockPos> nearbyMirrors(final ServerLevel level) {
		List<BlockPos> mirrors = new ArrayList<>();
		int radius = (int)Math.ceil(REFLECTION_RANGE / 16.0);
		int chunkX = this.chunkPosition().x();
		int chunkZ = this.chunkPosition().z();
		for (int x = chunkX - radius; x <= chunkX + radius; x++) {
			for (int z = chunkZ - radius; z <= chunkZ + radius; z++) {
				LevelChunk chunk = level.getChunkSource().getChunkNow(x, z);
				if (chunk == null) {
					continue;
				}

				for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
					if (blockEntity instanceof MirrorBlockEntity && blockEntity.getBlockPos().closerToCenterThan(this.position(), REFLECTION_RANGE)) {
						mirrors.add(blockEntity.getBlockPos());
					}
				}
			}
		}

		return mirrors;
	}

	private static @Nullable Surface mirrorSurface(final Level level, final BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		if (!(state.getBlock() instanceof MirrorBlock)) {
			return null;
		}

		Vec3 normal = Vec3.directionFromRotation(0.0F, RotationSegment.convertToDegrees(state.getValue(MirrorBlock.ROTATION)));
		return new Surface(Vec3.atBottomCenterOf(pos).add(0.0, 1.0, 0.0), normal);
	}

	private static @Nullable Surface entitySurface(final Entity holder) {
		if (holder.isRemoved()) {
			return null;
		}

		if (holder instanceof ItemFrame frame) {
			return frame.getItem().is(ArgentItems.MIRROR) ? new Surface(frame.position(), Vec3.atLowerCornerOf(frame.getDirection().getUnitVec3i())) : null;
		}

		if (holder instanceof Player player && player.isHolding(ArgentItems.REFLECTIVE_SHIELD)) {
			Vec3 normal = player.getLookAngle().horizontal().normalize();
			return new Surface(player.position().add(0.0, player.getBbHeight() * 0.55, 0.0).add(normal.scale(0.4)), normal);
		}

		return null;
	}

	private @Nullable Surface reflection() {
		if (this.reflectingMirror != null) {
			return mirrorSurface(this.level(), this.reflectingMirror);
		}

		return this.reflectingEntity != null ? entitySurface(this.reflectingEntity) : null;
	}

	private void loseReflection() {
		this.reflectingMirror = null;
		this.reflectingEntity = null;
	}

	private boolean sees(final @Nullable Surface surface) {
		if (surface == null) {
			return false;
		}

		Vec3 eye = this.getEyePosition();
		Vec3 toEye = eye.subtract(surface.point);
		if (toEye.lengthSqr() > REFLECTION_RANGE * REFLECTION_RANGE || surface.normal.dot(toEye.normalize()) < REFLECTION_FACING) {
			return false;
		}

		Vec3 view = Vec3.directionFromRotation(this.getXRot(), this.getYHeadRot());
		if (view.dot(toEye.normalize().reverse()) < REFLECTION_CONE) {
			return false;
		}

		Vec3 target = surface.point.add(surface.normal.scale(0.3));
		return this.level().clip(new ClipContext(eye, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS;
	}

	private void steal(final ServerLevel level, final Player player) {
		Inventory playerInventory = player.getInventory();
		List<Integer> slots = new ArrayList<>();
		for (int slot = 0; slot < playerInventory.getContainerSize(); slot++) {
			if (playerInventory.getItem(slot).is(ArgentItemTags.WITHER_ZOMBIE_TAKES)) {
				slots.add(slot);
			}
		}

		if (slots.isEmpty()) {
			return;
		}

		int slot = slots.get(this.random.nextInt(slots.size()));
		ItemStack taken = playerInventory.removeItem(slot, 1);
		ItemStack remainder = this.inventory.addItem(taken);
		if (!remainder.isEmpty()) {
			playerInventory.add(remainder);
			return;
		}

		this.playSound(SoundEvents.ITEM_PICKUP, 0.4F, 0.6F);
		if (this.random.nextFloat() < THEFT_BURST_CHANCE) {
			this.witherBurst(level);
		}
	}

	@Override
	public SimpleContainer getInventory() {
		return this.inventory;
	}

	@Override
	protected void addAdditionalSaveData(final ValueOutput output) {
		super.addAdditionalSaveData(output);
		this.writeInventoryToTag(output);
	}

	@Override
	protected void readAdditionalSaveData(final ValueInput input) {
		super.readAdditionalSaveData(input);
		this.readInventoryFromTag(input);
	}

	@Override
	protected void dropCustomDeathLoot(final ServerLevel level, final DamageSource source, final boolean killedByPlayer) {
		super.dropCustomDeathLoot(level, source, killedByPlayer);
		this.inventory.removeAllItems().forEach(itemStack -> this.spawnAtLocation(level, itemStack));
		if (this.isShattering()) {
			Holder<Enchantment> scrying = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ArgentEnchantments.SCRYING);
			this.spawnAtLocation(level, EnchantmentHelper.createBook(new EnchantmentInstance(scrying, 1)));
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return ArgentSoundEvents.WITHER_ZOMBIE_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(final DamageSource source) {
		return ArgentSoundEvents.WITHER_ZOMBIE_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return ArgentSoundEvents.WITHER_ZOMBIE_DEATH;
	}

	@Override
	protected void playStepSound(final BlockPos pos, final BlockState blockState) {
		this.playSound(ArgentSoundEvents.WITHER_ZOMBIE_STEP, 0.15F, 1.0F);
	}

	private record Surface(Vec3 point, Vec3 normal) {
	}

	private static class SeekReflectionGoal extends Goal {
		private static final double STAND_DISTANCE = 1.2;
		private static final double SHATTER_DISTANCE = 2.5;
		private static final double STUCK_SHATTER_DISTANCE = 8.0;
		private static final int REPATH_INTERVAL = 10;
		private final WitherZombie zombie;
		private int repathDelay;
		private boolean pathing;

		private SeekReflectionGoal(final WitherZombie zombie) {
			this.zombie = zombie;
			this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
		}

		@Override
		public boolean canUse() {
			return this.zombie.isFixated() && !this.zombie.isShattering();
		}

		@Override
		public void start() {
			this.repathDelay = 0;
			this.pathing = false;
		}

		@Override
		public boolean requiresUpdateEveryTick() {
			return true;
		}

		@Override
		public void tick() {
			Surface surface = this.zombie.reflection();
			if (surface == null) {
				this.zombie.loseReflection();
				return;
			}

			this.zombie.getLookControl().setLookAt(surface.point);
			double distance = this.zombie.getEyePosition().distanceTo(surface.point);
			boolean stuck = this.pathing && this.zombie.getNavigation().isDone();
			if (distance <= SHATTER_DISTANCE || stuck && distance <= STUCK_SHATTER_DISTANCE && this.zombie.sees(surface)) {
				this.zombie.startShattering(surface.point);
				return;
			}

			if (--this.repathDelay <= 0) {
				this.repathDelay = REPATH_INTERVAL;
				Vec3 stand = surface.point.add(surface.normal.horizontal().normalize().scale(STAND_DISTANCE));
				this.pathing = this.zombie.getNavigation().moveTo(stand.x, surface.point.y - 1.0, stand.z, 1.2);
			}
		}
	}

	private static class WitherZombieAttackGoal extends MeleeAttackGoal {
		private final WitherZombie zombie;

		private WitherZombieAttackGoal(final WitherZombie zombie) {
			super(zombie, 1.0, false);
			this.zombie = zombie;
		}

		@Override
		public boolean canUse() {
			return this.zombie.getTarget() instanceof WitherZombie && !this.zombie.isFixated() && super.canUse();
		}

		@Override
		public boolean canContinueToUse() {
			return this.zombie.getTarget() instanceof WitherZombie && !this.zombie.isFixated() && super.canContinueToUse();
		}
	}

	private static class StalkGoal extends Goal {
		private static final double BEHIND_DISTANCE = 2.5;
		private static final double REACH = 2.5;
		private static final int STEAL_INTERVAL = 60;
		private static final int REPATH_INTERVAL = 10;
		private final WitherZombie zombie;
		private int repathDelay;
		private int stealDelay;

		private StalkGoal(final WitherZombie zombie) {
			this.zombie = zombie;
			this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
		}

		@Override
		public boolean canUse() {
			return this.zombie.getTarget() instanceof Player player && player.isAlive() && !this.zombie.isFixated();
		}

		@Override
		public void start() {
			this.repathDelay = 0;
			this.stealDelay = STEAL_INTERVAL;
		}

		@Override
		public void stop() {
			this.zombie.getNavigation().stop();
		}

		@Override
		public boolean requiresUpdateEveryTick() {
			return true;
		}

		@Override
		public void tick() {
			if (!(this.zombie.getTarget() instanceof Player player)) {
				return;
			}

			this.zombie.getLookControl().setLookAt(player, 30.0F, 30.0F);
			Vec3 look = player.getLookAngle().horizontal().normalize();
			if (--this.repathDelay <= 0) {
				this.repathDelay = REPATH_INTERVAL;
				Vec3 behind = player.position().subtract(look.scale(BEHIND_DISTANCE));
				this.zombie.getNavigation().moveTo(behind.x, behind.y, behind.z, 1.0);
			}

			Vec3 toZombie = this.zombie.position().subtract(player.position()).horizontal().normalize();
			boolean behind = look.dot(toZombie) < 0.0;
			if (--this.stealDelay <= 0 && behind && this.zombie.distanceToSqr(player) <= REACH * REACH) {
				this.stealDelay = STEAL_INTERVAL;
				this.zombie.swingForAttack(InteractionHand.MAIN_HAND);
				this.zombie.steal(getServerLevel(this.zombie), player);
			}
		}
	}
}
