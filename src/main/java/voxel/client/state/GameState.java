package voxel.client.state;

public interface GameState {
	
	void initialize();
	
	void update();
	
	void render();
	
	void cleanup();

}