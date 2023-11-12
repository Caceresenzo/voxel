package voxel.common.packet.serverbound.login;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.UUID;

import voxel.common.packet.Packet;
import voxel.common.packet.PacketSerializer;

public record LoginStartPacket(
	UUID uuid,
	String login
) implements Packet {

	public static final PacketSerializer<LoginStartPacket> SERIALIZER = new PacketSerializer<LoginStartPacket>() {

		@Override
		public void serialize(LoginStartPacket packet, DataOutput dataOutput) throws IOException {
			dataOutput.writeLong(packet.uuid.getMostSignificantBits());
			dataOutput.writeLong(packet.uuid.getLeastSignificantBits());
			dataOutput.writeUTF(packet.login);
		}

		@Override
		public LoginStartPacket deserialize(DataInput dataInput) throws IOException {
			final var uuid = new UUID(dataInput.readLong(), dataInput.readLong());
			final var name = dataInput.readUTF();

			return new LoginStartPacket(uuid, name);
		}

	};

}