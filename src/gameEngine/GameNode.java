package gameEngine;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public abstract class GameNode {
	public Rectangle2D.Double geometry = new Rectangle2D.Double(0, 0, 0, 0);
	public double anchorX = 0, anchorY = 0;
	public double scaleX = 1, scaleY = 1;
	public double offsetX = 0, offsetY = 0;

	public Rectangle2D.Double mouseBound = new Rectangle2D.Double(0, 0, 0, 0);

	// For physical engine
	public double mass = 1;
	public double pulseX = 0, pulseY = 0;
	public double fx = 0, fy = 0;
	public double ax = 0, ay = 0;
	public double vx = 0, vy = 0;
	public double dampX = 1, dampY = 1;

	public Double alpha = 1.0;
	public Boolean visible = true;

	private boolean isMouseEntered = false;
	private HashSet<GameNode> enteredAreaSet = new HashSet<GameNode>();
	private HashSet<Integer> collisionGroupSet = new HashSet<Integer>();

	private Optional<GameNode> parent = Optional.empty();
	private ArrayList<GameNode> children = new ArrayList<GameNode>();

	public final Optional<Rectangle2D.Double> geometryInGameWorld() {
		if (!parent.isPresent()) {
			return Optional.empty();
		}

		Optional<GameNode> p = parent;
		Rectangle2D.Double result = new Rectangle2D.Double();

		result.setFrame(geometry);
		result.x += offsetX;
		result.y += offsetY;

		// TODO Should check if this node was attached to root node
		while (p.isPresent()) {
			GameNode parentNode = p.get();
			result.x += parentNode.geometry.x + parentNode.offsetX;
			result.y += parentNode.geometry.y + parentNode.offsetY;

			p = parentNode.parent;
		}

		return Optional.of(result);
	}

	final boolean _onMouseMoved(MouseEvent event) {
		for (int i = children.size() - 1; i >=0; i--) {
			GameNode child = children.get(i);

			if (child._onMouseMoved(event) == false) {
				return false;
			}
		};

		Point2D.Double translate = getTranslationInScreen();
		Boolean result = operateWithTranslate(translate, () -> {
			return onMouseMoved(event);
		});

		return result;
	}

	final boolean _onMousePressed(MouseEvent event) {
		Point point = new Point((int) event.getX(), (int) event.getY());

		for (int i = children.size() - 1; i >=0; i--) {
			GameNode child = children.get(i);

			if (child._onMousePressed(event) == false) {
				return false;
			}
		};

		Point2D.Double translate = getTranslationInScreen();
		Optional<Boolean> result = operateWithTranslate(translate, () -> {
			if (mouseBound.contains(point)) {
				return Optional.of(onMousePressed(event));
			} else {
				return Optional.empty();
			}
		});

		return result.isPresent() ? result.get() : true;
	}

	final boolean _onMouseReleased(MouseEvent event) {
		Point point = new Point((int) event.getX(), (int) event.getY());

		for (int i = children.size() - 1; i >=0; i--) {
			GameNode child = children.get(i);

			if (child._onMouseReleased(event) == false) {
				return false;
			}
		};

		Point2D.Double translate = getTranslationInScreen();
		Optional<Boolean> result = operateWithTranslate(translate, () -> {
			if (mouseBound.contains(point)) {
				return Optional.of(onMouseReleased(event));
			} else {
				return Optional.empty();
			}
		});

		return result.isPresent() ? result.get() : true;
	}
	final boolean _onMouseEntered(MouseEvent event) {
		Point point = new Point((int) event.getX(), (int) event.getY());

		for (int i = children.size() - 1; i >=0; i--) {
			GameNode child = children.get(i);

			if (child._onMouseEntered(event) == false) {
				return false;
			}
		};

		Point2D.Double translate = getTranslationInScreen();
		Optional<Boolean> result = operateWithTranslate(translate, () -> {
			if (!isMouseEntered && mouseBound.contains(point)) {
				isMouseEntered = true;
				return Optional.of(onMouseEntered(event));
			} else {
				return Optional.empty();
			}
		});

		return result.isPresent() ? result.get() : true;
	}
	final boolean _onMouseExited(MouseEvent event) {
		Point point = new Point((int) event.getX(), (int) event.getY());

		for (int i = children.size() - 1; i >=0; i--) {
			GameNode child = children.get(i);

			if (child._onMouseExited(event) == false) {
				return false;
			}
		};

		Point2D.Double translate = getTranslationInScreen();
		Optional<Boolean> result = operateWithTranslate(translate, () -> {
			if (isMouseEntered && !mouseBound.contains(point)) {
				isMouseEntered = false;
				return Optional.of(onMouseExited(event));
			} else {
				return Optional.empty();
			}
		});

		return result.isPresent() ? result.get() : true;
	}
	final boolean _onKeyPressed(KeyEvent event) {
		for (int i = children.size() - 1; i >=0; i--) {
			GameNode child = children.get(i);

			if (child._onKeyPressed(event) == false) {
				return false;
			}
		};

		return onKeyPressed(event);
	}
	final boolean _onKeyReleased(KeyEvent event) {
		for (int i = children.size() - 1; i >=0; i--) {
			GameNode child = children.get(i);

			if (child._onKeyReleased(event) == false) {
				return false;
			}
		};

		return onKeyReleased(event);
	}

	protected boolean onMouseMoved(MouseEvent event) { return true; }
	protected boolean onMousePressed(MouseEvent event) { return true; }
	protected boolean onMouseReleased(MouseEvent event) { return true; }
	protected boolean onMouseEntered(MouseEvent event) { return true; }
	protected boolean onMouseExited(MouseEvent event) { return true; }

	protected boolean onKeyPressed(KeyEvent event) { return true; }
	protected boolean onKeyReleased(KeyEvent event) { return true; }

	final void _onAreaEntered(GameNode node, long elapse) {
		if (!enteredAreaSet.contains(node)) {
			enteredAreaSet.add(node);
			onAreaEntered(node, elapse);
		}
	}
	final void _onAreaExited(GameNode node, long elapse) {
		if (enteredAreaSet.contains(node)) {
			enteredAreaSet.remove(node);
			onAreaExited(node, elapse);
		}
	}

	public boolean isAreaEntred() { return !enteredAreaSet.isEmpty(); }
	public Set<GameNode> enteredAreaSet() { return Collections.unmodifiableSet(enteredAreaSet); }

	// Physics event handlers
	public void onCollided(GameNode node, long elapse) {}
	public void onAreaEntered(GameNode node, long elapse) {}
	public void onAreaExited(GameNode node, long elapse) {}

	public final HashSet<Integer> colissionGroup() { return collisionGroupSet; }
	public final void addColissionGroup(int groupId) { collisionGroupSet.add(groupId); }

	public final Optional<GameNode> parent() {
		return parent;
	}

	public final boolean detachFromParent() {
		if (parent.isPresent()) {
			parent.get().removeChild(this);
			return true;
		} else {
			return false;
		}
	}

	public final List<GameNode> children() {
		return Collections.unmodifiableList(children);
	}

	public final void addChild(GameNode node) {
		if (node.parent.isPresent()) {
			GameNode parent = node.parent.get();
			parent.removeChild(node);
		}

		node.parent = Optional.of(this);
		children.add(node);
	}
	public final boolean removeChild(GameNode node) {
		boolean childExists = children.remove(node);
		if (childExists) {
			node.parent = Optional.empty();
		}

		return childExists;
	}

	final void _render(GraphicsContext gc) {
		if (!visible) {
			return;
		}

		gc.save();
		{
			gc.setGlobalAlpha(alpha);
			gc.translate(offsetX, offsetY);
			gc.scale(scaleX, scaleY);

			gc.save();
			{
				gc.translate(-geometry.width * anchorX * (1 - 1 / scaleX), -geometry.height * anchorY * (1 - 1 / scaleY));
				render(gc);
			}
			gc.restore();

			gc.translate(geometry.x, geometry.y);

			children.forEach(node -> {
				node._render(gc);
			});
		}
		gc.restore();
	}

	final void _update(long elapse) {
		update(elapse);
		children.forEach(node -> node._update(elapse));
	}

	public void update(long elapse) {}

	public void render(GraphicsContext gc) {}
	public void renderDebug(GraphicsContext gc) {
		gc.save();
		{
			if (visible) {
				gc.translate(offsetX, offsetY);
				gc.setStroke(Color.AQUA);
				gc.strokeRect(mouseBound.x, mouseBound.y, mouseBound.width, mouseBound.height);

				gc.translate(geometry.x, geometry.y);

				children.forEach(node -> {
					node.renderDebug(gc);
				});
			}
		}
		gc.restore();
	}

	public Point2D.Double getTranslationInScreen() {
		Point2D.Double result = new Point2D.Double();

		if (!parent.isPresent()) {
			return result;
		}

		Optional<GameNode> parent = this.parent;

		result.x = offsetX;
		result.y = offsetY;

		while (parent.isPresent()) {
			GameNode parentVal = parent.get();

			result.x += parentVal.geometry.x + parentVal.offsetX;
			result.y += parentVal.geometry.y + parentVal.offsetY;

			parent = parentVal.parent;
		}

		return result;
	}

	private <T> T operateWithTranslate(Point2D.Double translate, Supplier<T> op) {
		T result;

		mouseBound.x += translate.x;
		mouseBound.y += translate.y;
		{
			result = op.get();
		}
		mouseBound.x -= translate.x;
		mouseBound.y -= translate.y;

		return result;
	}
}
