package com.ronhelwig.simulationbreach.conversion;

import com.ronhelwig.simulationbreach.SimulationBreach;
import com.ronhelwig.simulationbreach.config.SimulationBreachConfig;
import com.ronhelwig.simulationbreach.entity.AgentEntity;
import com.ronhelwig.simulationbreach.entity.ModEntities;
import com.ronhelwig.simulationbreach.identity.BreachDataHolder;
import com.ronhelwig.simulationbreach.identity.BreachEntityData;
import com.ronhelwig.simulationbreach.identity.InfectionStage;
import com.ronhelwig.simulationbreach.identity.OriginDisposition;
import com.ronhelwig.simulationbreach.identity.TransformationState;
import com.ronhelwig.simulationbreach.network.TransformationPresentationNetworking;
import com.ronhelwig.simulationbreach.transformation.TransformationManager;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;

import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

public final class ConversionManager {
	private ConversionManager() {
	}

	public static boolean beginAgentTransformation(
			ServerLevel level,
			LivingEntity source,
			SimulationBreachConfig config,
			Entity convertedBy,
			String debugSource
	) {
		Objects.requireNonNull(level, "level");
		Objects.requireNonNull(source, "source");
		Objects.requireNonNull(config, "config");

		if (!source.isAlive() || source.isRemoved() || source instanceof AgentEntity) {
			return false;
		}
		if (isAgentCapReached(level, source, config)) {
			logDebug(config, "Denied Agent transformation for {} at {} because the local Agent cap is reached",
					EntityType.getKey(source.getType()), source.blockPosition());
			return false;
		}

		BreachEntityData sourceData = breachData(source).orElseGet(() -> cleanDataFor(source));
		if (sourceData.transformationState() == TransformationState.TRANSFORMING) {
			return false;
		}
		long gameTime = level.getGameTime();
		if (sourceData.immuneUntilGameTime().isPresent()
				&& sourceData.immuneUntilGameTime().getAsLong() > gameTime) {
			return false;
		}

		BreachEntityData transformingData = TransformationManager.beginTransformation(
						sourceData,
						gameTime,
						config,
						ModEntities.AGENT_ID.toString(),
						InfectionStage.AGENT
				)
				.toBuilder()
				.lastConversionGameTime(gameTime)
				.convertedByEntityUuid(convertedBy == null ? null : convertedBy.getUUID())
				.conversionGeneration(nextGeneration(sourceData))
				.debugSource(debugSource)
				.build();

		setBreachData(source, transformingData);
		TransformationPresentationNetworking.sendStart(source, config.transformationDurationTicks());
		playTransformationStartSound(level, source, config);
		logDebug(config, "Started Agent transformation for {} at {} with duration {} ticks",
				EntityType.getKey(source.getType()), source.blockPosition(), config.transformationDurationTicks());
		return true;
	}

	public static Optional<AgentEntity> completeTransformationIfReady(ServerLevel level, LivingEntity source) {
		Objects.requireNonNull(level, "level");
		Objects.requireNonNull(source, "source");

		Optional<BreachEntityData> data = breachData(source);
		if (data.isEmpty()) {
			return Optional.empty();
		}
		if (!source.isAlive() || source.isRemoved()) {
			cancelTransformation(source, "source_invalid");
			return Optional.empty();
		}
		if (!TransformationManager.isComplete(data.get(), level.getGameTime())) {
			return Optional.empty();
		}

		BreachEntityData sourceData = data.get();
		if (!sourceData.pendingReplacementEntityType().orElse("").equals(ModEntities.AGENT_ID.toString())
				|| sourceData.pendingInfectionStage().orElse(InfectionStage.NONE) != InfectionStage.AGENT) {
			return Optional.empty();
		}

		return replaceWithAgent(level, source, sourceData);
	}

	public static void cancelTransformation(LivingEntity source, String reason) {
		Objects.requireNonNull(source, "source");
		Optional<BreachEntityData> data = breachData(source);
		if (data.isEmpty() || data.get().transformationState() != TransformationState.TRANSFORMING) {
			return;
		}

		clearBreachData(source);
		TransformationPresentationNetworking.sendStop(source);
		if (SimulationBreach.CONFIG.debugLogging()) {
			SimulationBreach.LOGGER.info("Cancelled Agent transformation for {} at {}: {}",
					EntityType.getKey(source.getType()), source.blockPosition(), reason);
		}
	}

	public static Optional<BreachEntityData> breachData(Entity entity) {
		Objects.requireNonNull(entity, "entity");
		if (entity instanceof AgentEntity agentEntity) {
			return Optional.of(agentEntity.breachData());
		}
		if (entity instanceof BreachDataHolder holder) {
			return holder.simulationBreach$getBreachData();
		}
		return Optional.empty();
	}

	public static BreachEntityData cleanDataFor(LivingEntity entity) {
		Objects.requireNonNull(entity, "entity");
		Identifier entityTypeId = EntityType.getKey(entity.getType());
		return BreachEntityData.clean(originDispositionFor(entity), entityTypeId.toString());
	}

	private static Optional<AgentEntity> replaceWithAgent(ServerLevel level, LivingEntity source, BreachEntityData sourceData) {
		AgentEntity agent = ModEntities.AGENT.create(level, EntitySpawnReason.CONVERSION);
		if (agent == null) {
			SimulationBreach.LOGGER.warn("Could not create Agent replacement for {}", EntityType.getKey(source.getType()));
			return Optional.empty();
		}

		transferState(source, agent);
		BreachEntityData agentData = TransformationManager.completeTransformation(sourceData);
		agent.setBreachData(agentData);
		setBreachData(agent, agentData);

		if (!level.addFreshEntity(agent)) {
			SimulationBreach.LOGGER.warn("Could not spawn Agent replacement for {} at {}",
					EntityType.getKey(source.getType()), source.blockPosition());
			return Optional.empty();
		}

		clearBreachData(source);
		TransformationPresentationNetworking.sendStop(source);
		source.skipDropExperience();
		source.discard();

		if (SimulationBreach.CONFIG.debugLogging()) {
			SimulationBreach.LOGGER.info("Completed Agent transformation from {} at {}",
					EntityType.getKey(source.getType()), agent.blockPosition());
		}
		return Optional.of(agent);
	}

	private static void transferState(LivingEntity source, AgentEntity agent) {
		agent.snapTo(source.getX(), source.getY(), source.getZ(), source.getYRot(), source.getXRot());
		agent.setDeltaMovement(source.getDeltaMovement());
		agent.setRemainingFireTicks(source.getRemainingFireTicks());
		agent.setSilent(source.isSilent());
		agent.setInvulnerable(source.isInvulnerable());
		agent.setGlowingTag(source.isCurrentlyGlowing());

		if (source.hasCustomName()) {
			agent.setCustomName(source.getCustomName());
			agent.setCustomNameVisible(source.isCustomNameVisible());
		}

		if (source instanceof Mob sourceMob) {
			agent.setNoAi(sourceMob.isNoAi());
			if (sourceMob.isPersistenceRequired()) {
				agent.setPersistenceRequired();
			}
		}

		float healthRatio = Math.max(0.0F, Math.min(1.0F, source.getHealth() / source.getMaxHealth()));
		agent.setHealth(Math.max(1.0F, agent.getMaxHealth() * healthRatio));
	}

	private static OriginDisposition originDispositionFor(LivingEntity entity) {
		if (entity instanceof Enemy || entity instanceof AgentEntity) {
			return OriginDisposition.HOSTILE;
		}
		return OriginDisposition.PASSIVE;
	}

	private static Integer nextGeneration(BreachEntityData sourceData) {
		OptionalInt generation = sourceData.conversionGeneration();
		return generation.isPresent() ? generation.getAsInt() + 1 : 1;
	}

	private static void setBreachData(Entity entity, BreachEntityData data) {
		if (entity instanceof AgentEntity agentEntity) {
			agentEntity.setBreachData(data);
		}
		if (entity instanceof BreachDataHolder holder) {
			holder.simulationBreach$setBreachData(data);
		}
	}

	private static void clearBreachData(Entity entity) {
		if (entity instanceof BreachDataHolder holder) {
			holder.simulationBreach$clearBreachData();
		}
	}

	private static void playTransformationStartSound(ServerLevel level, LivingEntity source, SimulationBreachConfig config) {
		if (!config.enablePlaceholderCreeperTransformSound()) {
			return;
		}
		level.playSound(null, source.blockPosition(), SoundEvents.CREEPER_PRIMED, SoundSource.HOSTILE, 1.0F, 0.85F);
	}

	private static boolean isAgentCapReached(ServerLevel level, LivingEntity source, SimulationBreachConfig config) {
		if (config.maxAgentsPerChunk() <= 0) {
			return true;
		}

		int agentsInChunk = 0;
		for (Entity entity : level.getAllEntities()) {
			if (entity instanceof AgentEntity && entity.chunkPosition().equals(source.chunkPosition())) {
				agentsInChunk++;
				if (agentsInChunk >= config.maxAgentsPerChunk()) {
					return true;
				}
			}
		}
		return false;
	}

	private static void logDebug(SimulationBreachConfig config, String message, Object... arguments) {
		if (config.debugLogging()) {
			SimulationBreach.LOGGER.info(message, arguments);
		}
	}
}
