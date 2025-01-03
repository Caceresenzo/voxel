package voxel.networking.packet.serverbound;

import voxel.networking.Remote;
import voxel.networking.packet.Packet;
import voxel.networking.packet.serverbound.handshake.HandshakePacket;
import voxel.networking.packet.serverbound.login.LoginAcknowledgedPacket;
import voxel.networking.packet.serverbound.login.LoginStartPacket;
import voxel.networking.packet.serverbound.other.ConfirmTeleportationPacket;
import voxel.networking.packet.serverbound.other.PingPacket;
import voxel.networking.packet.serverbound.play.PlayerActionPacket;
import voxel.networking.packet.serverbound.play.SetPlayerPositionAndRotationPacket;
import voxel.networking.packet.serverbound.play.UseItemOnPacket;
import voxel.networking.packet.serverbound.status.StatusRequestPacket;

public interface ServerBoundPacketHandler<T extends Remote> {

	void onHandshake(T remote, HandshakePacket packet);

	void onPing(T remote, PingPacket packet);

	void onStatusRequest(T remote, StatusRequestPacket packet);

	void onLogin(T remote, LoginStartPacket packet);

	void onLoginAcknowledged(T remote, LoginAcknowledgedPacket packet);

	void onConfirmTeleportation(T remote, ConfirmTeleportationPacket packet);

	void onPlayerAction(T remote, PlayerActionPacket packet);

	void onSetPlayerPositionAndRotation(T remote, SetPlayerPositionAndRotationPacket packet);

	void onUseItemOn(T remote, UseItemOnPacket packet);

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

		if (packet instanceof ConfirmTeleportationPacket packet_) {
			handler.onConfirmTeleportation(remote, packet_);
			return true;
		}

		if (packet instanceof PlayerActionPacket packet_) {
			handler.onPlayerAction(remote, packet_);
			return true;
		}

		if (packet instanceof SetPlayerPositionAndRotationPacket packet_) {
			handler.onSetPlayerPositionAndRotation(remote, packet_);
			return true;
		}

		if (packet instanceof UseItemOnPacket packet_) {
			handler.onUseItemOn(remote, packet_);
			return true;
		}

		return false;
	}

}