package com.ronhelwig.simulationbreach.config;

public final class SimulationBreachConfig {
	public static final double DEFAULT_INITIAL_PASSIVE_AGENT_CHANCE = 0.00005D;
	public static final double DEFAULT_PEACEFUL_INITIAL_AGENT_CHANCE_MULTIPLIER = 0.10D;
	public static final double DEFAULT_EASY_INITIAL_AGENT_CHANCE_MULTIPLIER = 0.50D;
	public static final double DEFAULT_NORMAL_INITIAL_AGENT_CHANCE_MULTIPLIER = 1.00D;
	public static final double DEFAULT_HARD_INITIAL_AGENT_CHANCE_MULTIPLIER = 2.00D;
	public static final double DEFAULT_HARDCORE_INITIAL_AGENT_CHANCE_MULTIPLIER = 3.00D;
	public static final double DEFAULT_AGENT_CONVERT_PASSIVE_CHANCE = 1.0D;
	public static final double DEFAULT_AGENT_CONVERT_HOSTILE_TO_AGENT_CHANCE = 0.35D;
	public static final double DEFAULT_AGENT_CONVERT_CORRUPTED_PASSIVE_TO_AGENT_CHANCE = 0.20D;
	public static final int DEFAULT_AGENT_CONVERSION_COOLDOWN_TICKS = 40;
	public static final int DEFAULT_TRANSFORMATION_DURATION_TICKS = 60;
	public static final int DEFAULT_AGENT_CONVERSION_DETOUR_RADIUS = 4;
	public static final int DEFAULT_MAX_AGENTS_PER_CHUNK = 3;
	public static final boolean DEFAULT_ENABLE_INITIAL_OUTBREAKS = true;
	public static final int DEFAULT_OUTBREAK_CHECK_INTERVAL_TICKS = 200;
	public static final int DEFAULT_OUTBREAK_SCAN_LIMIT_PER_LEVEL = 256;
	public static final int DEFAULT_OUTBREAK_ELIGIBLE_ROLLS_PER_LEVEL = 16;
	public static final double DEFAULT_INITIAL_OUTBREAK_PLAYER_SEARCH_RADIUS = 32.0D;
	public static final boolean DEFAULT_INITIAL_OUTBREAK_REQUIRES_REACHABLE_PLAYER = true;
	public static final boolean DEFAULT_ENABLE_PLACEHOLDER_CREEPER_TRANSFORM_SOUND = true;
	public static final PassivePromotionMode DEFAULT_PASSIVE_PROMOTION_MODE = PassivePromotionMode.PROMOTED_CORRUPTION;
	public static final boolean DEFAULT_EXCLUDE_VILLAGERS = false;
	public static final boolean DEFAULT_EXCLUDE_TAMED_ANIMALS = true;
	public static final boolean DEFAULT_DEBUG_LOGGING = false;

	private final double initialPassiveAgentChance;
	private final double peacefulInitialAgentChanceMultiplier;
	private final double easyInitialAgentChanceMultiplier;
	private final double normalInitialAgentChanceMultiplier;
	private final double hardInitialAgentChanceMultiplier;
	private final double hardcoreInitialAgentChanceMultiplier;
	private final double agentConvertPassiveChance;
	private final double agentConvertHostileToAgentChance;
	private final double agentConvertCorruptedPassiveToAgentChance;
	private final int agentConversionCooldownTicks;
	private final int transformationDurationTicks;
	private final int agentConversionDetourRadius;
	private final int maxAgentsPerChunk;
	private final boolean enableInitialOutbreaks;
	private final int outbreakCheckIntervalTicks;
	private final int outbreakScanLimitPerLevel;
	private final int outbreakEligibleRollsPerLevel;
	private final double initialOutbreakPlayerSearchRadius;
	private final boolean initialOutbreakRequiresReachablePlayer;
	private final boolean enablePlaceholderCreeperTransformSound;
	private final PassivePromotionMode passivePromotionMode;
	private final boolean excludeVillagers;
	private final boolean excludeTamedAnimals;
	private final boolean debugLogging;

	private SimulationBreachConfig(Builder builder) {
		this.initialPassiveAgentChance = builder.initialPassiveAgentChance;
		this.peacefulInitialAgentChanceMultiplier = builder.peacefulInitialAgentChanceMultiplier;
		this.easyInitialAgentChanceMultiplier = builder.easyInitialAgentChanceMultiplier;
		this.normalInitialAgentChanceMultiplier = builder.normalInitialAgentChanceMultiplier;
		this.hardInitialAgentChanceMultiplier = builder.hardInitialAgentChanceMultiplier;
		this.hardcoreInitialAgentChanceMultiplier = builder.hardcoreInitialAgentChanceMultiplier;
		this.agentConvertPassiveChance = builder.agentConvertPassiveChance;
		this.agentConvertHostileToAgentChance = builder.agentConvertHostileToAgentChance;
		this.agentConvertCorruptedPassiveToAgentChance = builder.agentConvertCorruptedPassiveToAgentChance;
		this.agentConversionCooldownTicks = builder.agentConversionCooldownTicks;
		this.transformationDurationTicks = builder.transformationDurationTicks;
		this.agentConversionDetourRadius = builder.agentConversionDetourRadius;
		this.maxAgentsPerChunk = builder.maxAgentsPerChunk;
		this.enableInitialOutbreaks = builder.enableInitialOutbreaks;
		this.outbreakCheckIntervalTicks = builder.outbreakCheckIntervalTicks;
		this.outbreakScanLimitPerLevel = builder.outbreakScanLimitPerLevel;
		this.outbreakEligibleRollsPerLevel = builder.outbreakEligibleRollsPerLevel;
		this.initialOutbreakPlayerSearchRadius = builder.initialOutbreakPlayerSearchRadius;
		this.initialOutbreakRequiresReachablePlayer = builder.initialOutbreakRequiresReachablePlayer;
		this.enablePlaceholderCreeperTransformSound = builder.enablePlaceholderCreeperTransformSound;
		this.passivePromotionMode = builder.passivePromotionMode;
		this.excludeVillagers = builder.excludeVillagers;
		this.excludeTamedAnimals = builder.excludeTamedAnimals;
		this.debugLogging = builder.debugLogging;

		validate();
	}

	public static SimulationBreachConfig defaults() {
		return builder().build();
	}

	public static Builder builder() {
		return new Builder();
	}

	public double initialPassiveAgentChance() {
		return initialPassiveAgentChance;
	}

	public double peacefulInitialAgentChanceMultiplier() {
		return peacefulInitialAgentChanceMultiplier;
	}

	public double easyInitialAgentChanceMultiplier() {
		return easyInitialAgentChanceMultiplier;
	}

	public double normalInitialAgentChanceMultiplier() {
		return normalInitialAgentChanceMultiplier;
	}

	public double hardInitialAgentChanceMultiplier() {
		return hardInitialAgentChanceMultiplier;
	}

	public double hardcoreInitialAgentChanceMultiplier() {
		return hardcoreInitialAgentChanceMultiplier;
	}

	public double agentConvertPassiveChance() {
		return agentConvertPassiveChance;
	}

	public double agentConvertHostileToAgentChance() {
		return agentConvertHostileToAgentChance;
	}

	public double agentConvertCorruptedPassiveToAgentChance() {
		return agentConvertCorruptedPassiveToAgentChance;
	}

	public int agentConversionCooldownTicks() {
		return agentConversionCooldownTicks;
	}

	public int transformationDurationTicks() {
		return transformationDurationTicks;
	}

	public int agentConversionDetourRadius() {
		return agentConversionDetourRadius;
	}

	public int maxAgentsPerChunk() {
		return maxAgentsPerChunk;
	}

	public boolean enableInitialOutbreaks() {
		return enableInitialOutbreaks;
	}

	public int outbreakCheckIntervalTicks() {
		return outbreakCheckIntervalTicks;
	}

	public int outbreakScanLimitPerLevel() {
		return outbreakScanLimitPerLevel;
	}

	public int outbreakEligibleRollsPerLevel() {
		return outbreakEligibleRollsPerLevel;
	}

	public double initialOutbreakPlayerSearchRadius() {
		return initialOutbreakPlayerSearchRadius;
	}

	public boolean initialOutbreakRequiresReachablePlayer() {
		return initialOutbreakRequiresReachablePlayer;
	}

	public boolean enablePlaceholderCreeperTransformSound() {
		return enablePlaceholderCreeperTransformSound;
	}

	public PassivePromotionMode passivePromotionMode() {
		return passivePromotionMode;
	}

	public boolean excludeVillagers() {
		return excludeVillagers;
	}

	public boolean excludeTamedAnimals() {
		return excludeTamedAnimals;
	}

	public boolean debugLogging() {
		return debugLogging;
	}

	private void validate() {
		validateChance("initialPassiveAgentChance", initialPassiveAgentChance);
		validateChance("agentConvertPassiveChance", agentConvertPassiveChance);
		validateChance("agentConvertHostileToAgentChance", agentConvertHostileToAgentChance);
		validateChance("agentConvertCorruptedPassiveToAgentChance", agentConvertCorruptedPassiveToAgentChance);
		validateNonNegative("peacefulInitialAgentChanceMultiplier", peacefulInitialAgentChanceMultiplier);
		validateNonNegative("easyInitialAgentChanceMultiplier", easyInitialAgentChanceMultiplier);
		validateNonNegative("normalInitialAgentChanceMultiplier", normalInitialAgentChanceMultiplier);
		validateNonNegative("hardInitialAgentChanceMultiplier", hardInitialAgentChanceMultiplier);
		validateNonNegative("hardcoreInitialAgentChanceMultiplier", hardcoreInitialAgentChanceMultiplier);
		validatePositive("agentConversionCooldownTicks", agentConversionCooldownTicks);
		validatePositive("transformationDurationTicks", transformationDurationTicks);
		validateNonNegative("agentConversionDetourRadius", agentConversionDetourRadius);
		validateNonNegative("maxAgentsPerChunk", maxAgentsPerChunk);
		validatePositive("outbreakCheckIntervalTicks", outbreakCheckIntervalTicks);
		validatePositive("outbreakScanLimitPerLevel", outbreakScanLimitPerLevel);
		validatePositive("outbreakEligibleRollsPerLevel", outbreakEligibleRollsPerLevel);
		validatePositive("initialOutbreakPlayerSearchRadius", initialOutbreakPlayerSearchRadius);

		if (passivePromotionMode == null) {
			throw new IllegalArgumentException("passivePromotionMode must not be null");
		}

		if (peacefulInitialAgentChanceMultiplier > easyInitialAgentChanceMultiplier
				|| easyInitialAgentChanceMultiplier > normalInitialAgentChanceMultiplier
				|| normalInitialAgentChanceMultiplier > hardInitialAgentChanceMultiplier
				|| hardInitialAgentChanceMultiplier > hardcoreInitialAgentChanceMultiplier) {
			throw new IllegalArgumentException("initial Agent difficulty multipliers must be ordered from Peaceful lowest to Hardcore highest");
		}
	}

	private static void validateChance(String name, double value) {
		if (value < 0.0D || value > 1.0D) {
			throw new IllegalArgumentException(name + " must be between 0.0 and 1.0");
		}
	}

	private static void validateNonNegative(String name, double value) {
		if (value < 0.0D) {
			throw new IllegalArgumentException(name + " must be non-negative");
		}
	}

	private static void validateNonNegative(String name, int value) {
		if (value < 0) {
			throw new IllegalArgumentException(name + " must be non-negative");
		}
	}

	private static void validatePositive(String name, int value) {
		if (value <= 0) {
			throw new IllegalArgumentException(name + " must be positive");
		}
	}

	private static void validatePositive(String name, double value) {
		if (value <= 0.0D) {
			throw new IllegalArgumentException(name + " must be positive");
		}
	}

	public static final class Builder {
		private double initialPassiveAgentChance = DEFAULT_INITIAL_PASSIVE_AGENT_CHANCE;
		private double peacefulInitialAgentChanceMultiplier = DEFAULT_PEACEFUL_INITIAL_AGENT_CHANCE_MULTIPLIER;
		private double easyInitialAgentChanceMultiplier = DEFAULT_EASY_INITIAL_AGENT_CHANCE_MULTIPLIER;
		private double normalInitialAgentChanceMultiplier = DEFAULT_NORMAL_INITIAL_AGENT_CHANCE_MULTIPLIER;
		private double hardInitialAgentChanceMultiplier = DEFAULT_HARD_INITIAL_AGENT_CHANCE_MULTIPLIER;
		private double hardcoreInitialAgentChanceMultiplier = DEFAULT_HARDCORE_INITIAL_AGENT_CHANCE_MULTIPLIER;
		private double agentConvertPassiveChance = DEFAULT_AGENT_CONVERT_PASSIVE_CHANCE;
		private double agentConvertHostileToAgentChance = DEFAULT_AGENT_CONVERT_HOSTILE_TO_AGENT_CHANCE;
		private double agentConvertCorruptedPassiveToAgentChance = DEFAULT_AGENT_CONVERT_CORRUPTED_PASSIVE_TO_AGENT_CHANCE;
		private int agentConversionCooldownTicks = DEFAULT_AGENT_CONVERSION_COOLDOWN_TICKS;
		private int transformationDurationTicks = DEFAULT_TRANSFORMATION_DURATION_TICKS;
		private int agentConversionDetourRadius = DEFAULT_AGENT_CONVERSION_DETOUR_RADIUS;
		private int maxAgentsPerChunk = DEFAULT_MAX_AGENTS_PER_CHUNK;
		private boolean enableInitialOutbreaks = DEFAULT_ENABLE_INITIAL_OUTBREAKS;
		private int outbreakCheckIntervalTicks = DEFAULT_OUTBREAK_CHECK_INTERVAL_TICKS;
		private int outbreakScanLimitPerLevel = DEFAULT_OUTBREAK_SCAN_LIMIT_PER_LEVEL;
		private int outbreakEligibleRollsPerLevel = DEFAULT_OUTBREAK_ELIGIBLE_ROLLS_PER_LEVEL;
		private double initialOutbreakPlayerSearchRadius = DEFAULT_INITIAL_OUTBREAK_PLAYER_SEARCH_RADIUS;
		private boolean initialOutbreakRequiresReachablePlayer = DEFAULT_INITIAL_OUTBREAK_REQUIRES_REACHABLE_PLAYER;
		private boolean enablePlaceholderCreeperTransformSound = DEFAULT_ENABLE_PLACEHOLDER_CREEPER_TRANSFORM_SOUND;
		private PassivePromotionMode passivePromotionMode = DEFAULT_PASSIVE_PROMOTION_MODE;
		private boolean excludeVillagers = DEFAULT_EXCLUDE_VILLAGERS;
		private boolean excludeTamedAnimals = DEFAULT_EXCLUDE_TAMED_ANIMALS;
		private boolean debugLogging = DEFAULT_DEBUG_LOGGING;

		private Builder() {
		}

		public Builder initialPassiveAgentChance(double initialPassiveAgentChance) {
			this.initialPassiveAgentChance = initialPassiveAgentChance;
			return this;
		}

		public Builder peacefulInitialAgentChanceMultiplier(double peacefulInitialAgentChanceMultiplier) {
			this.peacefulInitialAgentChanceMultiplier = peacefulInitialAgentChanceMultiplier;
			return this;
		}

		public Builder easyInitialAgentChanceMultiplier(double easyInitialAgentChanceMultiplier) {
			this.easyInitialAgentChanceMultiplier = easyInitialAgentChanceMultiplier;
			return this;
		}

		public Builder normalInitialAgentChanceMultiplier(double normalInitialAgentChanceMultiplier) {
			this.normalInitialAgentChanceMultiplier = normalInitialAgentChanceMultiplier;
			return this;
		}

		public Builder hardInitialAgentChanceMultiplier(double hardInitialAgentChanceMultiplier) {
			this.hardInitialAgentChanceMultiplier = hardInitialAgentChanceMultiplier;
			return this;
		}

		public Builder hardcoreInitialAgentChanceMultiplier(double hardcoreInitialAgentChanceMultiplier) {
			this.hardcoreInitialAgentChanceMultiplier = hardcoreInitialAgentChanceMultiplier;
			return this;
		}

		public Builder agentConvertPassiveChance(double agentConvertPassiveChance) {
			this.agentConvertPassiveChance = agentConvertPassiveChance;
			return this;
		}

		public Builder agentConvertHostileToAgentChance(double agentConvertHostileToAgentChance) {
			this.agentConvertHostileToAgentChance = agentConvertHostileToAgentChance;
			return this;
		}

		public Builder agentConvertCorruptedPassiveToAgentChance(double agentConvertCorruptedPassiveToAgentChance) {
			this.agentConvertCorruptedPassiveToAgentChance = agentConvertCorruptedPassiveToAgentChance;
			return this;
		}

		public Builder agentConversionCooldownTicks(int agentConversionCooldownTicks) {
			this.agentConversionCooldownTicks = agentConversionCooldownTicks;
			return this;
		}

		public Builder transformationDurationTicks(int transformationDurationTicks) {
			this.transformationDurationTicks = transformationDurationTicks;
			return this;
		}

		public Builder agentConversionDetourRadius(int agentConversionDetourRadius) {
			this.agentConversionDetourRadius = agentConversionDetourRadius;
			return this;
		}

		public Builder maxAgentsPerChunk(int maxAgentsPerChunk) {
			this.maxAgentsPerChunk = maxAgentsPerChunk;
			return this;
		}

		public Builder enableInitialOutbreaks(boolean enableInitialOutbreaks) {
			this.enableInitialOutbreaks = enableInitialOutbreaks;
			return this;
		}

		public Builder outbreakCheckIntervalTicks(int outbreakCheckIntervalTicks) {
			this.outbreakCheckIntervalTicks = outbreakCheckIntervalTicks;
			return this;
		}

		public Builder outbreakScanLimitPerLevel(int outbreakScanLimitPerLevel) {
			this.outbreakScanLimitPerLevel = outbreakScanLimitPerLevel;
			return this;
		}

		public Builder outbreakEligibleRollsPerLevel(int outbreakEligibleRollsPerLevel) {
			this.outbreakEligibleRollsPerLevel = outbreakEligibleRollsPerLevel;
			return this;
		}

		public Builder initialOutbreakPlayerSearchRadius(double initialOutbreakPlayerSearchRadius) {
			this.initialOutbreakPlayerSearchRadius = initialOutbreakPlayerSearchRadius;
			return this;
		}

		public Builder initialOutbreakRequiresReachablePlayer(boolean initialOutbreakRequiresReachablePlayer) {
			this.initialOutbreakRequiresReachablePlayer = initialOutbreakRequiresReachablePlayer;
			return this;
		}

		public Builder enablePlaceholderCreeperTransformSound(boolean enablePlaceholderCreeperTransformSound) {
			this.enablePlaceholderCreeperTransformSound = enablePlaceholderCreeperTransformSound;
			return this;
		}

		public Builder passivePromotionMode(PassivePromotionMode passivePromotionMode) {
			this.passivePromotionMode = passivePromotionMode;
			return this;
		}

		public Builder excludeVillagers(boolean excludeVillagers) {
			this.excludeVillagers = excludeVillagers;
			return this;
		}

		public Builder excludeTamedAnimals(boolean excludeTamedAnimals) {
			this.excludeTamedAnimals = excludeTamedAnimals;
			return this;
		}

		public Builder debugLogging(boolean debugLogging) {
			this.debugLogging = debugLogging;
			return this;
		}

		public SimulationBreachConfig build() {
			return new SimulationBreachConfig(this);
		}
	}
}
