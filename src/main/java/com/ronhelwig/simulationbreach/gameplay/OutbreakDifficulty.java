package com.ronhelwig.simulationbreach.gameplay;

import com.ronhelwig.simulationbreach.config.SimulationBreachConfig;

public enum OutbreakDifficulty {
	PEACEFUL {
		@Override
		double multiplier(SimulationBreachConfig config) {
			return config.peacefulInitialAgentChanceMultiplier();
		}
	},
	EASY {
		@Override
		double multiplier(SimulationBreachConfig config) {
			return config.easyInitialAgentChanceMultiplier();
		}
	},
	NORMAL {
		@Override
		double multiplier(SimulationBreachConfig config) {
			return config.normalInitialAgentChanceMultiplier();
		}
	},
	HARD {
		@Override
		double multiplier(SimulationBreachConfig config) {
			return config.hardInitialAgentChanceMultiplier();
		}
	},
	HARDCORE {
		@Override
		double multiplier(SimulationBreachConfig config) {
			return config.hardcoreInitialAgentChanceMultiplier();
		}
	};

	abstract double multiplier(SimulationBreachConfig config);
}
