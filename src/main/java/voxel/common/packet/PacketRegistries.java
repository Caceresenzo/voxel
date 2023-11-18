package voxel.common.packet;

import static voxel.common.packet.ConnectionState.HANDSHAKE;
import static voxel.common.packet.ConnectionState.LOGIN;
import static voxel.common.packet.ConnectionState.PLAY;
import static voxel.common.packet.ConnectionState.STATUS;

import voxel.common.packet.clientbound.login.LoginSuccessPacket;
import voxel.common.packet.clientbound.other.PongPacket;
import voxel.common.packet.clientbound.play.PlayerInfoUpdatePacket;
import voxel.common.packet.clientbound.play.UpdateEntityPositionAndRotationPacket;
import voxel.common.packet.clientbound.status.StatusResponsePacket;
import voxel.common.packet.serverbound.handshake.HandshakePacket;
import voxel.common.packet.serverbound.login.LoginAcknowledgedPacket;
import voxel.common.packet.serverbound.login.LoginStartPacket;
import voxel.common.packet.serverbound.other.PingPacket;
import voxel.common.packet.serverbound.play.SetPlayerPositionAndRotationPacket;
import voxel.common.packet.serverbound.status.StatusRequestPacket;

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

		SERVER_BOUND.register(PLAY, 0x17, SetPlayerPositionAndRotationPacket.class, SetPlayerPositionAndRotationPacket.SERIALIZER);
		CLIENT_BOUND.register(PLAY, 0x2d, UpdateEntityPositionAndRotationPacket.class, UpdateEntityPositionAndRotationPacket.SERIALIZER);
		CLIENT_BOUND.register(PLAY, 0x3c, PlayerInfoUpdatePacket.class, PlayerInfoUpdatePacket.SERIALIZER);
	}

}