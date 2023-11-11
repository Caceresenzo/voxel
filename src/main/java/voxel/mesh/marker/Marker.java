package voxel.mesh.marker;

import engine.texture.Texture;
import lombok.RequiredArgsConstructor;
import voxel.Camera;
import voxel.VoxelHandler;

@RequiredArgsConstructor
public class Marker {
	
	private final Texture texture;
	private final MarkerMesh mesh;
	
	public void render(Camera camera, VoxelHandler voxelHandler) {
		if (voxelHandler.rayCast() && voxelHandler.getVoxelId() != 0) {
			mesh.move(voxelHandler.getVoxelWorldPosition());

			texture.activate(0);
			texture.bind();
			
			mesh.render(camera);
		}
	}

}