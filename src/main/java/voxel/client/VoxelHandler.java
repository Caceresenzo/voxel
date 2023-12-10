package voxel.client;

import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import voxel.client.chunk.ClientChunk;
import voxel.client.player.LocalPlayer;
import voxel.client.world.World;
import voxel.networking.packet.serverbound.play.PlayerActionPacket;
import voxel.networking.packet.serverbound.play.UseItemOnPacket;
import voxel.shared.block.BlockPosition;
import voxel.shared.chunk.Chunk;
import voxel.shared.chunk.ChunkPosition;
import voxel.shared.chunk.Face;
import voxel.shared.chunk.LocalBlockPosition;
import voxel.shared.chunk.Plane;

@RequiredArgsConstructor
public class VoxelHandler {

	private final LocalPlayer player;
	private final World world;
	private final RemoteServer server;

	private @Getter Vector3i currentVoxelPosition;
	private @Getter Vector3i voxelWorldPosition;
	private @Getter LocalBlockPosition voxelLocalPosition;
	private @Getter ClientChunk chunk;
	private @Getter byte voxelId;
	private @Getter Vector3i voxelNormal;

	public static float fract0(float x) {
		return (float) (x - Math.floor(x));
	}

	public static float fract1(float x) {
		return (float) (1.0f - x + Math.floor(x));
	}

	public boolean place() {
		if (rayCast() && getVoxelId(new Vector3i(currentVoxelPosition).add(voxelNormal)) && voxelId == 0) {
			server.offer(new UseItemOnPacket(new BlockPosition(currentVoxelPosition), Face.from(voxelNormal)));
			return true;
		}

		return false;
	}

	public boolean destroy() {
		if (rayCast() && voxelId != 0) {
			server.offer(PlayerActionPacket.startedDigging(new BlockPosition(currentVoxelPosition), Face.from(voxelNormal)));
			return true;
		}

		return false;
	}

	public void rebuildAdjacentChunk(int x, int y, int z) {
		final var chunk = world.getChunk(x, y, z);

		if (chunk != null) {
			chunk.deleteMesh();
		}
	}

	public void rebuildAdjacentChunks(ClientChunk chunk, LocalBlockPosition localPosition) {
		final var chunkPosition = chunk.getPosition();

		if (localPosition.x() == 0) {
			rebuildAdjacentChunk(
				chunkPosition.x() - 1,
				chunkPosition.y(),
				chunkPosition.z()
			);
		} else if (localPosition.x() == ClientChunk.WIDTH - 1) {
			rebuildAdjacentChunk(
				chunkPosition.x() + 1,
				chunkPosition.y(),
				chunkPosition.z()
			);
		}

		if (localPosition.y() == 0) {
			rebuildAdjacentChunk(
				chunkPosition.x(),
				chunkPosition.y() - 1,
				chunkPosition.z()
			);
		} else if (localPosition.y() == ClientChunk.DEPTH - 1) {
			rebuildAdjacentChunk(
				chunkPosition.x(),
				chunkPosition.y() + 1,
				chunkPosition.z()
			);
		}

		if (localPosition.z() == 0) {
			rebuildAdjacentChunk(
				chunkPosition.x(),
				chunkPosition.y(),
				chunkPosition.z() - 1
			);
		} else if (localPosition.z() == ClientChunk.HEIGHT - 1) {
			rebuildAdjacentChunk(
				chunkPosition.x(),
				chunkPosition.y(),
				chunkPosition.z() + 1
			);
		}
	}

	public void update() {
		if (rayCast()) {
			//			var chunkPosition = chunk != null ? ((Vector3i) chunk.getPosition()).toString(NumberFormat.getIntegerInstance()) : null;
			//			System.out.printf("chunk=%s voxelLocalPosition=%s %n", chunkPosition, voxelLocalPosition.toString(NumberFormat.getIntegerInstance()));
		}
		//		System.out.println("----------");
	}

	public static final float DBL_MAX = 10000000.0f;
	public static final float _bin_size = 1f;

	/**
	 * @author https://github.com/francisengelmann/fast_voxel_traversal
	 * @author https://github.com/francisengelmann/fast_voxel_traversal/issues/7
	 */
	public boolean rayCast() {
		final var start = player.getPosition();
		final var end = new Vector3f(start).add(new Vector3f(player.getForward()).mul(Settings.MAX_RAY_DIST));

		final var ray = end.sub(start, new Vector3f());

		final var current = new Vector3i(
			(int) Math.floor((float) start.x / _bin_size),
			(int) Math.floor((float) start.y / _bin_size),
			(int) Math.floor((float) start.z / _bin_size)
		);

		final var last = new Vector3i(
			(int) Math.floor((float) end.x / _bin_size),
			(int) Math.floor((float) end.y / _bin_size),
			(int) Math.floor((float) end.z / _bin_size)
		);

		currentVoxelPosition = new Vector3i((int) start.x, (int) start.y, (int) start.z);
		voxelId = 0;
		voxelNormal = new Vector3i(0);

		final var stepX = (ray.x >= 0) ? 1f : -1f;
		final var stepY = (ray.y >= 0) ? 1f : -1f;
		final var stepZ = (ray.z >= 0) ? 1f : -1f;

		var nextVoxelBoundaryX = ((float) current.x + stepX);
		if (stepX < 0) {
			nextVoxelBoundaryX += _bin_size;
		}
		var nextVoxelBoundaryY = ((float) current.y + stepY);
		if (stepY < 0) {
			nextVoxelBoundaryY += _bin_size;
		}
		var nextVoxelBoundaryZ = ((float) current.z + stepZ);
		if (stepZ < 0) {
			nextVoxelBoundaryZ += _bin_size;
		}

		var tMaxX = (ray.x != 0) ? (nextVoxelBoundaryX - start.x) / ray.x : DBL_MAX; //
		var tMaxY = (ray.y != 0) ? (nextVoxelBoundaryY - start.y) / ray.y : DBL_MAX; //
		var tMaxZ = (ray.z != 0) ? (nextVoxelBoundaryZ - start.z) / ray.z : DBL_MAX; //

		final var tDeltaX = (ray.x != 0) ? _bin_size / ray.x * stepX : DBL_MAX;
		final var tDeltaY = (ray.y != 0) ? _bin_size / ray.y * stepY : DBL_MAX;
		final var tDeltaZ = (ray.z != 0) ? _bin_size / ray.z * stepZ : DBL_MAX;

		//		final var visited = new ArrayList<Pair<Vector3i, Plane>>();
		//		visited.add(Pair.of(new Vector3i(current), null));

		Plane plane = null;

		while (!last.equals(current)) {
			currentVoxelPosition.set(current);
			if (getVoxelId(currentVoxelPosition) && voxelId != 0) {
				this.voxelWorldPosition = currentVoxelPosition;

				if (plane == Plane.X) {
					voxelNormal.x = (int) -stepX;
				} else if (plane == Plane.Y) {
					voxelNormal.y = (int) -stepY;
				} else {
					voxelNormal.z = (int) -stepZ;
				}

				return true;
			}

			if (tMaxX < tMaxY) {
				if (tMaxX < tMaxZ) {
					current.x += stepX;
					tMaxX += tDeltaX;
					plane = Plane.X;
				} else {
					current.z += stepZ;
					tMaxZ += tDeltaZ;
					plane = Plane.Z;
				}
			} else {
				if (tMaxY < tMaxZ) {
					current.y += stepY;
					tMaxY += tDeltaY;
					plane = Plane.Y;
				} else {
					current.z += stepZ;
					tMaxZ += tDeltaZ;
					plane = Plane.Z;
				}
			}
		}

		return false;
	}

	public boolean getVoxelId(Vector3i position) {
		final var chunk = world.getChunkAt(position);
		if (chunk == null) {
			return false;
		}

		final var localPosition = worldToLocal(position, chunk.getPosition());

		//		System.out.printf("localPosition=%s %n", localPosition.toString(NumberFormat.getIntegerInstance()));
		final var voxelId = chunk.getVoxel(localPosition);

		this.voxelId = voxelId;
		this.voxelLocalPosition = localPosition;
		this.chunk = chunk;

		return true;
	}

	public static LocalBlockPosition worldToLocal(Vector3ic position, ChunkPosition chunkPosition) {
		var x = (position.x() - (chunkPosition.x() * Chunk.WIDTH)) % Chunk.WIDTH;
		var y = (position.y() - (chunkPosition.y() * Chunk.DEPTH)) % Chunk.DEPTH;
		var z = (position.z() - (chunkPosition.z() * Chunk.HEIGHT)) % Chunk.HEIGHT;

		x = (x + Chunk.WIDTH) % Chunk.WIDTH;
		y = (y + Chunk.DEPTH) % Chunk.DEPTH;
		z = (z + Chunk.HEIGHT) % Chunk.HEIGHT;

		return new LocalBlockPosition(x, y, z);
	}

}