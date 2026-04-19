package com.ronhelwig.simulationbreach.client.render;

import com.ronhelwig.simulationbreach.SimulationBreach;
import com.ronhelwig.simulationbreach.entity.AgentEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class AgentRenderer extends HumanoidMobRenderer<AgentEntity, AgentRenderState, HumanoidModel<AgentRenderState>> {
	private static final Identifier AGENT_TEXTURE = Identifier.fromNamespaceAndPath(
			SimulationBreach.MOD_ID,
			"textures/entity/agent/agent.png"
	);
	private static final Identifier PLACEHOLDER_TEXTURE = Identifier.withDefaultNamespace("textures/entity/zombie/zombie.png");
	private static final int BASE_TINT = 0xFF102A28;
	private static final int PULSE_TINT = 0xFF6EFFF0;

	public AgentRenderer(EntityRendererProvider.Context context) {
		super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIE)), 0.5F);
	}

	@Override
	public AgentRenderState createRenderState() {
		return new AgentRenderState();
	}

	@Override
	public void extractRenderState(AgentEntity entity, AgentRenderState renderState, float tickProgress) {
		super.extractRenderState(entity, renderState, tickProgress);
		renderState.breachPulse = 0.5F + (0.5F * Mth.sin(renderState.ageInTicks * 0.18F));
	}

	@Override
	public Identifier getTextureLocation(AgentRenderState renderState) {
		return hasAgentTexture() ? AGENT_TEXTURE : PLACEHOLDER_TEXTURE;
	}

	@Override
	protected int getModelTint(AgentRenderState renderState) {
		if (hasAgentTexture()) {
			return -1;
		}
		return blendArgb(BASE_TINT, PULSE_TINT, renderState.breachPulse * 0.45F);
	}

	private static boolean hasAgentTexture() {
		return Minecraft.getInstance().getResourceManager().getResource(AGENT_TEXTURE).isPresent();
	}

	private static int blendArgb(int from, int to, float amount) {
		float clamped = Mth.clamp(amount, 0.0F, 1.0F);
		int alpha = blendChannel(from >>> 24, to >>> 24, clamped);
		int red = blendChannel((from >>> 16) & 0xFF, (to >>> 16) & 0xFF, clamped);
		int green = blendChannel((from >>> 8) & 0xFF, (to >>> 8) & 0xFF, clamped);
		int blue = blendChannel(from & 0xFF, to & 0xFF, clamped);
		return alpha << 24 | red << 16 | green << 8 | blue;
	}

	private static int blendChannel(int from, int to, float amount) {
		return Mth.clamp(Math.round(Mth.lerp(amount, from, to)), 0, 255);
	}
}
