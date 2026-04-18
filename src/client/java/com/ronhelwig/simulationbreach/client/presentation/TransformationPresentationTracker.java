package com.ronhelwig.simulationbreach.client.presentation;

import com.ronhelwig.simulationbreach.network.TransformationPresentationPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class TransformationPresentationTracker {
	private static final Map<Integer, Long> TRANSFORMING_UNTIL_GAME_TIME = new HashMap<>();

	private TransformationPresentationTracker() {
	}

	public static void handle(TransformationPresentationPayload payload) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.level == null) {
			return;
		}

		if (!payload.active()) {
			TRANSFORMING_UNTIL_GAME_TIME.remove(payload.entityId());
			return;
		}

		long durationTicks = Math.max(1, payload.durationTicks());
		TRANSFORMING_UNTIL_GAME_TIME.put(payload.entityId(), minecraft.level.getGameTime() + durationTicks);
	}

	public static boolean isTransforming(Entity entity) {
		ClientLevel level = Minecraft.getInstance().level;
		if (level == null) {
			return false;
		}

		long currentGameTime = level.getGameTime();
		removeExpired(currentGameTime);
		Long transformingUntil = TRANSFORMING_UNTIL_GAME_TIME.get(entity.getId());
		return transformingUntil != null && transformingUntil > currentGameTime;
	}

	public static void clear() {
		TRANSFORMING_UNTIL_GAME_TIME.clear();
	}

	private static void removeExpired(long currentGameTime) {
		Iterator<Map.Entry<Integer, Long>> iterator = TRANSFORMING_UNTIL_GAME_TIME.entrySet().iterator();
		while (iterator.hasNext()) {
			if (iterator.next().getValue() <= currentGameTime) {
				iterator.remove();
			}
		}
	}
}
