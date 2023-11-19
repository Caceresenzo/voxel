package voxel.client.skybox;

import java.awt.Color;

import lombok.RequiredArgsConstructor;
import voxel.client.player.LocalPlayer;

@RequiredArgsConstructor
public class SkyBox {

	private final SkyBoxMesh mesh;
	private Color dayColor = new Color(0x82caff);
	private Color nightColor = new Color(0x131862);

	public void render(LocalPlayer player, long time) {
		mesh.render(player, time, dayColor, nightColor);
	}

}