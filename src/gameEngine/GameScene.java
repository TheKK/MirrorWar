package gameEngine;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public abstract class GameScene {
	protected RootGameNode rootNode = new RootGameNode();
	protected PhysicEngine physicEngine = new PhysicEngine();
	
	protected boolean onMouseMoved(MouseEvent event) { return true; }
	protected boolean onMousePressed(MouseEvent event) { return true; }
	protected boolean onMouseReleased(MouseEvent event) { return true; }
	protected boolean onKeyPressed(KeyEvent event) { return true; }
	protected boolean onKeyReleased(KeyEvent event) { return true; }

	final void _onMouseMoved(MouseEvent event) {
		if (onMouseMoved(event)) {
			rootNode._onMouseMoved(event);
		}
	}
	final void _onMousePressed(MouseEvent event) {
		if (onMousePressed(event)) {
			rootNode._onMousePressed(event);
		}
	}
	final void _onMouseReleased(MouseEvent event) {
		if (onMouseReleased(event)) {
			rootNode._onMouseReleased(event);
		}
	}
	final void _onMouseEntered(MouseEvent event) {
		rootNode._onMouseEntered(event);
	}
	final void _onMouseExited(MouseEvent event) {
		rootNode._onMouseExited(event);
	}

	final void _onKeyPressed(KeyEvent event) {
		if (onKeyPressed(event)) {
			rootNode._onKeyPressed(event);
		}
	}
	final void _onKeyReleased(KeyEvent event) {
		if (onKeyReleased(event)) {
			rootNode._onKeyReleased(event);
		}
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