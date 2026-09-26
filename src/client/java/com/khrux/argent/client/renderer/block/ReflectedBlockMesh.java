package com.khrux.argent.client.renderer.block;

import com.mojang.blaze3d.vertex.QuadInstance;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class ReflectedBlockMesh {
	private static final int TESSELLATION_BUDGET = 256;
	private static final int REFRESH_FRAMES = 200;
	private final Map<BlockPos, Entry> entries = new HashMap<>();
	private ModelBlockRenderer blockRenderer = new ModelBlockRenderer(true, true, Minecraft.getInstance().getBlockColors());
	private boolean cutoutLeaves;
	private int budget;
	private long frame;

	public void startFrame() {
		Minecraft minecraft = Minecraft.getInstance();
		this.blockRenderer = new ModelBlockRenderer(minecraft.options.ambientOcclusion().get(), true, minecraft.getBlockColors());
		this.cutoutLeaves = minecraft.options.cutoutLeaves().get();
		this.budget = TESSELLATION_BUDGET;
		this.frame++;
	}

	public @Nullable List<Quad> quads(final ClientLevel level, final BlockPos pos) {
		int signature = signature(level, pos);
		Entry entry = this.entries.get(pos);
		boolean stale = entry == null || entry.signature != signature || this.frame - entry.builtAt > REFRESH_FRAMES;
		if (stale && this.budget > 0) {
			this.budget--;
			entry = new Entry(signature, this.frame, this.tessellate(level, pos));
			this.entries.put(pos, entry);
		}

		return entry == null ? null : entry.quads;
	}

	public void retain(final Collection<BlockPos> positions) {
		this.entries.keySet().retainAll(new HashSet<>(positions));
	}

	private List<Quad> tessellate(final ClientLevel level, final BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		ChunkSectionLayer forcedLayer = ModelBlockRenderer.forceOpaque(this.cutoutLeaves, state) ? ChunkSectionLayer.SOLID : null;
		List<Quad> quads = new ArrayList<>();
		this.blockRenderer
			.tesselateBlock(
				(x, y, z, quad, instance) -> quads.add(new Quad(quad, copy(instance), forcedLayer != null ? forcedLayer : quad.materialInfo().layer())),
				0.0F,
				0.0F,
				0.0F,
				level,
				pos,
				state,
				Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(state),
				state.getSeed(pos)
			);
		return quads;
	}

	private static QuadInstance copy(final QuadInstance instance) {
		QuadInstance copy = new QuadInstance();
		for (int vertex = 0; vertex < 4; vertex++) {
			copy.setColor(vertex, instance.getColor(vertex));
			copy.setLightCoords(vertex, instance.getLightCoords(vertex));
		}

		copy.setOverlayCoords(instance.overlayCoords());
		return copy;
	}

	private static int signature(final ClientLevel level, final BlockPos pos) {
		int signature = System.identityHashCode(level.getBlockState(pos));
		for (Direction direction : Direction.values()) {
			signature = signature * 31 + System.identityHashCode(level.getBlockState(pos.relative(direction)));
		}

		return signature;
	}

	public record Quad(BakedQuad quad, QuadInstance instance, ChunkSectionLayer layer) {
	}

	private record Entry(int signature, long builtAt, List<Quad> quads) {
	}
}
