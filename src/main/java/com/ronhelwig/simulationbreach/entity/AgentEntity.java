package com.ronhelwig.simulationbreach.entity;

import com.ronhelwig.simulationbreach.SimulationBreach;
import com.ronhelwig.simulationbreach.config.SimulationBreachConfig;
import com.ronhelwig.simulationbreach.conversion.ConversionManager;
import com.ronhelwig.simulationbreach.damage.ModDamageTypes;
import com.ronhelwig.simulationbreach.identity.BreachEntityData;
import com.ronhelwig.simulationbreach.identity.BreachEntityDataStorage;
import com.ronhelwig.simulationbreach.identity.InfectionStage;
import com.ronhelwig.simulationbreach.identity.OriginDisposition;
import com.ronhelwig.simulationbreach.sound.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class AgentEntity extends Monster {
	private static final Random CONVERSION_RANDOM = new Random();
	private static final String TRACKED_PLAYER_UUID_KEY = "TrackedPlayerUuid";

	private BreachEntityData breachData = defaultBreachData();
	private long nextConversionSweepGameTime;
	private UUID trackedPlayerUuid;

	public AgentEntity(EntityType<? extends AgentEntity> entityType, Level level) {
		super(entityType, level);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 24.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.30D)
				.add(Attributes.ATTACK_DAMAGE, 4.0D)
				.add(Attributes.FOLLOW_RANGE, 40.0D);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.05D, false));
		this.goalSelector.addGoal(7, new RandomStrollGoal(this, 0.8D));
		this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	@Override
	protected void customServerAiStep(ServerLevel level) {
		super.customServerAiStep(level);
		updateTrackedPlayer();
		if (revertAfterTrackedPlayerDeath(level)) {
			return;
		}
		sweepNearbyConversionTargets(level);
	}

	@Override
	public boolean doHurtTarget(ServerLevel level, Entity target) {
		if (!(target instanceof Player)) {
			return super.doHurtTarget(level, target);
		}

		trackedPlayerUuid = target.getUUID();
		float damage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE);
		DamageSource damageSource = damageSources().source(ModDamageTypes.AGENT_ERADICATION, this);
		Vec3 targetMovement = target.getDeltaMovement();
		boolean damaged = target.hurtServer(level, damageSource, damage);
		if (damaged) {
			causeExtraKnockback(target, getKnockback(target, damageSource), targetMovement);
			setLastHurtMob(target);
			playAttackSound();
		}
		postPiercingAttack();
		return damaged;
	}

	@Override
	protected int getBaseExperienceReward(ServerLevel level) {
		return SimulationBreach.CONFIG.agentExperienceReward();
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return ModSounds.AGENT_VOICE;
	}

	public BreachEntityData breachData() {
		return breachData;
	}

	public void setBreachData(BreachEntityData breachData) {
		this.breachData = breachData;
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
		super.addAdditionalSaveData(output);
		BreachEntityDataStorage.write(output, breachData);
		if (trackedPlayerUuid != null) {
			output.putString(TRACKED_PLAYER_UUID_KEY, trackedPlayerUuid.toString());
		}
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		super.readAdditionalSaveData(input);
		this.breachData = BreachEntityDataStorage.read(input)
				.orElseGet(AgentEntity::defaultBreachData);
		this.trackedPlayerUuid = input.getString(TRACKED_PLAYER_UUID_KEY)
				.flatMap(AgentEntity::parseUuid)
				.orElse(null);
	}

	private void updateTrackedPlayer() {
		if (getTarget() instanceof Player player) {
			trackedPlayerUuid = player.getUUID();
		}
	}

	private boolean revertAfterTrackedPlayerDeath(ServerLevel level) {
		if (trackedPlayerUuid == null) {
			return false;
		}

		ServerPlayer trackedPlayer = level.getServer().getPlayerList().getPlayer(trackedPlayerUuid);
		if (trackedPlayer == null || !trackedPlayer.isDeadOrDying()) {
			return false;
		}
		if (hasNearbySurvivingPlayer(level, trackedPlayer)) {
			return false;
		}

		return ConversionManager.replaceAgentWithOriginalMob(level, this);
	}

	private boolean hasNearbySurvivingPlayer(ServerLevel level, ServerPlayer trackedPlayer) {
		double radius = SimulationBreach.CONFIG.agentDespawnNearbyPlayerRadius();
		double radiusSqr = radius * radius;
		for (ServerPlayer player : level.players()) {
			if (player.getUUID().equals(trackedPlayer.getUUID())
					|| !player.isAlive()
					|| player.isDeadOrDying()
					|| player.isSpectator()) {
				continue;
			}
			if (distanceToSqr(player) <= radiusSqr || trackedPlayer.distanceToSqr(player) <= radiusSqr) {
				return true;
			}
		}
		return false;
	}

	private static java.util.Optional<UUID> parseUuid(String value) {
		try {
			return java.util.Optional.of(UUID.fromString(value));
		} catch (IllegalArgumentException exception) {
			return java.util.Optional.empty();
		}
	}

	private void sweepNearbyConversionTargets(ServerLevel level) {
		SimulationBreachConfig config = SimulationBreach.CONFIG;
		long gameTime = level.getGameTime();
		if (gameTime < nextConversionSweepGameTime) {
			return;
		}

		nextConversionSweepGameTime = gameTime + config.agentConversionCooldownTicks();
		double radius = Math.max(1.0D, config.agentConversionDetourRadius());
		List<Entity> nearbyEntities = level.getEntities(
				this,
				getBoundingBox().inflate(radius),
				AgentEntity::isSweepCandidate
		);
		nearbyEntities.sort(Comparator.comparingDouble(this::distanceToSqr));

		int attempts = 0;
		int started = 0;
		for (Entity entity : nearbyEntities) {
			if (entity instanceof LivingEntity target) {
				attempts++;
				if (ConversionManager.attemptAgentConversion(level, this, target, config, CONVERSION_RANDOM)) {
					started++;
				}
			}
		}

		if (config.debugLogging() && attempts > 0) {
			SimulationBreach.LOGGER.info(
					"Agent conversion sweep: agent={}, position={}, attempts={}, started={}",
					getUUID(),
					blockPosition(),
					attempts,
					started
			);
		}
	}

	private static boolean isSweepCandidate(Entity entity) {
		return entity instanceof Mob
				&& !(entity instanceof AgentEntity)
				&& !(entity instanceof Player)
				&& entity.isAlive()
				&& !entity.isRemoved();
	}

	private static BreachEntityData defaultBreachData() {
		return BreachEntityData.builder(OriginDisposition.HOSTILE, ModEntities.AGENT_ID.toString())
				.infectionStage(InfectionStage.AGENT)
				.build();
	}
}
