package com.ronhelwig.simulationbreach.identity;

public final class BreachDataKeys {
	public static final String ROOT = "simulationBreach";
	public static final String ORIGIN_DISPOSITION = "originDisposition";
	public static final String ORIGINAL_ENTITY_TYPE = "originalEntityType";
	public static final String INFECTION_STAGE = "infectionStage";
	public static final String TRANSFORMATION_STATE = "transformationState";
	public static final String LAST_CONVERSION_GAME_TIME = "lastConversionGameTime";
	public static final String CONVERTED_BY_ENTITY_UUID = "convertedByEntityUuid";
	public static final String CONVERSION_GENERATION = "conversionGeneration";
	public static final String IMMUNE_UNTIL_GAME_TIME = "immuneUntilGameTime";
	public static final String TRANSFORMATION_STARTED_GAME_TIME = "transformationStartedGameTime";
	public static final String TRANSFORMATION_DURATION_TICKS = "transformationDurationTicks";
	public static final String PENDING_REPLACEMENT_ENTITY_TYPE = "pendingReplacementEntityType";
	public static final String PENDING_INFECTION_STAGE = "pendingInfectionStage";
	public static final String DEBUG_SOURCE = "debugSource";
	public static final String[] ALL = {
			ORIGIN_DISPOSITION,
			ORIGINAL_ENTITY_TYPE,
			INFECTION_STAGE,
			TRANSFORMATION_STATE,
			LAST_CONVERSION_GAME_TIME,
			CONVERTED_BY_ENTITY_UUID,
			CONVERSION_GENERATION,
			IMMUNE_UNTIL_GAME_TIME,
			TRANSFORMATION_STARTED_GAME_TIME,
			TRANSFORMATION_DURATION_TICKS,
			PENDING_REPLACEMENT_ENTITY_TYPE,
			PENDING_INFECTION_STAGE,
			DEBUG_SOURCE
	};

	private BreachDataKeys() {
	}
}
