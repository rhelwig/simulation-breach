package com.ronhelwig.simulationbreach.mixin;

import com.ronhelwig.simulationbreach.SimulationBreach;
import com.ronhelwig.simulationbreach.conversion.ConversionManager;
import com.ronhelwig.simulationbreach.identity.BreachDataHolder;
import com.ronhelwig.simulationbreach.identity.BreachEntityData;
import com.ronhelwig.simulationbreach.identity.BreachEntityDataStorage;
import com.ronhelwig.simulationbreach.identity.TransformationState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityBreachDataMixin implements BreachDataHolder {
	@Unique
	private BreachEntityData simulationBreach$breachData;

	@Override
	public Optional<BreachEntityData> simulationBreach$getBreachData() {
		return Optional.ofNullable(simulationBreach$breachData);
	}

	@Override
	public void simulationBreach$setBreachData(BreachEntityData data) {
		this.simulationBreach$breachData = data;
	}

	@Override
	public void simulationBreach$clearBreachData() {
		this.simulationBreach$breachData = null;
	}

	@Inject(method = "saveWithoutId", at = @At("TAIL"))
	private void simulationBreach$saveBreachData(ValueOutput output, CallbackInfo callbackInfo) {
		if (simulationBreach$breachData != null) {
			BreachEntityDataStorage.write(output, simulationBreach$breachData);
		}
	}

	@Inject(method = "load", at = @At("TAIL"))
	private void simulationBreach$loadBreachData(ValueInput input, CallbackInfo callbackInfo) {
		try {
			this.simulationBreach$breachData = BreachEntityDataStorage.read(input).orElse(null);
		} catch (IllegalArgumentException exception) {
			this.simulationBreach$breachData = null;
			SimulationBreach.LOGGER.warn("Discarded invalid breach data while loading entity", exception);
		}
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void simulationBreach$tickTransformation(CallbackInfo callbackInfo) {
		if (simulationBreach$breachData == null
				|| simulationBreach$breachData.transformationState() != TransformationState.TRANSFORMING) {
			return;
		}

		Entity entity = (Entity) (Object) this;
		if (entity.level() instanceof ServerLevel level && entity instanceof LivingEntity livingEntity) {
			if (!livingEntity.isAlive() || livingEntity.isRemoved()) {
				ConversionManager.cancelTransformation(livingEntity, "source_died_before_completion");
				return;
			}
			ConversionManager.completeTransformationIfReady(level, livingEntity);
		}
	}

	@Unique
	private Entity simulationBreach$entity() {
		return (Entity) (Object) this;
	}
}
