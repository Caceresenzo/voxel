package voxel.client.game;

public interface GameState {
	
	void initialize();
	
	void update();
	
	void render();
	
	void cleanup();

}