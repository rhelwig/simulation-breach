package com.ronhelwig.simulationbreach;

import com.ronhelwig.simulationbreach.command.ModCommands;
import com.ronhelwig.simulationbreach.config.SimulationBreachConfig;
import com.ronhelwig.simulationbreach.config.SimulationBreachConfigLoader;
import com.ronhelwig.simulationbreach.entity.ModEntities;
import com.ronhelwig.simulationbreach.gameplay.InfectionRules;
import com.ronhelwig.simulationbreach.gameplay.OutbreakDifficulty;
import com.ronhelwig.simulationbreach.network.ModNetworking;
import com.ronhelwig.simulationbreach.outbreak.OutbreakManager;
import com.ronhelwig.simulationbreach.sound.ModSounds;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimulationBreach implements ModInitializer {
	public static final String MOD_ID = "simulation-breach";
	public static SimulationBreachConfig CONFIG = SimulationBreachConfig.defaults();

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Entering the world!");
		CONFIG = SimulationBreachConfigLoader.load(MOD_ID, LOGGER);
		ModNetworking.registerPayloads();
		ModEntities.register();
		ModSounds.register();
		ModCommands.register();
		OutbreakManager.register();
		LOGGER.info(
				"Loaded Simulation Breach config: normal initial Agent chance={}, transformation duration={} ticks, outbreak interval={} ticks",
				InfectionRules.initialPassiveAgentChance(CONFIG, OutbreakDifficulty.NORMAL),
				CONFIG.transformationDurationTicks(),
				CONFIG.outbreakCheckIntervalTicks()
		);
	}
}
