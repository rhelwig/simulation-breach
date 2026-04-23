package com.ronhelwig.simulationbreach.mixin;

import com.ronhelwig.simulationbreach.outbreak.OutbreakManager;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemPlacementMixin {
	@Inject(method = "place", at = @At("RETURN"))
	private void simulationBreach$recordPlacedBlock(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> callbackInfo) {
		InteractionResult result = callbackInfo.getReturnValue();
		if (result == null || !result.consumesAction()) {
			return;
		}

		Level level = context.getLevel();
		Player player = context.getPlayer();
		if (player != null) {
			OutbreakManager.recordPlayerBlockChange(level, player, context.getClickedPos(), "place");
		}
	}
}
