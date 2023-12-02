package voxel.client.marker;

import lombok.RequiredArgsConstructor;
import opengl.texture.Texture;
import voxel.client.VoxelHandler;
import voxel.client.render.Camera;

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