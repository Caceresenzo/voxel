package voxel.client.player;

import java.util.UUID;

import org.joml.Vector3fc;

import voxel.client.world.World;

public interface Player {

	UUID getUUID();

	String getLogin();

	Vector3fc getPosition();

	float getYaw();

	float getPitch();

	World getWorld();

	void move(float x, float y, float z, float yaw, float pitch);

}