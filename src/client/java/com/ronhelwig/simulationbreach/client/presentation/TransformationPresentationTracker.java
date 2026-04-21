package com.ronhelwig.simulationbreach.client.presentation;

import com.ronhelwig.simulationbreach.network.TransformationPresentationPayload;
import com.ronhelwig.simulationbreach.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class TransformationPresentationTracker {
	private static final Map<Integer, Presentation> PRESENTATIONS = new HashMap<>();

	private TransformationPresentationTracker() {
	}

	public static void handle(TransformationPresentationPayload payload) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.level == null) {
			return;
		}

		if (!payload.active()) {
			PRESENTATIONS.remove(payload.entityId());
			return;
		}

		long durationTicks = Math.max(1, payload.durationTicks());
		PRESENTATIONS.put(payload.entityId(), new Presentation(minecraft.level.getGameTime(), durationTicks));
		playStartSound(minecraft, payload);
	}

	public static boolean isTransforming(Entity entity) {
		return presentation(entity) != null;
	}

	public static void applyMotion(Entity entity, LivingEntityRenderState renderState, float tickProgress) {
		Presentation presentation = presentation(entity);
		if (presentation == null) {
			return;
		}

		renderState.isFullyFrozen = true;

		ClientLevel level = Minecraft.getInstance().level;
		if (level == null) {
			return;
		}

		double elapsedTicks = Math.max(0.0D, level.getGameTime() - presentation.startedGameTime() + tickProgress);
		double progress = Math.min(1.0D, elapsedTicks / presentation.durationTicks());
		double intensity = 0.35D + 0.65D * easeIn(progress);
		double time = elapsedTicks + entity.getId() * 0.37D;

		double snapX = Math.signum(Math.sin(time * 2.9D));
		double snapZ = Math.signum(Math.cos(time * 3.4D + entity.getId()));
		double xShake = (
				Math.sin(time * 3.1D) * 0.11D
						+ Math.sin(time * 8.7D + entity.getId()) * 0.05D
						+ snapX * 0.055D
		) * intensity;
		double zShake = (
				Math.cos(time * 2.6D) * 0.10D
						+ Math.sin(time * 9.3D + entity.getId() * 0.5D) * 0.045D
						+ snapZ * 0.05D
		) * intensity;
		double yJolt = Math.abs(Math.sin(time * 5.2D)) * 0.035D * intensity;

		renderState.x += xShake;
		renderState.y += yJolt;
		renderState.z += zShake;

		float yawJerk = (float) ((
				Math.sin(time * 2.7D) * 28.0D
						+ Math.sin(time * 7.9D + entity.getId()) * 12.0D
						+ snapX * 18.0D
		) * intensity);
		float pitchJerk = (float) ((
				Math.cos(time * 3.6D + entity.getId()) * 14.0D
						+ snapZ * 8.0D
		) * intensity);
		float bodyJerk = (float) ((
				Math.sin(time * 4.8D + entity.getId() * 0.2D) * 24.0D
						- snapZ * 14.0D
		) * intensity);

		renderState.bodyRot += bodyJerk;
		renderState.yRot += yawJerk;
		renderState.xRot += pitchJerk;
	}

	public static void clear() {
		PRESENTATIONS.clear();
	}

	private static Presentation presentation(Entity entity) {
		ClientLevel level = Minecraft.getInstance().level;
		if (level == null) {
			return null;
		}

		long currentGameTime = level.getGameTime();
		removeExpired(currentGameTime);
		Presentation presentation = PRESENTATIONS.get(entity.getId());
		if (presentation == null || !presentation.activeAt(currentGameTime)) {
			return null;
		}
		return presentation;
	}

	private static void removeExpired(long currentGameTime) {
		Iterator<Map.Entry<Integer, Presentation>> iterator = PRESENTATIONS.entrySet().iterator();
		while (iterator.hasNext()) {
			if (!iterator.next().getValue().activeAt(currentGameTime)) {
				iterator.remove();
			}
		}
	}

	private static double easeIn(double progress) {
		return progress * progress;
	}

	private static void playStartSound(Minecraft minecraft, TransformationPresentationPayload payload) {
		if (!payload.playAgentSound() && !payload.playPlaceholderSound()) {
			return;
		}

		Entity entity = minecraft.level.getEntity(payload.entityId());
		if (entity == null) {
			return;
		}

		if (payload.playAgentSound()) {
			minecraft.getSoundManager().play(new SimpleSoundInstance(
					ModSounds.AGENT_TRANSFORM,
					SoundSource.HOSTILE,
					1.0F,
					1.0F,
					SoundInstance.createUnseededRandom(),
					entity.getX(),
					entity.getY(),
					entity.getZ()
			));
			return;
		}
		if (payload.playPlaceholderSound()) {
			minecraft.getSoundManager().play(new SimpleSoundInstance(
					SoundEvents.CREEPER_PRIMED,
					SoundSource.HOSTILE,
					1.0F,
					0.85F,
					SoundInstance.createUnseededRandom(),
					entity.getX(),
					entity.getY(),
					entity.getZ()
			));
		}
	}

	private record Presentation(long startedGameTime, long durationTicks) {
		private boolean activeAt(long gameTime) {
			return gameTime < startedGameTime + durationTicks;
		}
	}
}
