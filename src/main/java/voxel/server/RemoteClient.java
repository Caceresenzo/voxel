package voxel.server;

import java.net.Socket;
import java.util.concurrent.ThreadFactory;

import lombok.Getter;
import lombok.Setter;
import voxel.networking.Remote;
import voxel.networking.packet.ConnectionState;
import voxel.networking.packet.Packet;
import voxel.networking.packet.PacketRegistries;
import voxel.server.player.Player;

public class RemoteClient extends Remote {

	private final Server server;
	private @Getter @Setter Player player;
	private @Getter @Setter int latency;

	public RemoteClient(Server server, Socket socket, ThreadFactory threadFactory) {
		super(
			socket,
			PacketRegistries.CLIENT_BOUND,
			PacketRegistries.SERVER_BOUND,
			threadFactory
		);

		this.server = server;
	}

	@Override
	public void onPacketReceived(Packet packet) {
		server.onPacketReceived(this, packet);
	}

	@Override
	public void onPacketSent(Packet packet) {
	}

	void setState(ConnectionState state) {
		this.state = state;
	}

}