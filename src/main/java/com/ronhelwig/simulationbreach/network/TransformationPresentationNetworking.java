package com.ronhelwig.simulationbreach.network;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public final class TransformationPresentationNetworking {
	private TransformationPresentationNetworking() {
	}

	public static void sendStart(Entity entity, int durationTicks) {
		send(entity, new TransformationPresentationPayload(entity.getId(), durationTicks, true));
	}

	public static void sendStop(Entity entity) {
		send(entity, new TransformationPresentationPayload(entity.getId(), 0, false));
	}

	private static void send(Entity entity, TransformationPresentationPayload payload) {
		for (ServerPlayer player : PlayerLookup.tracking(entity)) {
			if (ServerPlayNetworking.canSend(player, TransformationPresentationPayload.TYPE)) {
				ServerPlayNetworking.send(player, payload);
			}
		}
	}
}
