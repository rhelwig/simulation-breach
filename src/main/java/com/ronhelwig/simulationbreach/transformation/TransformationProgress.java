package com.ronhelwig.simulationbreach.transformation;

public record TransformationProgress(
		long startedGameTime,
		int durationTicks,
		long currentGameTime,
		long elapsedTicks,
		long remainingTicks,
		double progress,
		double shakeIntensity,
		boolean complete
) {
	public TransformationProgress {
		if (startedGameTime < 0L) {
			throw new IllegalArgumentException("startedGameTime must be non-negative");
		}
		if (durationTicks <= 0) {
			throw new IllegalArgumentException("durationTicks must be positive");
		}
		if (currentGameTime < 0L) {
			throw new IllegalArgumentException("currentGameTime must be non-negative");
		}
		if (elapsedTicks < 0L) {
			throw new IllegalArgumentException("elapsedTicks must be non-negative");
		}
		if (remainingTicks < 0L) {
			throw new IllegalArgumentException("remainingTicks must be non-negative");
		}
		if (progress < 0.0D || progress > 1.0D) {
			throw new IllegalArgumentException("progress must be between 0.0 and 1.0");
		}
		if (shakeIntensity < 0.0D || shakeIntensity > 1.0D) {
			throw new IllegalArgumentException("shakeIntensity must be between 0.0 and 1.0");
		}
	}
}
