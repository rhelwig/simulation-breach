package com.ronhelwig.simulationbreach.gameplay;

import com.ronhelwig.simulationbreach.config.SimulationBreachConfig;

import java.util.Objects;
import java.util.Random;

public final class InfectionRules {
	private InfectionRules() {
	}

	public static double initialPassiveAgentChance(SimulationBreachConfig config, OutbreakDifficulty difficulty) {
		Objects.requireNonNull(config, "config");
		Objects.requireNonNull(difficulty, "difficulty");
		return clampChance(config.initialPassiveAgentChance() * difficulty.multiplier(config));
	}

	public static boolean shouldStartInitialAgentOutbreak(Random random, SimulationBreachConfig config, OutbreakDifficulty difficulty) {
		Objects.requireNonNull(random, "random");
		return random.nextDouble() < initialPassiveAgentChance(config, difficulty);
	}

	private static double clampChance(double value) {
		if (value < 0.0D) {
			return 0.0D;
		}
		if (value > 1.0D) {
			return 1.0D;
		}
		return value;
	}
}
