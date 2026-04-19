package com.ronhelwig.simulationbreach.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.ronhelwig.simulationbreach.SimulationBreach;
import com.ronhelwig.simulationbreach.conversion.ConversionManager;
import com.ronhelwig.simulationbreach.entity.AgentEntity;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;

public final class ModCommands {
	private static final int DEFAULT_TRANSFORM_RADIUS = 16;
	private static final int MAX_TRANSFORM_RADIUS = 128;

	private ModCommands() {
	}

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, selection) -> dispatcher.register(
				Commands.literal("simulationbreach")
						.requires(source -> Commands.LEVEL_GAMEMASTERS.check(source.permissions()))
						.then(Commands.literal("transform_nearest")
								.executes(context -> transformNearest(context, DEFAULT_TRANSFORM_RADIUS))
								.then(Commands.argument("radius", IntegerArgumentType.integer(1, MAX_TRANSFORM_RADIUS))
										.executes(context -> transformNearest(
												context,
												IntegerArgumentType.getInteger(context, "radius")
										))))
		));
	}

	private static int transformNearest(CommandContext<CommandSourceStack> context, int radius) {
		CommandSourceStack source = context.getSource();
		ServerLevel level = source.getLevel();
		Vec3 origin = source.getPosition();
		AABB searchBox = AABB.ofSize(origin, radius * 2.0D, radius * 2.0D, radius * 2.0D);
		List<Entity> candidates = level.getEntities(source.getEntity(), searchBox, ModCommands::isTransformCandidate);
		candidates.sort(Comparator.comparingDouble(entity -> entity.distanceToSqr(origin)));

		int considered = 0;
		for (Entity entity : candidates) {
			considered++;
			LivingEntity target = (LivingEntity) entity;
			if (ConversionManager.beginAgentTransformation(
					level,
					target,
					SimulationBreach.CONFIG,
					source.getEntity(),
					"debug_command"
			)) {
				source.sendSuccess(
						() -> Component.literal("Started Agent transformation for "
								+ EntityType.getKey(target.getType())
								+ " at "
								+ target.blockPosition()),
						true
				);
				return Command.SINGLE_SUCCESS;
			}
		}

		source.sendFailure(Component.literal("No transformable mob found within " + radius
				+ " blocks; considered " + considered + " candidate(s)."));
		return 0;
	}

	private static boolean isTransformCandidate(Entity entity) {
		return entity instanceof Mob
				&& entity instanceof LivingEntity
				&& !(entity instanceof AgentEntity)
				&& !(entity instanceof Player)
				&& entity.isAlive()
				&& !entity.isRemoved();
	}
}
