package voxel.networking.packet;

public enum ConnectionState {

	HANDSHAKE,
	STATUS,
	LOGIN,
	PLAY;

	private static final ConnectionState[] VALUES = ConnectionState.values();

	public static ConnectionState valueOf(int ordinal) {
		return VALUES[ordinal];
	}

}