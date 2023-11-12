package voxel.common.packet;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class PacketRegistry {

	private final Map<ConnectionState, Map<Integer, PacketIdentifier<?>>> registry = new EnumMap<>(ConnectionState.class);

	public PacketRegistry() {
		Arrays.stream(ConnectionState.values())
			.forEach((state) -> registry.put(state, new HashMap<>()));
	}

	public PacketIdentifier<?> get(ConnectionState state, int number) {
		return registry.get(state).get(number);
	}

	public void register(PacketIdentifier<?> identifier) {
		Arrays.stream(ConnectionState.values())
			.forEach((state) -> register(state, identifier));
	}

	public void register(ConnectionState state, PacketIdentifier<?> identifier) {
		registry.get(state).put(identifier.number(), identifier);
	}

}