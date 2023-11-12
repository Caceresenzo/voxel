package voxel.common.packet.serverbound;

import voxel.common.packet.Remote;
import voxel.common.packet.serverbound.handshake.HandshakePacket;
import voxel.common.packet.serverbound.login.LoginAcknowledgedPacket;
import voxel.common.packet.serverbound.login.LoginPacket;
import voxel.common.packet.serverbound.other.PingPacket;
import voxel.common.packet.serverbound.status.StatusRequestPacket;

public interface ServerBoundPacketHandler<T extends Remote> {

	void onHandshake(T remote, HandshakePacket packet);

	void onPing(T remote, PingPacket packet);
	
	void onStatusRequest(T remote, StatusRequestPacket packet);

	void onLogin(T remote, LoginPacket packet);

	void onLoginAcknowledged(T remote, LoginAcknowledgedPacket packet);

}