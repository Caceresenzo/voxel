package voxel.common.packet.clientbound;

import voxel.common.packet.Remote;
import voxel.common.packet.clientbound.login.LoginSuccessPacket;
import voxel.common.packet.clientbound.other.PongPacket;
import voxel.common.packet.clientbound.status.StatusResponsePacket;

public interface ClientBoundPacketHandler<T extends Remote> {

	void onPong(T client, PongPacket packet);
	
	void onStatusResponse(T client, StatusResponsePacket packet);
	
	void onLoginSuccess(T client, LoginSuccessPacket packet);

}