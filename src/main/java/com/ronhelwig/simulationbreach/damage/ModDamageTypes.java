package com.ronhelwig.simulationbreach.damage;

import com.ronhelwig.simulationbreach.SimulationBreach;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public final class ModDamageTypes {
	public static final ResourceKey<DamageType> AGENT_ERADICATION = ResourceKey.create(
			Registries.DAMAGE_TYPE,
			Identifier.fromNamespaceAndPath(SimulationBreach.MOD_ID, "agent_eradication")
	);

	private ModDamageTypes() {
	}
}
