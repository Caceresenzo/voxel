package voxel.common.packet;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class PacketRegistry {

	private final Map<ConnectionState, StatePacketRegistry> stateRegistries = new EnumMap<>(ConnectionState.class);

	public PacketRegistry() {
		Arrays.stream(ConnectionState.values())
			.forEach((state) -> stateRegistries.put(state, new StatePacketRegistry()));
	}

	public <T extends Packet> void register(ConnectionState state, int number, Class<T> clazz, PacketSerializer<T> serializer) {
		register(new PacketIdentifier<T>(state, number, clazz, serializer));
	}

	public void register(PacketIdentifier<?> identifier) {
		stateRegistries.get(identifier.state()).register(identifier);
	}

	public PacketIdentifier<?> get(ConnectionState state, int number) {
		return stateRegistries.get(state).get(number);
	}

	public PacketIdentifier<?> get(ConnectionState state, Class<?> clazz) {
		return stateRegistries.get(state).get(clazz);
	}

	private static class StatePacketRegistry {

		private final Map<Integer, PacketIdentifier<?>> numberToIdentifier = new HashMap<>();
		private final Map<Class<? extends Packet>, PacketIdentifier<?>> classToIdentifier = new HashMap<>();

		public void register(PacketIdentifier<?> identifier) {
			numberToIdentifier.put(identifier.number(), identifier);
			classToIdentifier.put(identifier.clazz(), identifier);
		}

		public PacketIdentifier<?> get(int number) {
			return numberToIdentifier.get(number);
		}

		public PacketIdentifier<?> get(Class<?> clazz) {
			return classToIdentifier.get(clazz);
		}

	}

}