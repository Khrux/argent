package com.khrux.justwitherzombies.sounds;

import com.khrux.justwitherzombies.JustWitherZombies;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class JustWitherZombiesSoundEvents {
	public static final SoundEvent WITHER_ZOMBIE_AMBIENT = register("entity.wither_zombie.ambient");
	public static final SoundEvent WITHER_ZOMBIE_HURT = register("entity.wither_zombie.hurt");
	public static final SoundEvent WITHER_ZOMBIE_DEATH = register("entity.wither_zombie.death");
	public static final SoundEvent WITHER_ZOMBIE_STEP = register("entity.wither_zombie.step");
	public static final SoundEvent WITHER_ZOMBIE_NOTICE = register("entity.wither_zombie.notice");
	public static final SoundEvent WITHER_ZOMBIE_SHATTER = register("entity.wither_zombie.shatter");

	private static SoundEvent register(final String id) {
		Identifier location = JustWitherZombies.id(id);
		return Registry.register(BuiltInRegistries.SOUND_EVENT, location, SoundEvent.createVariableRangeEvent(location));
	}

	public static void bootstrap() {
	}
}
