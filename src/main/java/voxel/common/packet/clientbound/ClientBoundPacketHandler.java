package voxel.common.packet.clientbound;

import voxel.common.packet.Packet;
import voxel.common.packet.Remote;
import voxel.common.packet.clientbound.login.LoginSuccessPacket;
import voxel.common.packet.clientbound.other.PongPacket;
import voxel.common.packet.clientbound.status.StatusResponsePacket;

public interface ClientBoundPacketHandler<T extends Remote> {

	void onPong(T client, PongPacket packet);

	void onStatusResponse(T client, StatusResponsePacket packet);

	void onLoginSuccess(T client, LoginSuccessPacket packet);

	public static <T extends Remote> boolean dispatch(ClientBoundPacketHandler<T> handler, T remote, Packet packet) {
		if (packet instanceof PongPacket packet_) {
			handler.onPong(remote, packet_);
			return true;
		}

		if (packet instanceof StatusResponsePacket packet_) {
			handler.onStatusResponse(remote, packet_);
			return true;
		}

		if (packet instanceof LoginSuccessPacket packet_) {
			handler.onLoginSuccess(remote, packet_);
			return true;
		}

		return false;
	}

}