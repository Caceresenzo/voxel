package voxel.client.graphics.mesh.marker;

import lombok.RequiredArgsConstructor;
import voxel.client.Camera;
import voxel.client.VoxelHandler;
import voxel.client.graphics.opengl.texture.Texture;

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