package voxel.server;

public record PendingTeleport(
	int id,
	Runnable runnable
) {}