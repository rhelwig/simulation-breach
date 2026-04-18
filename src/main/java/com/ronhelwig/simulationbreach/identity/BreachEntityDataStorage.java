package com.ronhelwig.simulationbreach.identity;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class BreachEntityDataStorage {
	private BreachEntityDataStorage() {
	}

	public static void write(ValueOutput output, BreachEntityData data) {
		Objects.requireNonNull(output, "output");
		Objects.requireNonNull(data, "data");

		ValueOutput child = output.child(BreachDataKeys.ROOT);
		for (Map.Entry<String, String> entry : data.toMap().entrySet()) {
			child.putString(entry.getKey(), entry.getValue());
		}
	}

	public static Optional<BreachEntityData> read(ValueInput input) {
		Objects.requireNonNull(input, "input");
		return input.child(BreachDataKeys.ROOT)
				.map(BreachEntityDataStorage::readFromChild);
	}

	public static void discard(ValueOutput output) {
		Objects.requireNonNull(output, "output");
		output.discard(BreachDataKeys.ROOT);
	}

	private static BreachEntityData readFromChild(ValueInput child) {
		Map<String, String> values = new LinkedHashMap<>();
		for (String key : BreachDataKeys.ALL) {
			child.getString(key).ifPresent(value -> values.put(key, value));
		}
		return BreachEntityData.fromMap(values);
	}
}
