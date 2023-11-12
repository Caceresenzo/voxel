package voxel.common.packet.serverbound.login;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.UUID;

import voxel.common.packet.Packet;
import voxel.common.packet.PacketDeserializer;
import voxel.common.packet.PacketIdentifier;
import voxel.common.packet.PacketSerializer;

public record LoginPacket(
	UUID uuid,
	String login
) implements Packet<LoginPacket> {

	public static PacketIdentifier<LoginPacket> IDENTIFIER = new PacketIdentifier<>(
		0x00,
		"login",
		new Serializer(),
		new Deserializer()
	);

	@Override
	public PacketIdentifier<LoginPacket> getPacketIdentifier() {
		return IDENTIFIER;
	}

	public static class Serializer implements PacketSerializer<LoginPacket> {

		@Override
		public void serialize(LoginPacket packet, DataOutput dataOutput) throws IOException {
			dataOutput.writeLong(packet.uuid.getMostSignificantBits());
			dataOutput.writeLong(packet.uuid.getLeastSignificantBits());
			dataOutput.writeUTF(packet.login);
		}

	}

	public static class Deserializer implements PacketDeserializer<LoginPacket> {

		@Override
		public LoginPacket deserialize(DataInput dataInput) throws IOException {
			final var uuid = new UUID(dataInput.readLong(), dataInput.readLong());
			final var name = dataInput.readUTF();

			return new LoginPacket(uuid, name);
		}

	}

}