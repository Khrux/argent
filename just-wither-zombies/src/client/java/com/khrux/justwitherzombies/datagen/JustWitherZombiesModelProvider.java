package com.khrux.justwitherzombies.datagen;

import com.khrux.justwitherzombies.client.renderer.special.ReflectiveShieldSpecialRenderer;
import com.khrux.justwitherzombies.world.item.JustWitherZombiesItems;
import java.util.List;
import java.util.Optional;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.properties.select.DisplayContext;
import net.minecraft.client.renderer.special.ShieldSpecialRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;

public class JustWitherZombiesModelProvider extends FabricModelProvider {
	private static final ModelTemplate SHIELD = new ModelTemplate(Optional.of(Identifier.withDefaultNamespace("item/shield")), Optional.empty());
	private static final ModelTemplate SHIELD_BLOCKING = new ModelTemplate(Optional.of(Identifier.withDefaultNamespace("item/shield_blocking")), Optional.of("_blocking"));
	private static final List<ItemDisplayContext> REFLECTIVE_CONTEXTS = List.of(
		ItemDisplayContext.THIRD_PERSON_LEFT_HAND, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, ItemDisplayContext.GROUND, ItemDisplayContext.FIXED
	);

	public JustWitherZombiesModelProvider(final FabricPackOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(final BlockModelGenerators blockModelGenerators) {
	}

	private static void createReflectiveShield(final ItemModelGenerators itemModelGenerators) {
		Identifier normal = SHIELD.create(JustWitherZombiesItems.REFLECTIVE_SHIELD, new TextureMapping(), itemModelGenerators.modelOutput);
		Identifier blocking = SHIELD_BLOCKING.create(JustWitherZombiesItems.REFLECTIVE_SHIELD, new TextureMapping(), itemModelGenerators.modelOutput);
		itemModelGenerators.itemModelOutput
			.accept(
				JustWitherZombiesItems.REFLECTIVE_SHIELD,
				ItemModelUtils.conditional(ShieldSpecialRenderer.DEFAULT_TRANSFORMATION, ItemModelUtils.isUsingItem(), shieldModel(blocking), shieldModel(normal))
			);
	}

	private static ItemModel.Unbaked shieldModel(final Identifier base) {
		return ItemModelUtils.select(
			new DisplayContext(),
			ItemModelUtils.specialModel(base, new ReflectiveShieldSpecialRenderer.Unbaked(false)),
			ItemModelUtils.when(REFLECTIVE_CONTEXTS, ItemModelUtils.specialModel(base, new ReflectiveShieldSpecialRenderer.Unbaked(true)))
		);
	}

	@Override
	public void generateItemModels(final ItemModelGenerators itemModelGenerators) {
		itemModelGenerators.generateFlatItem(JustWitherZombiesItems.WITHER_ZOMBIE_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
		createReflectiveShield(itemModelGenerators);
	}
}
