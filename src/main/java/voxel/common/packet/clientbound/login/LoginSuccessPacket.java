package voxel.common.packet.clientbound.login;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.UUID;

import voxel.common.packet.Packet;
import voxel.common.packet.PacketSerializer;

public record LoginSuccessPacket(
	UUID uuid,
	String name
) implements Packet {

	public static final PacketSerializer<LoginSuccessPacket> SERIALIZER = new PacketSerializer<LoginSuccessPacket>() {

		@Override
		public void serialize(LoginSuccessPacket packet, DataOutput dataOutput) throws IOException {
			dataOutput.writeLong(packet.uuid.getMostSignificantBits());
			dataOutput.writeLong(packet.uuid.getLeastSignificantBits());
			dataOutput.writeUTF(packet.name);
		}

		@Override
		public LoginSuccessPacket deserialize(DataInput dataInput) throws IOException {
			final var uuid = new UUID(dataInput.readLong(), dataInput.readLong());
			final var name = dataInput.readUTF();

			return new LoginSuccessPacket(uuid, name);
		}

	};

}