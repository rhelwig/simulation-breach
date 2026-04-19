package com.ronhelwig.simulationbreach.sound;

import com.ronhelwig.simulationbreach.SimulationBreach;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;

public final class ModSounds {
	public static final Identifier AGENT_TRANSFORM_ID = Identifier.fromNamespaceAndPath(
			SimulationBreach.MOD_ID,
			"entity.agent.transform"
	);
	public static final Identifier AGENT_VOICE_ID = Identifier.fromNamespaceAndPath(
			SimulationBreach.MOD_ID,
			"entity.agent.voice"
	);
	public static final SoundEvent AGENT_TRANSFORM = register(AGENT_TRANSFORM_ID);
	public static final SoundEvent AGENT_VOICE = register(AGENT_VOICE_ID);

	private ModSounds() {
	}

	public static void register() {
		SimulationBreach.LOGGER.info("Registered Agent sounds {}, {}", AGENT_TRANSFORM_ID, AGENT_VOICE_ID);
	}

	private static SoundEvent register(Identifier id) {
		ResourceKey<SoundEvent> key = ResourceKey.create(Registries.SOUND_EVENT, id);
		return Registry.register(BuiltInRegistries.SOUND_EVENT, key, SoundEvent.createVariableRangeEvent(id));
	}
}
