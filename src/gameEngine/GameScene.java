package gameEngine;

import java.awt.geom.Rectangle2D;
import java.util.Optional;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public abstract class GameScene {
	protected RootGameNode rootNode = new RootGameNode();
	protected PhysicEngine physicEngine = new PhysicEngine();
	protected Optional<GameSceneCamera> camera = Optional.empty();

	final void onMouseMoved(MouseEvent event) {
		rootNode.children.forEach(node -> {
			node.onMouseMoved(event);
		});
	}
	final void onMousePressed(MouseEvent event) {
		if (camera.isPresent()) {
			Rectangle2D.Double cameraCache = (Rectangle2D.Double) camera.get().geometry().clone();

			rootNode.geometry.x -= cameraCache.x;
			rootNode.geometry.y -= cameraCache.y;
			{
				rootNode._onMousePressed(event);
			}
			rootNode.geometry.x += cameraCache.x;
			rootNode.geometry.y += cameraCache.y;
		} else {
			rootNode._onMousePressed(event);
		}
	}
	final void onMouseReleased(MouseEvent event) {
		if (camera.isPresent()) {
			Rectangle2D.Double cameraCache = (Rectangle2D.Double) camera.get().geometry().clone();

			rootNode.geometry.x -= cameraCache.x;
			rootNode.geometry.y -= cameraCache.y;
			{
				rootNode._onMouseReleased(event);
			}
			rootNode.geometry.x += cameraCache.x;
			rootNode.geometry.y += cameraCache.y;
		} else {
			rootNode._onMouseReleased(event);
		}
	}
	final void onMouseEntered(MouseEvent event) {
		if (camera.isPresent()) {
			Rectangle2D.Double cameraCache = (Rectangle2D.Double) camera.get().geometry().clone();

			rootNode.geometry.x -= cameraCache.x;
			rootNode.geometry.y -= cameraCache.y;
			{
				rootNode._onMouseEntered(event);
			}
			rootNode.geometry.x += cameraCache.x;
			rootNode.geometry.y += cameraCache.y;
		} else {
			rootNode._onMouseEntered(event);
		}
	}
	final void onMouseExited(MouseEvent event) {
		if (camera.isPresent()) {
			Rectangle2D.Double cameraCache = (Rectangle2D.Double) camera.get().geometry().clone();

			rootNode.geometry.x -= cameraCache.x;
			rootNode.geometry.y -= cameraCache.y;
			{
				rootNode._onMouseExited(event);
			}
			rootNode.geometry.x += cameraCache.x;
			rootNode.geometry.y += cameraCache.y;
		} else {
			rootNode._onMouseExited(event);
		}
	}

	final void onKeyPressed(KeyEvent event) {
		rootNode._onKeyPressed(event);
	}
	final void onKeyReleased(KeyEvent event) {
		rootNode._onKeyReleased(event);
	}
	
	final Optional<GameSceneCamera> camera() {
		return camera;
	}
	final void setCamera(GameSceneCamera newCamera) {
		camera = Optional.of(newCamera);
	}
	final Optional<GameSceneCamera> removeCamera() {
		Optional<GameSceneCamera> result = camera;
		
		camera = Optional.empty();
		
		return result;
	}

	final void update(long elapse) {
		rootNode._update(elapse);

		if (camera.isPresent()) {
			camera.get().update(elapse);
		}
	}
	final void physicEngineUpdate(long elapse) {
		physicEngine.update(elapse);
	}
	final void render(GraphicsContext gc) {
		if (camera.isPresent()) {
			Rectangle2D.Double cameraGeometry = camera.get().geometry();

			gc.save();
			gc.translate(-cameraGeometry.x, -cameraGeometry.y);
			{
				rootNode._render(gc);
			}
			gc.restore();
		} else {
			rootNode._render(gc);
		}
	}
	final void renderDebug(GraphicsContext gc) {
		if (camera.isPresent()) {
			Rectangle2D.Double cameraGeometry = camera.get().geometry();

			gc.save();
			gc.translate(-cameraGeometry.x, -cameraGeometry.y);
			{
				rootNode.renderDebug(gc);
			}
			gc.restore();
		} else {
			rootNode.renderDebug(gc);
		}
	}
	final void renderPhysicEngineDebug(GraphicsContext gc) {
		if (camera.isPresent()) {
			Rectangle2D.Double cameraGeometry = camera.get().geometry();

			gc.save();
			gc.translate(-cameraGeometry.x, -cameraGeometry.y);
			{
				physicEngine.renderDebug(gc);
			}
			gc.restore();
		} else {
			physicEngine.renderDebug(gc);
		}
	}
}
