package gameEngine;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public abstract class GameScene {
	protected RootGameNode rootNode = new RootGameNode();
	protected PhysicEngine physicEngine = new PhysicEngine();
	
	final void onMouseMoved(MouseEvent event) {
		rootNode._onMouseMoved(event);
	}
	final void onMousePressed(MouseEvent event) {
		rootNode._onMousePressed(event);
	}
	final void onMouseReleased(MouseEvent event) {
		rootNode._onMouseReleased(event);
	}
	final void onMouseEntered(MouseEvent event) {
		rootNode._onMouseEntered(event);
	}
	final void onMouseExited(MouseEvent event) {
		rootNode._onMouseExited(event);
	}

	final void onKeyPressed(KeyEvent event) {
		rootNode._onKeyPressed(event);
	}
	final void onKeyReleased(KeyEvent event) {
		rootNode._onKeyReleased(event);
	}
	
	final void update(long elapse) {
		physicEngine.updatePosition(elapse);
		physicEngine.handleCollisions(elapse);
		rootNode._update(elapse);
		physicEngine.updateVelocity(elapse);
	}
	final void render(GraphicsContext gc) {
		rootNode._render(gc);
	}
	final void renderDebug(GraphicsContext gc) {
		rootNode.renderDebug(gc);
	}

	final void renderPhysicEngineDebug(GraphicsContext gc) {
		physicEngine.renderDebug(gc);
	}
}