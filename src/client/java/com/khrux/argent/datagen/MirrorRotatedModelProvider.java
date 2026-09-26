package com.khrux.argent.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.khrux.argent.Argent;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.Direction;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class MirrorRotatedModelProvider implements DataProvider {
	private static final String[] SUFFIXES = new String[]{"", "_22_5", "_45", "_67_5"};
	private static final float DEGREES_PER_STEP = 22.5F;
	private static final Vector3fc CENTRE = new Vector3f(8.0F, 8.0F, 8.0F);
	private final FabricPackOutput output;
	private final PackOutput.PathProvider modelPathProvider;

	public MirrorRotatedModelProvider(final FabricPackOutput output) {
		this.output = output;
		this.modelPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
	}

	public static Identifier modelLocation(final int step) {
		return Argent.id("block/mirror" + SUFFIXES[step]);
	}

	@Override
	public CompletableFuture<?> run(final CachedOutput cache) {
		JsonObject model;
		try (Reader reader = Files.newBufferedReader(this.output.getModContainer().findPath("assets/argent/models/block/mirror.json").orElseThrow())) {
			model = JsonParser.parseReader(reader).getAsJsonObject();
		} catch (IOException e) {
			return CompletableFuture.failedFuture(e);
		}

		List<CompletableFuture<?>> futures = new ArrayList<>();
		for (int step = 1; step < SUFFIXES.length; step++) {
			futures.add(DataProvider.saveStable(cache, rotate(model, step * DEGREES_PER_STEP), this.modelPathProvider.json(modelLocation(step))));
		}

		return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
	}

	private static JsonObject rotate(final JsonObject model, final float degrees) {
		JsonObject rotated = model.deepCopy();
		Matrix4f turn = new Matrix4f().rotationY(-degrees * Mth.DEG_TO_RAD);
		for (JsonElement element : rotated.getAsJsonArray("elements")) {
			rotateElement(element.getAsJsonObject(), turn);
		}

		return rotated;
	}

	private static void rotateElement(final JsonObject element, final Matrix4f turn) {
		Vector3f origin = new Vector3f(CENTRE);
		Matrix4f transform = new Matrix4f(turn);
		if (element.has("rotation")) {
			JsonObject rotation = element.getAsJsonObject("rotation");
			origin = readVector(rotation.getAsJsonArray("origin"));
			Vector3fc axis = Direction.Axis.byName(rotation.get("axis").getAsString()).getPositive().getUnitVec3f();
			transform.rotate(rotation.get("angle").getAsFloat() * Mth.DEG_TO_RAD, axis);
		}

		Vector3f turnedOrigin = turn.transformPosition(origin.sub(CENTRE, new Vector3f())).add(CENTRE);
		Vector3f shift = turnedOrigin.sub(origin, new Vector3f());
		element.add("from", writeVector(readVector(element.getAsJsonArray("from")).add(shift)));
		element.add("to", writeVector(readVector(element.getAsJsonArray("to")).add(shift)));
		Vector3f angles = transform.getEulerAnglesZYX(new Vector3f()).mul(Mth.RAD_TO_DEG);
		JsonObject rotation = new JsonObject();
		rotation.add("origin", writeVector(turnedOrigin));
		rotation.addProperty("x", round(angles.x));
		rotation.addProperty("y", round(angles.y));
		rotation.addProperty("z", round(angles.z));
		element.add("rotation", rotation);
	}

	private static Vector3f readVector(final JsonArray array) {
		return new Vector3f(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat());
	}

	private static JsonArray writeVector(final Vector3fc vector) {
		JsonArray array = new JsonArray();
		array.add(round(vector.x()));
		array.add(round(vector.y()));
		array.add(round(vector.z()));
		return array;
	}

	private static float round(final float value) {
		return Math.round(value * 10000.0F) / 10000.0F;
	}

	@Override
	public String getName() {
		return "Mirror rotated models";
	}
}
