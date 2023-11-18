package voxel.common.packet.clientbound;

import voxel.common.packet.Packet;
import voxel.common.packet.Remote;
import voxel.common.packet.clientbound.login.LoginSuccessPacket;
import voxel.common.packet.clientbound.other.PongPacket;
import voxel.common.packet.clientbound.play.PlayerInfoUpdatePacket;
import voxel.common.packet.clientbound.play.UpdateEntityPositionAndRotationPacket;
import voxel.common.packet.clientbound.status.StatusResponsePacket;

public interface ClientBoundPacketHandler<T extends Remote> {

	void onPong(T remote, PongPacket packet);

	void onStatusResponse(T remote, StatusResponsePacket packet);

	void onLoginSuccess(T remote, LoginSuccessPacket packet);

	void onPlayerInfoUpdate(T remote, PlayerInfoUpdatePacket packet);

	void onUpdateEntityPositionAndRotation(T remote, UpdateEntityPositionAndRotationPacket packet);

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

		if (packet instanceof PlayerInfoUpdatePacket packet_) {
			handler.onPlayerInfoUpdate(remote, packet_);
			return true;
		}

		if (packet instanceof UpdateEntityPositionAndRotationPacket packet_) {
			handler.onUpdateEntityPositionAndRotation(remote, packet_);
			return true;
		}

		return false;
	}

}