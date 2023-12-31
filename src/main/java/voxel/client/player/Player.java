package voxel.client.player;

import java.util.UUID;

import org.joml.Vector3f;

import voxel.client.world.World;

public interface Player {

	UUID getUUID();

	String getLogin();

	Vector3f getPosition();

	float getYaw();

	float getPitch();
	
	World getWorld();

}