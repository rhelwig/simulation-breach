package com.ronhelwig.simulationbreach.outbreak;

import com.ronhelwig.simulationbreach.SimulationBreach;
import com.ronhelwig.simulationbreach.config.SimulationBreachConfig;
import com.ronhelwig.simulationbreach.conversion.ConversionManager;
import com.ronhelwig.simulationbreach.entity.AgentEntity;
import com.ronhelwig.simulationbreach.gameplay.InfectionRules;
import com.ronhelwig.simulationbreach.gameplay.OutbreakDifficulty;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.pathfinder.Path;

import java.util.Random;

public final class OutbreakManager {
	private static final Random RANDOM = new Random();

	private OutbreakManager() {
	}

	public static void register() {
		ServerTickEvents.END_SERVER_TICK.register(OutbreakManager::onEndServerTick);
		SimulationBreach.LOGGER.info("Registered initial outbreak scheduler");
	}

	private static void onEndServerTick(MinecraftServer server) {
		SimulationBreachConfig config = SimulationBreach.CONFIG;
		if (!config.enableInitialOutbreaks() || !shouldRunThisTick(server, config)) {
			return;
		}

		long startedNanos = System.nanoTime();
		OutbreakCheckResult total = OutbreakCheckResult.empty();
		for (ServerLevel level : server.getAllLevels()) {
			total = total.plus(checkLevel(server, level, config));
		}

		if (config.debugLogging()) {
			long elapsedMicros = (System.nanoTime() - startedNanos) / 1_000L;
			SimulationBreach.LOGGER.info(
					"Initial outbreak check: levels={}, scanned={}, eligible={}, rolls={}, candidateSelections={}, elapsed={}us",
					total.levelsChecked(),
					total.entitiesScanned(),
					total.eligibleEntitiesFound(),
					total.chanceRolls(),
					total.candidateSelections(),
					elapsedMicros
			);
		}
	}

	private static boolean shouldRunThisTick(MinecraftServer server, SimulationBreachConfig config) {
		return server.getTickCount() % config.outbreakCheckIntervalTicks() == 0;
	}

	private static OutbreakCheckResult checkLevel(MinecraftServer server, ServerLevel level, SimulationBreachConfig config) {
		int entitiesScanned = 0;
		int eligibleEntitiesFound = 0;
		int chanceRolls = 0;
		int candidateSelections = 0;
		OutbreakDifficulty difficulty = difficultyFor(server, level);

		for (Entity entity : level.getAllEntities()) {
			if (entitiesScanned >= config.outbreakScanLimitPerLevel()
					|| chanceRolls >= config.outbreakEligibleRollsPerLevel()) {
				break;
			}

			entitiesScanned++;
			if (!isInitialOutbreakEligible(level, entity, config)) {
				continue;
			}

			eligibleEntitiesFound++;
			chanceRolls++;
			if (InfectionRules.shouldStartInitialAgentOutbreak(RANDOM, config, difficulty)
					&& ConversionManager.beginAgentTransformation(level, mob(entity), config, null, "initial_outbreak")) {
				candidateSelections++;
				logCandidateSelection(level, entity, difficulty, config);
			}
		}

		return new OutbreakCheckResult(1, entitiesScanned, eligibleEntitiesFound, chanceRolls, candidateSelections);
	}

	private static boolean isInitialOutbreakEligible(ServerLevel level, Entity entity, SimulationBreachConfig config) {
		if (!entity.isAlive() || entity.isRemoved() || entity instanceof AgentEntity) {
			return false;
		}
		if (config.excludeTamedAnimals() && entity instanceof TamableAnimal tamableAnimal && tamableAnimal.isTame()) {
			return false;
		}

		if (!(entity instanceof Mob mob)) {
			return false;
		}

		boolean eligibleType = entity instanceof Animal || (!config.excludeVillagers() && entity instanceof Villager);
		if (!eligibleType) {
			return false;
		}

		return hasNearbyPlayerWithReasonableRoute(level, mob, config);
	}

	private static Mob mob(Entity entity) {
		return (Mob) entity;
	}

	private static boolean hasNearbyPlayerWithReasonableRoute(ServerLevel level, Mob mob, SimulationBreachConfig config) {
		double maxDistanceSqr = config.initialOutbreakPlayerSearchRadius() * config.initialOutbreakPlayerSearchRadius();
		for (ServerPlayer player : level.players()) {
			if (!player.isAlive() || player.isSpectator() || mob.distanceToSqr(player) > maxDistanceSqr) {
				continue;
			}
			if (!config.initialOutbreakRequiresReachablePlayer()) {
				return true;
			}

			Path path = mob.getNavigation().createPath(player, 0);
			if (path != null && path.canReach()) {
				return true;
			}
		}
		return false;
	}

	private static OutbreakDifficulty difficultyFor(MinecraftServer server, ServerLevel level) {
		if (server.isHardcore()) {
			return OutbreakDifficulty.HARDCORE;
		}

		Difficulty difficulty = level.getDifficulty();
		return switch (difficulty) {
			case PEACEFUL -> OutbreakDifficulty.PEACEFUL;
			case EASY -> OutbreakDifficulty.EASY;
			case NORMAL -> OutbreakDifficulty.NORMAL;
			case HARD -> OutbreakDifficulty.HARD;
		};
	}

	private static void logCandidateSelection(
			ServerLevel level,
			Entity entity,
			OutbreakDifficulty difficulty,
			SimulationBreachConfig config
	) {
		if (!config.debugLogging()) {
			return;
		}

		Identifier entityTypeId = EntityType.getKey(entity.getType());
		SimulationBreach.LOGGER.info(
				"Initial outbreak candidate selected: entityType={}, dimension={}, position={}, difficulty={}, chance={}",
				entityTypeId,
				level.dimension().identifier(),
				entity.blockPosition(),
				difficulty,
				InfectionRules.initialPassiveAgentChance(config, difficulty)
		);
	}

	private record OutbreakCheckResult(
			int levelsChecked,
			int entitiesScanned,
			int eligibleEntitiesFound,
			int chanceRolls,
			int candidateSelections
	) {
		static OutbreakCheckResult empty() {
			return new OutbreakCheckResult(0, 0, 0, 0, 0);
		}

		OutbreakCheckResult plus(OutbreakCheckResult other) {
			return new OutbreakCheckResult(
					levelsChecked + other.levelsChecked,
					entitiesScanned + other.entitiesScanned,
					eligibleEntitiesFound + other.eligibleEntitiesFound,
					chanceRolls + other.chanceRolls,
					candidateSelections + other.candidateSelections
			);
		}
	}
}
