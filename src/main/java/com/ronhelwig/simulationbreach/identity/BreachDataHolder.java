package com.ronhelwig.simulationbreach.identity;

import java.util.Optional;

public interface BreachDataHolder {
	Optional<BreachEntityData> simulationBreach$getBreachData();

	void simulationBreach$setBreachData(BreachEntityData data);

	void simulationBreach$clearBreachData();
}
