package voxel.common.packet.clientbound.login;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.UUID;

import voxel.common.packet.Packet;
import voxel.common.packet.PacketDeserializer;
import voxel.common.packet.PacketIdentifier;
import voxel.common.packet.PacketSerializer;

public record LoginSuccessPacket(
	UUID uuid,
	String name
) implements Packet<LoginSuccessPacket> {

	public static PacketIdentifier<LoginSuccessPacket> IDENTIFIER = new PacketIdentifier<>(
		0x02,
		"login success",
		new Serializer(),
		new Deserializer()
	);

	@Override
	public PacketIdentifier<LoginSuccessPacket> getPacketIdentifier() {
		return IDENTIFIER;
	}

	public static class Serializer implements PacketSerializer<LoginSuccessPacket> {

		@Override
		public void serialize(LoginSuccessPacket packet, DataOutput dataOutput) throws IOException {
			dataOutput.writeLong(packet.uuid.getMostSignificantBits());
			dataOutput.writeLong(packet.uuid.getLeastSignificantBits());
			dataOutput.writeUTF(packet.name);
		}

	}

	public static class Deserializer implements PacketDeserializer<LoginSuccessPacket> {

		@Override
		public LoginSuccessPacket deserialize(DataInput dataInput) throws IOException {
			final var uuid = new UUID(dataInput.readLong(), dataInput.readLong());
			final var name = dataInput.readUTF();

			return new LoginSuccessPacket(uuid, name);
		}

	}

}