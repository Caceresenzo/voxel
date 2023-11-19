package voxel.client.state;

public enum NullGameState implements GameState {
	
	INSTANCE;

	@Override
	public void initialize() {
	}

	@Override
	public void update() {
	}

	@Override
	public void render() {
	}

	@Override
	public void cleanup() {
	}

}