package voxel.client.crosshair;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CrossHair {

	private final CrossHairMesh mesh;

	public void render() {
		mesh.render();
	}

}