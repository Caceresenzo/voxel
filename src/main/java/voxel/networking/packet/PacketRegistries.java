package voxel.networking.packet;

import static voxel.networking.packet.ConnectionState.HANDSHAKE;
import static voxel.networking.packet.ConnectionState.LOGIN;
import static voxel.networking.packet.ConnectionState.PLAY;
import static voxel.networking.packet.ConnectionState.STATUS;

import voxel.networking.packet.clientbound.login.LoginSuccessPacket;
import voxel.networking.packet.clientbound.other.PongPacket;
import voxel.networking.packet.clientbound.play.BlockUpdatePacket;
import voxel.networking.packet.clientbound.play.ChunkDataPacket;
import voxel.networking.packet.clientbound.play.LoginPacket;
import voxel.networking.packet.clientbound.play.PlayerInfoUpdatePacket;
import voxel.networking.packet.clientbound.play.UpdateEntityPositionAndRotationPacket;
import voxel.networking.packet.clientbound.status.StatusResponsePacket;
import voxel.networking.packet.serverbound.handshake.HandshakePacket;
import voxel.networking.packet.serverbound.login.LoginAcknowledgedPacket;
import voxel.networking.packet.serverbound.login.LoginStartPacket;
import voxel.networking.packet.serverbound.other.PingPacket;
import voxel.networking.packet.serverbound.play.PlayerActionPacket;
import voxel.networking.packet.serverbound.play.SetPlayerPositionAndRotationPacket;
import voxel.networking.packet.serverbound.play.UseItemOnPacket;
import voxel.networking.packet.serverbound.status.StatusRequestPacket;

public class PacketRegistries {

	public static final PacketRegistry SERVER_BOUND = new PacketRegistry();
	public static final PacketRegistry CLIENT_BOUND = new PacketRegistry();

	static {
		SERVER_BOUND.register(HANDSHAKE, 0x00, HandshakePacket.class, HandshakePacket.SERIALIZER);

		SERVER_BOUND.register(STATUS, 0x00, StatusRequestPacket.class, StatusRequestPacket.SERIALIZER);
		CLIENT_BOUND.register(STATUS, 0x00, StatusResponsePacket.class, StatusResponsePacket.SERIALIZER);

		SERVER_BOUND.register(STATUS, 0x01, PingPacket.class, PingPacket.SERIALIZER);
		CLIENT_BOUND.register(STATUS, 0x01, PongPacket.class, PongPacket.SERIALIZER);

		SERVER_BOUND.register(LOGIN, 0x00, LoginStartPacket.class, LoginStartPacket.SERIALIZER);
		CLIENT_BOUND.register(LOGIN, 0x02, LoginSuccessPacket.class, LoginSuccessPacket.SERIALIZER);
		SERVER_BOUND.register(LOGIN, 0x03, LoginAcknowledgedPacket.class, LoginAcknowledgedPacket.SERIALIZER);

		CLIENT_BOUND.register(PLAY, 0x09, BlockUpdatePacket.class, BlockUpdatePacket.SERIALIZER);
		SERVER_BOUND.register(PLAY, 0x17, SetPlayerPositionAndRotationPacket.class, SetPlayerPositionAndRotationPacket.SERIALIZER);
		CLIENT_BOUND.register(PLAY, 0x2d, UpdateEntityPositionAndRotationPacket.class, UpdateEntityPositionAndRotationPacket.SERIALIZER);
		SERVER_BOUND.register(PLAY, 0x21, PlayerActionPacket.class, PlayerActionPacket.SERIALIZER);
		CLIENT_BOUND.register(PLAY, 0x25, ChunkDataPacket.class, ChunkDataPacket.SERIALIZER);
		CLIENT_BOUND.register(PLAY, 0x29, LoginPacket.class, LoginPacket.SERIALIZER);
		SERVER_BOUND.register(PLAY, 0x35, UseItemOnPacket.class, UseItemOnPacket.SERIALIZER);
		CLIENT_BOUND.register(PLAY, 0x3c, PlayerInfoUpdatePacket.class, PlayerInfoUpdatePacket.SERIALIZER);
	}

}