package voxel.common.packet.clientbound.play;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import voxel.common.data.BufferReader;
import voxel.common.data.BufferWritter;
import voxel.common.packet.Packet;
import voxel.common.packet.PacketSerializer;
import voxel.common.packet.clientbound.play.PlayerInfoUpdatePacket.PlayerData;

@SuppressWarnings("unused")
public record PlayerInfoUpdatePacket(
	byte actionMask,
	List<PlayerData> players
) implements Packet {

	public PlayerInfoUpdatePacket {
		if (CollectionUtils.isEmpty(players)) {
			throw new IllegalArgumentException("players cannot be null or empty");
		}
	}

	@Getter
	@RequiredArgsConstructor
	@Accessors(fluent = true)
	public enum Action {

		ADD_PLAYER(1 << 0),
		UPDATE_LATENCY(1 << 4);

		private final int bit;

		public boolean test(byte mask) {
			return (mask & bit) != 0;
		}

	}

	public record PlayerData(
		UUID uuid,
		String login,
		Integer latency
	) {

		public PlayerData {
			Objects.requireNonNull("uuid", "uuid cannot be null");
		}

	}

	public static final PacketSerializer<PlayerInfoUpdatePacket> SERIALIZER = new PacketSerializer<PlayerInfoUpdatePacket>() {

		@Override
		public void serialize(PlayerInfoUpdatePacket packet, BufferWritter output) throws IOException {
			output.writeByte(packet.actionMask);
			output.writeInt(packet.players.size());

			for (final var player : packet.players) {
				output.writeUUID(player.uuid);
				
				if (Action.ADD_PLAYER.test(packet.actionMask)) {
					output.writeAsciiString(player.login);
				}

				if (Action.UPDATE_LATENCY.test(packet.actionMask)) {
					output.writeInt(player.latency);
				}
			}
		}

		@Override
		public PlayerInfoUpdatePacket deserialize(BufferReader input) throws IOException {
			final var actionMask = input.readByte();
			final var count = input.readInt();

			final var players = new ArrayList<PlayerData>(count);

			for (var index = 0; index < count; ++index) {
				final var uuid = input.readUUID();

				String login = null;
				if (Action.ADD_PLAYER.test(actionMask)) {
					login = input.readAsciiString();
				}

				Integer latency = null;
				if (Action.UPDATE_LATENCY.test(actionMask)) {
					latency = input.readInt();
				}

				players.add(new PlayerData(uuid, login, latency));
			}

			return new PlayerInfoUpdatePacket(actionMask, players);
		}

	};

}