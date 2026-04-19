package com.ronhelwig.simulationbreach.outbreak;

import com.ronhelwig.simulationbreach.SimulationBreach;
import com.ronhelwig.simulationbreach.config.SimulationBreachConfig;
import com.ronhelwig.simulationbreach.conversion.ConversionManager;
import com.ronhelwig.simulationbreach.entity.AgentEntity;
import com.ronhelwig.simulationbreach.gameplay.InfectionRules;
import com.ronhelwig.simulationbreach.gameplay.OutbreakDifficulty;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public final class OutbreakManager {
	private static final Random RANDOM = new Random();
	private static final Map<UUID, PlayerLingerState> PLAYER_LINGER_STATES = new HashMap<>();

	private OutbreakManager() {
	}

	public static void register() {
		ServerTickEvents.END_SERVER_TICK.register(OutbreakManager::onEndServerTick);
		SimulationBreach.LOGGER.info("Registered initial outbreak scheduler");
	}

	private static void onEndServerTick(MinecraftServer server) {
		SimulationBreachConfig config = SimulationBreach.CONFIG;
		updatePlayerLingerStates(server, config);
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
			SimulationBreach.LOGGER.info(
					"Initial outbreak pressure: activeLingerRecords={}, lingerPressureRolls={}",
					PLAYER_LINGER_STATES.size(),
					total.lingerPressureRolls()
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
		int lingerPressureRolls = 0;
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
			double pressureMultiplier = playerLingerPressureMultiplier(level, mob(entity), config);
			if (pressureMultiplier > 1.0D) {
				lingerPressureRolls++;
			}
			if (InfectionRules.shouldStartInitialAgentOutbreak(RANDOM, config, difficulty, pressureMultiplier)
					&& ConversionManager.beginAgentTransformation(level, mob(entity), config, null, "initial_outbreak")) {
				candidateSelections++;
				logCandidateSelection(level, entity, difficulty, config, pressureMultiplier);
			}
		}

		return new OutbreakCheckResult(1, entitiesScanned, eligibleEntitiesFound, chanceRolls, candidateSelections, lingerPressureRolls);
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

	private static void updatePlayerLingerStates(MinecraftServer server, SimulationBreachConfig config) {
		if (!config.enablePlayerLingerOutbreakPressure()) {
			PLAYER_LINGER_STATES.clear();
			return;
		}

		double resetDistanceSqr = config.playerLingerPressureRadius() * config.playerLingerPressureRadius();
		Set<UUID> activePlayers = new HashSet<>();
		for (ServerLevel level : server.getAllLevels()) {
			for (ServerPlayer player : level.players()) {
				if (!player.isAlive() || player.isSpectator()) {
					continue;
				}

				UUID playerId = player.getUUID();
				activePlayers.add(playerId);
				long gameTime = level.getGameTime();
				PlayerLingerState state = PLAYER_LINGER_STATES.get(playerId);
				if (state == null
						|| !state.dimension().equals(level.dimension())
						|| state.distanceToSqr(player) > resetDistanceSqr) {
					PLAYER_LINGER_STATES.put(playerId, PlayerLingerState.from(level, player, gameTime));
				}
			}
		}
		PLAYER_LINGER_STATES.keySet().retainAll(activePlayers);
	}

	private static double playerLingerPressureMultiplier(ServerLevel level, Mob mob, SimulationBreachConfig config) {
		if (!config.enablePlayerLingerOutbreakPressure() || PLAYER_LINGER_STATES.isEmpty()) {
			return 1.0D;
		}

		long gameTime = level.getGameTime();
		double pressureRadiusSqr = config.playerLingerPressureRadius() * config.playerLingerPressureRadius();
		double bestMultiplier = 1.0D;
		for (PlayerLingerState state : PLAYER_LINGER_STATES.values()) {
			if (!state.dimension().equals(level.dimension()) || state.distanceToSqr(mob) > pressureRadiusSqr) {
				continue;
			}
			bestMultiplier = Math.max(bestMultiplier, state.pressureMultiplier(gameTime, config));
		}
		return bestMultiplier;
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
			SimulationBreachConfig config,
			double pressureMultiplier
	) {
		if (!config.debugLogging()) {
			return;
		}

		Identifier entityTypeId = EntityType.getKey(entity.getType());
		SimulationBreach.LOGGER.info(
				"Initial outbreak candidate selected: entityType={}, dimension={}, position={}, difficulty={}, chance={}, pressureMultiplier={}",
				entityTypeId,
				level.dimension().identifier(),
				entity.blockPosition(),
				difficulty,
				InfectionRules.initialPassiveAgentChance(config, difficulty, pressureMultiplier),
				pressureMultiplier
		);
	}

	private record PlayerLingerState(
			ResourceKey<Level> dimension,
			double anchorX,
			double anchorY,
			double anchorZ,
			long anchorGameTime
	) {
		static PlayerLingerState from(ServerLevel level, ServerPlayer player, long gameTime) {
			return new PlayerLingerState(
					level.dimension(),
					player.getX(),
					player.getY(),
					player.getZ(),
					gameTime
			);
		}

		double distanceToSqr(Entity entity) {
			double dx = entity.getX() - anchorX;
			double dy = entity.getY() - anchorY;
			double dz = entity.getZ() - anchorZ;
			return dx * dx + dy * dy + dz * dz;
		}

		double pressureMultiplier(long gameTime, SimulationBreachConfig config) {
			long lingerTicks = Math.max(0L, gameTime - anchorGameTime);
			if (lingerTicks <= config.playerLingerPressureGraceTicks()) {
				return 1.0D;
			}

			double rampTicks = Math.max(1.0D, config.playerLingerPressureRampTicks());
			double progress = Math.min(1.0D, (lingerTicks - config.playerLingerPressureGraceTicks()) / rampTicks);
			return 1.0D + ((config.maxPlayerLingerPressureMultiplier() - 1.0D) * progress);
		}
	}

	private record OutbreakCheckResult(
			int levelsChecked,
			int entitiesScanned,
			int eligibleEntitiesFound,
			int chanceRolls,
			int candidateSelections,
			int lingerPressureRolls
	) {
		static OutbreakCheckResult empty() {
			return new OutbreakCheckResult(0, 0, 0, 0, 0, 0);
		}

		OutbreakCheckResult plus(OutbreakCheckResult other) {
			return new OutbreakCheckResult(
					levelsChecked + other.levelsChecked,
					entitiesScanned + other.entitiesScanned,
					eligibleEntitiesFound + other.eligibleEntitiesFound,
					chanceRolls + other.chanceRolls,
					candidateSelections + other.candidateSelections,
					lingerPressureRolls + other.lingerPressureRolls
			);
		}
	}
}
