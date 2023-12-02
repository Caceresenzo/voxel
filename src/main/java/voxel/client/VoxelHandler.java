package voxel.client;

import org.joml.Vector3f;
import org.joml.Vector3i;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import voxel.client.chunk.Chunk;
import voxel.client.player.LocalPlayer;
import voxel.client.world.World;

@RequiredArgsConstructor
public class VoxelHandler {

	private final LocalPlayer player;
	private final World world;

	private @Getter Vector3i currentVoxelPosition;
	private @Getter Vector3i voxelWorldPosition;
	private @Getter Vector3i voxelLocalPosition;
	private @Getter Chunk chunk;
	private @Getter byte voxelId;
	private @Getter int voxelIndex;
	private @Getter Vector3i voxelNormal;

	public static float fract(float x) {
		return (float) (x - Math.floor(x));
	}

	public boolean place() {
//		if (rayCast() && getVoxelId(new Vector3i(currentVoxelPosition).add(voxelNormal)) && voxelId == 0) {
//			chunk.getVoxels()[voxelIndex] = 1;
//			chunk.testIfEmpty();
//			chunk.getMesh().rebuild();
//
//			return true;
//		}

		return false;
	}

	public boolean destroy() {
//		if (rayCast() && voxelId != 0) {
//			chunk.getVoxels()[voxelIndex] = 0;
//			chunk.testIfEmpty();
//			chunk.getMesh().rebuild();
//
//			rebuildAdjacentChunks();
//
//			return true;
//		}

		return false;
	}

	public void rebuildAdjacentChunk(Vector3i adjacentVoxelPosition) {
//		adjacentVoxelPosition.div(Settings.CHUNK_SIZE);
//		final var index = World.toChunkIndex(adjacentVoxelPosition);
//
//		if (0 <= index && index < chunk.getVoxels().length) {
//			world.getChunks()[index].getMesh().rebuild();
//		}
	}

	public void rebuildAdjacentChunks() {
//		if (voxelLocalPosition.x == 0) {
//			rebuildAdjacentChunk(new Vector3i(
//				voxelWorldPosition.x - 1,
//				voxelWorldPosition.y,
//				voxelWorldPosition.z
//			));
//		} else if (voxelLocalPosition.x == Settings.CHUNK_SIZE - 1) {
//			rebuildAdjacentChunk(new Vector3i(
//				voxelWorldPosition.x + 1,
//				voxelWorldPosition.y,
//				voxelWorldPosition.z
//			));
//		}
//
//		if (voxelLocalPosition.y == 0) {
//			rebuildAdjacentChunk(new Vector3i(
//				voxelWorldPosition.x,
//				voxelWorldPosition.y - 1,
//				voxelWorldPosition.z
//			));
//		} else if (voxelLocalPosition.y == Settings.CHUNK_SIZE - 1) {
//			rebuildAdjacentChunk(new Vector3i(
//				voxelWorldPosition.x,
//				voxelWorldPosition.y - 1,
//				voxelWorldPosition.z
//			));
//		}
//
//		if (voxelLocalPosition.z == 0) {
//			rebuildAdjacentChunk(new Vector3i(
//				voxelWorldPosition.x,
//				voxelWorldPosition.y,
//				voxelWorldPosition.z - 1
//			));
//		} else if (voxelLocalPosition.z == Settings.CHUNK_SIZE - 1) {
//			rebuildAdjacentChunk(new Vector3i(
//				voxelWorldPosition.x,
//				voxelWorldPosition.y,
//				voxelWorldPosition.z + 1
//			));
//		}
	}

	public void update() {
		//		if (rayCast()) {
		//			System.out.printf("%s %n", voxelLocalPosition.toString(NumberFormat.getIntegerInstance()));
		//		}
		//		System.out.println("----------");
	}

	public boolean rayCast() {
//		final var start = player.getPosition();
//		final var end = new Vector3f(start).add(new Vector3f(player.getForward()).mul(Settings.MAX_RAY_DIST));
//
//		currentVoxelPosition = new Vector3i((int) start.x, (int) start.y, (int) start.z);
//		voxelId = 0;
//		voxelNormal = new Vector3i(0);
//		var stepDirection = -1;
//
//		final var directionX = (int) Math.signum(end.x - start.x);
//		final var deltaX = directionX != 0
//			? Math.min(directionX / (end.x - start.x), 10000000.0f)
//			: 10000000.0f;
//		var maxX = directionX > 0
//			? deltaX * (1.0 - fract(start.x))
//			: deltaX * fract(start.x);
//
//		final var directionY = (int) Math.signum(end.y - start.y);
//		final var deltaY = directionY != 0
//			? Math.min(directionY / (end.y - start.y), 10000000.0f)
//			: 10000000.0f;
//		var maxY = directionY > 0
//			? deltaY * (1.0 - fract(start.y))
//			: deltaY * fract(start.y);
//
//		final var directionZ = (int) Math.signum(end.z - start.z);
//		final var deltaZ = directionZ != 0
//			? Math.min(directionZ / (end.z - start.z), 10000000.0f)
//			: 10000000.0f;
//		var maxZ = directionZ > 0
//			? deltaZ * (1.0 - fract(start.z))
//			: deltaZ * fract(start.z);
//
//		//		System.out.printf("directionX=%f deltaX=%f maxX=%f %n", directionX, deltaX, maxX);
//		//		System.out.printf("directionY=%f deltaY=%f maxY=%f %n", directionY, deltaY, maxY);
//		//		System.out.printf("directionZ=%f deltaZ=%f maxZ=%f %n", directionZ, deltaZ, maxZ);
//
//		while (!(maxX > 1.0 && maxY > 1.0 && maxZ > 1.0)) {
//			//			System.out.printf("--%ncurrentVoxelPosition=%s %n", currentVoxelPosition.toString(NumberFormat.getIntegerInstance()));
//			if (getVoxelId(currentVoxelPosition) && voxelId != 0) {
//				this.voxelWorldPosition = currentVoxelPosition;
//
//				if (stepDirection == 0) {
//					voxelNormal.x = -directionX;
//				} else if (stepDirection == 1) {
//					voxelNormal.y = -directionY;
//				} else {
//					voxelNormal.z = -directionZ;
//				}
//
//				return true;
//			}
//
//			if (maxX < maxY) {
//				if (maxX < maxZ) {
//					currentVoxelPosition.x += directionX;
//					maxX += deltaX;
//					stepDirection = 0;
//				} else {
//					currentVoxelPosition.z += directionZ;
//					maxZ += deltaZ;
//					stepDirection = 2;
//				}
//			} else {
//				if (maxY < maxZ) {
//					currentVoxelPosition.y += directionY;
//					maxY += deltaY;
//					stepDirection = 1;
//				} else {
//					currentVoxelPosition.z += directionZ;
//					maxZ += deltaZ;
//					stepDirection = 2;
//				}
//			}
//		}

		return false;
	}

	public boolean getVoxelId(Vector3i position) {
//		if (position.x < 0 || position.y < 0 || position.z < 0) {
//			return false;
//		}
//
//		//		System.out.printf("position=%s %n", position.toString(NumberFormat.getIntegerInstance()));
//		final var chunkPosition = new Vector3i(position).div(Settings.CHUNK_SIZE);
//		//		System.out.printf("chunkPosition=%s %n", chunkPosition.toString(NumberFormat.getIntegerInstance()));
//
//		if (
//			(0 <= chunkPosition.x && chunkPosition.x < Settings.WORLD_WIDTH) &&
//			(0 <= chunkPosition.y && chunkPosition.y < Settings.WORLD_HEIGHT) &&
//			(0 <= chunkPosition.z && chunkPosition.z < Settings.WORLD_DEPTH)
//		) {
//			final var chunkIndex = World.toChunkIndex(chunkPosition);
//			final var chunk = world.getChunks()[chunkIndex];
//
//			final var localPosition = new Vector3i(position).sub(new Vector3i(chunkPosition).mul(Settings.CHUNK_SIZE));
//
//			//			System.out.printf("localPosition=%s %n", localPosition.toString(NumberFormat.getIntegerInstance()));
//			final var voxelIndex = Chunk.index(localPosition);
//			//			System.out.printf("voxelIndex=%s %n", voxelIndex);
//			final var voxelId = chunk.getVoxels()[voxelIndex];
//
//			this.voxelId = voxelId;
//			this.voxelIndex = voxelIndex;
//			this.voxelLocalPosition = localPosition;
//			this.chunk = chunk;
//
//			return true;
//		}

		return false;
	}

}