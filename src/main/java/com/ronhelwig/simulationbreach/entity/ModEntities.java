package com.ronhelwig.simulationbreach.entity;

import com.ronhelwig.simulationbreach.SimulationBreach;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class ModEntities {
	public static final Identifier AGENT_ID = Identifier.fromNamespaceAndPath(SimulationBreach.MOD_ID, "agent");
	public static final ResourceKey<EntityType<?>> AGENT_KEY = ResourceKey.create(Registries.ENTITY_TYPE, AGENT_ID);
	public static final EntityType<AgentEntity> AGENT = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			AGENT_KEY,
			EntityType.Builder.of(AgentEntity::new, MobCategory.MONSTER)
					.sized(0.6F, 1.95F)
					.eyeHeight(1.74F)
					.clientTrackingRange(8)
					.build(AGENT_KEY)
	);

	private ModEntities() {
	}

	public static void register() {
		FabricDefaultAttributeRegistry.register(AGENT, AgentEntity.createAttributes());
		SimulationBreach.LOGGER.info("Registered Agent entity type {}", AGENT_ID);
	}
}
