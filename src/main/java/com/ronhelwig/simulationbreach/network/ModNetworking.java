package com.ronhelwig.simulationbreach.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public final class ModNetworking {
	private ModNetworking() {
	}

	public static void registerPayloads() {
		PayloadTypeRegistry.clientboundPlay().register(TransformationPresentationPayload.TYPE, TransformationPresentationPayload.CODEC);
	}
}
