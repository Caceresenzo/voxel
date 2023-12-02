package voxel.networking.packet.serverbound;

import voxel.networking.Remote;
import voxel.networking.packet.Packet;
import voxel.networking.packet.serverbound.handshake.HandshakePacket;
import voxel.networking.packet.serverbound.login.LoginAcknowledgedPacket;
import voxel.networking.packet.serverbound.login.LoginStartPacket;
import voxel.networking.packet.serverbound.other.PingPacket;
import voxel.networking.packet.serverbound.play.SetPlayerPositionAndRotationPacket;
import voxel.networking.packet.serverbound.status.StatusRequestPacket;

public interface ServerBoundPacketHandler<T extends Remote> {

	void onHandshake(T remote, HandshakePacket packet);

	void onPing(T remote, PingPacket packet);

	void onStatusRequest(T remote, StatusRequestPacket packet);

	void onLogin(T remote, LoginStartPacket packet);

	void onLoginAcknowledged(T remote, LoginAcknowledgedPacket packet);

	void onSetPlayerPositionAndRotation(T remote, SetPlayerPositionAndRotationPacket packet);

	public static <T extends Remote> boolean dispatch(ServerBoundPacketHandler<T> handler, T remote, Packet packet) {
		if (packet instanceof HandshakePacket packet_) {
			handler.onHandshake(remote, packet_);
			return true;
		}

		if (packet instanceof PingPacket packet_) {
			handler.onPing(remote, packet_);
			return true;
		}

		if (packet instanceof StatusRequestPacket packet_) {
			handler.onStatusRequest(remote, packet_);
			return true;
		}

		if (packet instanceof LoginStartPacket packet_) {
			handler.onLogin(remote, packet_);
			return true;
		}

		if (packet instanceof LoginAcknowledgedPacket packet_) {
			handler.onLoginAcknowledged(remote, packet_);
			return true;
		}

		if (packet instanceof SetPlayerPositionAndRotationPacket packet_) {
			handler.onSetPlayerPositionAndRotation(remote, packet_);
			return true;
		}

		return false;
	}

}