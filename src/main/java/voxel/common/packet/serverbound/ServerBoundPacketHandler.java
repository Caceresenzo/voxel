package voxel.common.packet.serverbound;

import voxel.common.packet.Packet;
import voxel.common.packet.Remote;
import voxel.common.packet.serverbound.handshake.HandshakePacket;
import voxel.common.packet.serverbound.login.LoginAcknowledgedPacket;
import voxel.common.packet.serverbound.login.LoginStartPacket;
import voxel.common.packet.serverbound.other.PingPacket;
import voxel.common.packet.serverbound.status.StatusRequestPacket;

public interface ServerBoundPacketHandler<T extends Remote> {

	void onHandshake(T remote, HandshakePacket packet);

	void onPing(T remote, PingPacket packet);

	void onStatusRequest(T remote, StatusRequestPacket packet);

	void onLogin(T remote, LoginStartPacket packet);

	void onLoginAcknowledged(T remote, LoginAcknowledgedPacket packet);

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

		return false;
	}

}