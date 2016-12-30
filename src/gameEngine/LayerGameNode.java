package gameEngine;

public class LayerGameNode extends GameNode {
	public GameSceneCamera camera = new GameSceneCamera();

	public LayerGameNode() {
	}

	@Override
	public void update(long elapse) {
		camera.update(elapse);

		geometry.x = -camera.geometry.x;
		geometry.y = -camera.geometry.y;

		offsetX = camera.offsetX;
		offsetY = camera.offsetY;

		scaleX = camera.zoom;
		scaleY = camera.zoom;

		anchorX = camera.anchorX;
		anchorY = camera.anchorY;
	}
}
