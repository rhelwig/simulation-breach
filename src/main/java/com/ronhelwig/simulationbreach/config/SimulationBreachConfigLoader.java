package com.ronhelwig.simulationbreach.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SimulationBreachConfigLoader {
	private static final Gson GSON = new GsonBuilder()
			.setPrettyPrinting()
			.create();

	private SimulationBreachConfigLoader() {
	}

	public static SimulationBreachConfig load(String modId, Logger logger) {
		Path configPath = FabricLoader.getInstance().getConfigDir().resolve(modId + ".json");
		return load(configPath, logger);
	}

	static SimulationBreachConfig load(Path configPath, Logger logger) {
		SimulationBreachConfig defaults = SimulationBreachConfig.defaults();

		if (Files.notExists(configPath)) {
			writeDefaults(configPath, defaults, logger);
			return defaults;
		}

		try {
			String json = Files.readString(configPath, StandardCharsets.UTF_8);
			ConfigData data = GSON.fromJson(json, ConfigData.class);
			if (data == null) {
				logger.warn("Config file {} was empty. Using Simulation Breach defaults.", configPath);
				return defaults;
			}
			return data.toConfig();
		} catch (JsonSyntaxException | IllegalArgumentException exception) {
			logger.warn("Config file {} is invalid. Using Simulation Breach defaults.", configPath, exception);
			return defaults;
		} catch (IOException exception) {
			logger.warn("Could not read config file {}. Using Simulation Breach defaults.", configPath, exception);
			return defaults;
		}
	}

	private static void writeDefaults(Path configPath, SimulationBreachConfig defaults, Logger logger) {
		try {
			Path parent = configPath.getParent();
			if (parent != null) {
				Files.createDirectories(parent);
			}
			String json = GSON.toJson(ConfigData.from(defaults)) + System.lineSeparator();
			Files.writeString(configPath, json, StandardCharsets.UTF_8);
			logger.info("Created default Simulation Breach config at {}", configPath);
		} catch (IOException exception) {
			logger.warn("Could not write default Simulation Breach config at {}. Using in-memory defaults.", configPath, exception);
		}
	}

	private static final class ConfigData {
		private Double initialPassiveAgentChance;
		private Double peacefulInitialAgentChanceMultiplier;
		private Double easyInitialAgentChanceMultiplier;
		private Double normalInitialAgentChanceMultiplier;
		private Double hardInitialAgentChanceMultiplier;
		private Double hardcoreInitialAgentChanceMultiplier;
		private Double agentConvertPassiveChance;
		private Double agentConvertHostileToAgentChance;
		private Double agentConvertCorruptedPassiveToAgentChance;
		private Integer agentConversionCooldownTicks;
		private Integer transformationDurationTicks;
		private Integer agentExperienceReward;
		private Integer agentConversionDetourRadius;
		private Integer maxAgentsPerChunk;
		private Boolean enableInitialOutbreaks;
		private Integer outbreakCheckIntervalTicks;
		private Integer outbreakScanLimitPerLevel;
		private Integer outbreakEligibleRollsPerLevel;
		private Double initialOutbreakPlayerSearchRadius;
		private Boolean initialOutbreakRequiresReachablePlayer;
		private Boolean enableAgentTransformSound;
		private Boolean enablePlaceholderCreeperTransformSound;
		private Boolean enablePlayerLingerOutbreakPressure;
		private Double playerLingerPressureRadius;
		private Integer playerLingerPressureGraceTicks;
		private Integer playerLingerPressureRampTicks;
		private Double maxPlayerLingerPressureMultiplier;
		private Boolean enableNaturalOutbreakChatNotice;
		private PassivePromotionMode passivePromotionMode;
		private Boolean excludeVillagers;
		private Boolean excludeTamedAnimals;
		private Boolean debugLogging;

		static ConfigData from(SimulationBreachConfig config) {
			ConfigData data = new ConfigData();
			data.initialPassiveAgentChance = config.initialPassiveAgentChance();
			data.peacefulInitialAgentChanceMultiplier = config.peacefulInitialAgentChanceMultiplier();
			data.easyInitialAgentChanceMultiplier = config.easyInitialAgentChanceMultiplier();
			data.normalInitialAgentChanceMultiplier = config.normalInitialAgentChanceMultiplier();
			data.hardInitialAgentChanceMultiplier = config.hardInitialAgentChanceMultiplier();
			data.hardcoreInitialAgentChanceMultiplier = config.hardcoreInitialAgentChanceMultiplier();
			data.agentConvertPassiveChance = config.agentConvertPassiveChance();
			data.agentConvertHostileToAgentChance = config.agentConvertHostileToAgentChance();
			data.agentConvertCorruptedPassiveToAgentChance = config.agentConvertCorruptedPassiveToAgentChance();
			data.agentConversionCooldownTicks = config.agentConversionCooldownTicks();
			data.transformationDurationTicks = config.transformationDurationTicks();
			data.agentExperienceReward = config.agentExperienceReward();
			data.agentConversionDetourRadius = config.agentConversionDetourRadius();
			data.maxAgentsPerChunk = config.maxAgentsPerChunk();
			data.enableInitialOutbreaks = config.enableInitialOutbreaks();
			data.outbreakCheckIntervalTicks = config.outbreakCheckIntervalTicks();
			data.outbreakScanLimitPerLevel = config.outbreakScanLimitPerLevel();
			data.outbreakEligibleRollsPerLevel = config.outbreakEligibleRollsPerLevel();
			data.initialOutbreakPlayerSearchRadius = config.initialOutbreakPlayerSearchRadius();
			data.initialOutbreakRequiresReachablePlayer = config.initialOutbreakRequiresReachablePlayer();
			data.enableAgentTransformSound = config.enableAgentTransformSound();
			data.enablePlaceholderCreeperTransformSound = config.enablePlaceholderCreeperTransformSound();
			data.enablePlayerLingerOutbreakPressure = config.enablePlayerLingerOutbreakPressure();
			data.playerLingerPressureRadius = config.playerLingerPressureRadius();
			data.playerLingerPressureGraceTicks = config.playerLingerPressureGraceTicks();
			data.playerLingerPressureRampTicks = config.playerLingerPressureRampTicks();
			data.maxPlayerLingerPressureMultiplier = config.maxPlayerLingerPressureMultiplier();
			data.enableNaturalOutbreakChatNotice = config.enableNaturalOutbreakChatNotice();
			data.passivePromotionMode = config.passivePromotionMode();
			data.excludeVillagers = config.excludeVillagers();
			data.excludeTamedAnimals = config.excludeTamedAnimals();
			data.debugLogging = config.debugLogging();
			return data;
		}

		SimulationBreachConfig toConfig() {
			SimulationBreachConfig.Builder builder = SimulationBreachConfig.builder();
			if (initialPassiveAgentChance != null) {
				builder.initialPassiveAgentChance(initialPassiveAgentChance);
			}
			if (peacefulInitialAgentChanceMultiplier != null) {
				builder.peacefulInitialAgentChanceMultiplier(peacefulInitialAgentChanceMultiplier);
			}
			if (easyInitialAgentChanceMultiplier != null) {
				builder.easyInitialAgentChanceMultiplier(easyInitialAgentChanceMultiplier);
			}
			if (normalInitialAgentChanceMultiplier != null) {
				builder.normalInitialAgentChanceMultiplier(normalInitialAgentChanceMultiplier);
			}
			if (hardInitialAgentChanceMultiplier != null) {
				builder.hardInitialAgentChanceMultiplier(hardInitialAgentChanceMultiplier);
			}
			if (hardcoreInitialAgentChanceMultiplier != null) {
				builder.hardcoreInitialAgentChanceMultiplier(hardcoreInitialAgentChanceMultiplier);
			}
			if (agentConvertPassiveChance != null) {
				builder.agentConvertPassiveChance(agentConvertPassiveChance);
			}
			if (agentConvertHostileToAgentChance != null) {
				builder.agentConvertHostileToAgentChance(agentConvertHostileToAgentChance);
			}
			if (agentConvertCorruptedPassiveToAgentChance != null) {
				builder.agentConvertCorruptedPassiveToAgentChance(agentConvertCorruptedPassiveToAgentChance);
			}
			if (agentConversionCooldownTicks != null) {
				builder.agentConversionCooldownTicks(agentConversionCooldownTicks);
			}
			if (transformationDurationTicks != null) {
				builder.transformationDurationTicks(transformationDurationTicks);
			}
			if (agentExperienceReward != null) {
				builder.agentExperienceReward(agentExperienceReward);
			}
			if (agentConversionDetourRadius != null) {
				builder.agentConversionDetourRadius(agentConversionDetourRadius);
			}
			if (maxAgentsPerChunk != null) {
				builder.maxAgentsPerChunk(maxAgentsPerChunk);
			}
			if (enableInitialOutbreaks != null) {
				builder.enableInitialOutbreaks(enableInitialOutbreaks);
			}
			if (outbreakCheckIntervalTicks != null) {
				builder.outbreakCheckIntervalTicks(outbreakCheckIntervalTicks);
			}
			if (outbreakScanLimitPerLevel != null) {
				builder.outbreakScanLimitPerLevel(outbreakScanLimitPerLevel);
			}
			if (outbreakEligibleRollsPerLevel != null) {
				builder.outbreakEligibleRollsPerLevel(outbreakEligibleRollsPerLevel);
			}
			if (initialOutbreakPlayerSearchRadius != null) {
				builder.initialOutbreakPlayerSearchRadius(initialOutbreakPlayerSearchRadius);
			}
			if (initialOutbreakRequiresReachablePlayer != null) {
				builder.initialOutbreakRequiresReachablePlayer(initialOutbreakRequiresReachablePlayer);
			}
			if (enableAgentTransformSound != null) {
				builder.enableAgentTransformSound(enableAgentTransformSound);
			}
			if (enablePlaceholderCreeperTransformSound != null) {
				builder.enablePlaceholderCreeperTransformSound(enablePlaceholderCreeperTransformSound);
			}
			if (enablePlayerLingerOutbreakPressure != null) {
				builder.enablePlayerLingerOutbreakPressure(enablePlayerLingerOutbreakPressure);
			}
			if (playerLingerPressureRadius != null) {
				builder.playerLingerPressureRadius(playerLingerPressureRadius);
			}
			if (playerLingerPressureGraceTicks != null) {
				builder.playerLingerPressureGraceTicks(playerLingerPressureGraceTicks);
			}
			if (playerLingerPressureRampTicks != null) {
				builder.playerLingerPressureRampTicks(playerLingerPressureRampTicks);
			}
			if (maxPlayerLingerPressureMultiplier != null) {
				builder.maxPlayerLingerPressureMultiplier(maxPlayerLingerPressureMultiplier);
			}
			if (enableNaturalOutbreakChatNotice != null) {
				builder.enableNaturalOutbreakChatNotice(enableNaturalOutbreakChatNotice);
			}
			if (passivePromotionMode != null) {
				builder.passivePromotionMode(passivePromotionMode);
			}
			if (excludeVillagers != null) {
				builder.excludeVillagers(excludeVillagers);
			}
			if (excludeTamedAnimals != null) {
				builder.excludeTamedAnimals(excludeTamedAnimals);
			}
			if (debugLogging != null) {
				builder.debugLogging(debugLogging);
			}
			return builder.build();
		}
	}
}
