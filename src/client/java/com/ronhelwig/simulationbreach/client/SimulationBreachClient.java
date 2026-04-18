package com.ronhelwig.simulationbreach.client;

import com.ronhelwig.simulationbreach.client.presentation.TransformationPresentationTracker;
import com.ronhelwig.simulationbreach.client.render.AgentRenderer;
import com.ronhelwig.simulationbreach.entity.ModEntities;
import com.ronhelwig.simulationbreach.network.TransformationPresentationPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class SimulationBreachClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(ModEntities.AGENT, AgentRenderer::new);
		ClientPlayNetworking.registerGlobalReceiver(
				TransformationPresentationPayload.TYPE,
				(payload, context) -> context.client().execute(() -> TransformationPresentationTracker.handle(payload))
		);
	}
}
