package mirrorWar;

import gameEngine.Game;
import gameEngine.GameNode;
import javafx.scene.canvas.GraphicsContext;

public class CyclicTiledBackground extends GameNode {
	private GameNode tile;
	private double offsetX = 0, offsetY = 0;

	private final double MOVE_SPEED = 0.02;

	public CyclicTiledBackground(GameNode tile) {
		this.tile = tile;
	}

	@Override
	public void update(long elapse) {
		tile.update(elapse);

		offsetX += MOVE_SPEED * elapse;
		while (offsetX >= tile.geometry.width) {
			offsetX -= tile.geometry.width;
		}

		offsetY += MOVE_SPEED * elapse;
		while (offsetY >= tile.geometry.height) {
			offsetY -= tile.geometry.height;
		}
	}

	@Override
	public void render(GraphicsContext gc) {
		int rowNum = (int) (Game.canvasHeight() / tile.geometry.height) + 2;
		int columnNum = (int) (Game.canvasWidth() / tile.geometry.width) + 2;

		for (int r = 0; r < rowNum; ++r) {
			tile.geometry.y = offsetY + (r - 1) * tile.geometry.height;

			for (int c = 0; c < columnNum; ++c) {
				tile.geometry.x = offsetX + (c - 1) * tile.geometry.width;
				tile._render(gc);
			}
		}
	}
}
