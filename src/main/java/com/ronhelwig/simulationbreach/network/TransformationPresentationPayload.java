package com.ronhelwig.simulationbreach.network;

import com.ronhelwig.simulationbreach.SimulationBreach;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record TransformationPresentationPayload(
		int entityId,
		int durationTicks,
		boolean active,
		boolean playAgentSound,
		boolean playPlaceholderSound
) implements CustomPacketPayload {
	public static final Type<TransformationPresentationPayload> TYPE = new Type<>(
			Identifier.fromNamespaceAndPath(SimulationBreach.MOD_ID, "transformation_presentation")
	);
	public static final StreamCodec<RegistryFriendlyByteBuf, TransformationPresentationPayload> CODEC = StreamCodec.ofMember(
			TransformationPresentationPayload::write,
			TransformationPresentationPayload::read
	);

	private static TransformationPresentationPayload read(RegistryFriendlyByteBuf buffer) {
		return new TransformationPresentationPayload(
				buffer.readInt(),
				buffer.readInt(),
				buffer.readBoolean(),
				buffer.readBoolean(),
				buffer.readBoolean()
		);
	}

	private void write(RegistryFriendlyByteBuf buffer) {
		buffer.writeInt(entityId);
		buffer.writeInt(durationTicks);
		buffer.writeBoolean(active);
		buffer.writeBoolean(playAgentSound);
		buffer.writeBoolean(playPlaceholderSound);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
