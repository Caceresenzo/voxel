package voxel.networking.packet.clientbound.play;

import java.io.IOException;

import voxel.networking.packet.Packet;
import voxel.networking.packet.PacketSerializer;
import voxel.util.data.BufferReader;
import voxel.util.data.BufferWritter;

public record LoginPacket(
	String dimensionName
) implements Packet {

	public static final PacketSerializer<LoginPacket> SERIALIZER = new PacketSerializer<LoginPacket>() {

		@Override
		public void serialize(LoginPacket packet, BufferWritter output) throws IOException {
			output.writeAsciiString(packet.dimensionName);
		}

		@Override
		public LoginPacket deserialize(BufferReader input) throws IOException {
			final var dimensionName = input.readAsciiString();

			return new LoginPacket(dimensionName);
		}

	};

}