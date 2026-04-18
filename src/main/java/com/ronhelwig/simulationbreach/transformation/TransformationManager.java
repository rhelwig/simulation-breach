package com.ronhelwig.simulationbreach.transformation;

import com.ronhelwig.simulationbreach.config.SimulationBreachConfig;
import com.ronhelwig.simulationbreach.identity.BreachEntityData;
import com.ronhelwig.simulationbreach.identity.InfectionStage;
import com.ronhelwig.simulationbreach.identity.TransformationState;

import java.util.Objects;
import java.util.Optional;

public final class TransformationManager {
	private TransformationManager() {
	}

	public static BreachEntityData beginTransformation(
			BreachEntityData source,
			long startedGameTime,
			SimulationBreachConfig config,
			String replacementEntityType,
			InfectionStage pendingInfectionStage
	) {
		Objects.requireNonNull(source, "source");
		Objects.requireNonNull(config, "config");
		Objects.requireNonNull(pendingInfectionStage, "pendingInfectionStage");

		if (source.transformationState() == TransformationState.TRANSFORMING) {
			throw new IllegalStateException("entity is already transforming");
		}

		return source.toBuilder()
				.beginTransformation(
						startedGameTime,
						config.transformationDurationTicks(),
						replacementEntityType,
						pendingInfectionStage
				)
				.build();
	}

	public static Optional<TransformationProgress> progress(BreachEntityData data, long currentGameTime) {
		Objects.requireNonNull(data, "data");
		if (data.transformationState() != TransformationState.TRANSFORMING) {
			return Optional.empty();
		}

		long startedGameTime = data.transformationStartedGameTime()
				.orElseThrow(() -> new IllegalArgumentException("transforming entity is missing transformation start time"));
		int durationTicks = data.transformationDurationTicks()
				.orElseThrow(() -> new IllegalArgumentException("transforming entity is missing transformation duration"));
		long elapsedTicks = Math.max(0L, currentGameTime - startedGameTime);
		long remainingTicks = Math.max(0L, (long) durationTicks - elapsedTicks);
		double normalizedProgress = clamp((double) elapsedTicks / (double) durationTicks);
		boolean complete = elapsedTicks >= durationTicks;

		return Optional.of(new TransformationProgress(
				startedGameTime,
				durationTicks,
				currentGameTime,
				elapsedTicks,
				remainingTicks,
				normalizedProgress,
				shakeIntensity(normalizedProgress),
				complete
		));
	}

	public static boolean shouldPlayStartSound(BreachEntityData previousData, BreachEntityData currentData) {
		Objects.requireNonNull(previousData, "previousData");
		Objects.requireNonNull(currentData, "currentData");
		return previousData.transformationState() == TransformationState.NONE
				&& currentData.transformationState() == TransformationState.TRANSFORMING;
	}

	public static boolean isComplete(BreachEntityData data, long currentGameTime) {
		return progress(data, currentGameTime)
				.map(TransformationProgress::complete)
				.orElse(false);
	}

	public static BreachEntityData completeTransformation(BreachEntityData data) {
		Objects.requireNonNull(data, "data");
		InfectionStage pendingStage = data.pendingInfectionStage()
				.orElseThrow(() -> new IllegalArgumentException("transforming entity is missing pending infection stage"));

		return data.toBuilder()
				.infectionStage(pendingStage)
				.clearTransformation()
				.build();
	}

	public static BreachEntityData cancelTransformation(BreachEntityData data) {
		Objects.requireNonNull(data, "data");
		return data.toBuilder()
				.clearTransformation()
				.build();
	}

	private static double shakeIntensity(double progress) {
		double eased = progress * progress;
		double pulse = 0.75D + (0.25D * Math.sin(progress * Math.PI * 12.0D));
		return clamp(eased * pulse);
	}

	private static double clamp(double value) {
		if (value < 0.0D) {
			return 0.0D;
		}
		if (value > 1.0D) {
			return 1.0D;
		}
		return value;
	}
}
