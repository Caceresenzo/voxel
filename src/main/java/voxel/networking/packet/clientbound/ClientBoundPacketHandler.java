package voxel.networking.packet.clientbound;

import voxel.networking.Remote;
import voxel.networking.packet.Packet;
import voxel.networking.packet.clientbound.login.LoginSuccessPacket;
import voxel.networking.packet.clientbound.other.PongPacket;
import voxel.networking.packet.clientbound.play.BlockUpdatePacket;
import voxel.networking.packet.clientbound.play.ChunkDataPacket;
import voxel.networking.packet.clientbound.play.GameEventPacket;
import voxel.networking.packet.clientbound.play.LoginPacket;
import voxel.networking.packet.clientbound.play.PlayerInfoUpdatePacket;
import voxel.networking.packet.clientbound.play.SetCenterChunkPacket;
import voxel.networking.packet.clientbound.play.SynchronizePlayerPositionPacket;
import voxel.networking.packet.clientbound.play.UpdateEntityPositionAndRotationPacket;
import voxel.networking.packet.clientbound.status.StatusResponsePacket;

public interface ClientBoundPacketHandler<T extends Remote> {

	void onPong(T remote, PongPacket packet);

	void onStatusResponse(T remote, StatusResponsePacket packet);

	void onLoginSuccess(T remote, LoginSuccessPacket packet);

	void onLogin(T remote, LoginPacket packet);

	void onBlockUpdate(T remote, BlockUpdatePacket packet);

	void onChunkData(T remote, ChunkDataPacket packet);

	void onGameEvent(T remote, GameEventPacket packet);

	void onPlayerInfoUpdate(T remote, PlayerInfoUpdatePacket packet);

	void onSetCenterChunk(T remote, SetCenterChunkPacket packet);

	void onSynchronizePlayerPosition(T remote, SynchronizePlayerPositionPacket packet);

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

		if (packet instanceof LoginPacket packet_) {
			handler.onLogin(remote, packet_);
			return true;
		}

		if (packet instanceof BlockUpdatePacket packet_) {
			handler.onBlockUpdate(remote, packet_);
			return true;
		}

		if (packet instanceof ChunkDataPacket packet_) {
			handler.onChunkData(remote, packet_);
			return true;
		}

		if (packet instanceof GameEventPacket packet_) {
			handler.onGameEvent(remote, packet_);
			return true;
		}

		if (packet instanceof PlayerInfoUpdatePacket packet_) {
			handler.onPlayerInfoUpdate(remote, packet_);
			return true;
		}

		if (packet instanceof SetCenterChunkPacket packet_) {
			handler.onSetCenterChunk(remote, packet_);
			return true;
		}

		if (packet instanceof SynchronizePlayerPositionPacket packet_) {
			handler.onSynchronizePlayerPosition(remote, packet_);
			return true;
		}

		if (packet instanceof UpdateEntityPositionAndRotationPacket packet_) {
			handler.onUpdateEntityPositionAndRotation(remote, packet_);
			return true;
		}

		return false;
	}

}