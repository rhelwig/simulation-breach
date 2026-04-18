package com.ronhelwig.simulationbreach.client.mixin;

import com.ronhelwig.simulationbreach.client.presentation.TransformationPresentationTracker;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
	@Inject(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V", at = @At("TAIL"))
	private void simulationBreach$extractTransformationShake(
			LivingEntity entity,
			LivingEntityRenderState renderState,
			float tickProgress,
			CallbackInfo callbackInfo
	) {
		if (TransformationPresentationTracker.isTransforming(entity)) {
			renderState.isFullyFrozen = true;
		}
	}
}
