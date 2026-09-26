package com.khrux.argent.datagen;

import com.khrux.argent.sounds.ArgentSoundEvents;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.client.datagen.v1.builder.SoundTypeBuilder;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricSoundsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class ArgentSoundsProvider extends FabricSoundsProvider {
	private static final float VOICE_PITCH = 0.65F;
	private static final float STEP_PITCH = 0.75F;
	private static final float NOTICE_PITCH = 0.5F;

	public ArgentSoundsProvider(final PackOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void configure(final HolderLookup.Provider registries, final FabricSoundsProvider.SoundExporter exporter) {
		add(exporter, ArgentSoundEvents.WITHER_ZOMBIE_AMBIENT, "subtitles.entity.wither_zombie.ambient", VOICE_PITCH, "mob/zombie/say1", "mob/zombie/say2", "mob/zombie/say3");
		add(exporter, ArgentSoundEvents.WITHER_ZOMBIE_HURT, "subtitles.entity.wither_zombie.hurt", VOICE_PITCH, "mob/zombie/hurt1", "mob/zombie/hurt2");
		add(exporter, ArgentSoundEvents.WITHER_ZOMBIE_DEATH, "subtitles.entity.wither_zombie.death", VOICE_PITCH, "mob/zombie/death");
		add(exporter, ArgentSoundEvents.WITHER_ZOMBIE_NOTICE, "subtitles.entity.wither_zombie.notice", NOTICE_PITCH, "mob/zombie/say1", "mob/zombie/say2", "mob/zombie/say3");
		add(exporter, ArgentSoundEvents.WITHER_ZOMBIE_SHATTER, "subtitles.entity.wither_zombie.shatter", NOTICE_PITCH, "mob/zombie/death");
		add(
			exporter,
			ArgentSoundEvents.WITHER_ZOMBIE_STEP,
			"subtitles.block.generic.footsteps",
			STEP_PITCH,
			"mob/zombie/step1",
			"mob/zombie/step2",
			"mob/zombie/step3",
			"mob/zombie/step4",
			"mob/zombie/step5"
		);
	}

	private static void add(final FabricSoundsProvider.SoundExporter exporter, final SoundEvent event, final String subtitle, final float pitch, final String... files) {
		SoundTypeBuilder builder = SoundTypeBuilder.of(event).subtitle(subtitle);
		for (String file : files) {
			builder.sound(SoundTypeBuilder.RegistrationBuilder.ofFile(Identifier.withDefaultNamespace(file)).pitch(pitch));
		}

		exporter.add(event, builder);
	}

	@Override
	public String getName() {
		return "Sounds";
	}
}
