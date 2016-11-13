package application;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public abstract class GameNode {
	public Rectangle2D.Double geometry = new Rectangle2D.Double(0, 0, 0, 0);
	public Rectangle2D.Double mouseBound = new Rectangle2D.Double(0, 0, 0, 0);
	
	// For physical engine
	public double mass = 1;
	public double pulseX = 0, pulseY = 0;
	public double fx = 0, fy = 0;
	public double ax = 0, ay = 0;
	public double vx = 0, vy = 0;
	public double dampX = 0, dampY = 0;
	
	public double alpha = 1.0;
	
	private boolean isMouseEntered = false;
	private HashSet<GameNode> enteredAreaSet = new HashSet<GameNode>();
	private HashSet<Integer> collisionGroupSet = new HashSet<Integer>();
	
	private Optional<GameNode> parent = Optional.empty();
	ArrayList<GameNode> children = new ArrayList<GameNode>();

	public final boolean _onMousePressed(MouseEvent event) {
		Point point = new Point((int) event.getX(), (int) event.getY());
		
		Double d = Double.valueOf(10);
		
		for (int i = children.size() - 1; i >=0; i--) {
			GameNode child = children.get(i);

			if (child._onMousePressed(event) == false) {
				return false;
			}
		};
		
		Optional<GameNode> parent = this.parent;
		Point2D.Double translate = new Point2D.Double(0, 0);
		boolean result = true;

		// Compute world coordinate
		while (parent.isPresent()) {
			translate.x += parent.get().geometry.x;
			translate.y += parent.get().geometry.y;
			
			parent = parent.get().parent;
		}
		
		mouseBound.x += translate.x;
		mouseBound.y += translate.y;
		{
			if (mouseBound.contains(point)) {
				result = onMousePressed(event);
			}
		}
		mouseBound.x -= translate.x;
		mouseBound.y -= translate.y;
		
		return result;
	}
	public final boolean _onMouseReleased(MouseEvent event) {
		Point point = new Point((int) event.getX(), (int) event.getY());
		
		for (int i = children.size() - 1; i >=0; i--) {
			GameNode child = children.get(i);

			if (child._onMouseReleased(event) == false) {
				return false;
			}
		};
		
		Optional<GameNode> parent = this.parent;
		Point2D.Double translate = new Point2D.Double(0, 0);
		boolean result = true;

		// Compute world coordinate
		while (parent.isPresent()) {
			translate.x += parent.get().geometry.x;
			translate.y += parent.get().geometry.y;
			
			parent = parent.get().parent;
		}
		
		mouseBound.x += translate.x;
		mouseBound.y += translate.y;
		{
			if (mouseBound.contains(point)) {
				result = onMouseReleased(event);
			}
		}
		mouseBound.x -= translate.x;
		mouseBound.y -= translate.y;
		
		return result;
	}
	public final boolean _onMouseEntered(MouseEvent event) {
		Point point = new Point((int) event.getX(), (int) event.getY());
		
		for (int i = children.size() - 1; i >=0; i--) {
			GameNode child = children.get(i);

			if (child._onMouseEntered(event) == false) {
				return false;
			}
		};

		Optional<GameNode> parent = this.parent;
		Point2D.Double translate = new Point2D.Double(0, 0);
		boolean result = true;

		// Compute world coordinate
		while (parent.isPresent()) {
			translate.x += parent.get().geometry.x;
			translate.y += parent.get().geometry.y;
			
			parent = parent.get().parent;
		}
		
		mouseBound.x += translate.x;
		mouseBound.y += translate.y;
		{
			if (!isMouseEntered && mouseBound.contains(point)) {
				isMouseEntered = true;
				result = onMouseEntered(event);
			}
		}
		mouseBound.x -= translate.x;
		mouseBound.y -= translate.y;
		
		return result;
	}
	public final boolean _onMouseExited(MouseEvent event) {
		Point point = new Point((int) event.getX(), (int) event.getY());
		
		for (int i = children.size() - 1; i >=0; i--) {
			GameNode child = children.get(i);

			if (child._onMouseExited(event) == false) {
				return false;
			}
		};
		
		Optional<GameNode> parent = this.parent;
		Point2D.Double translate = new Point2D.Double(0, 0);
		boolean result = true;

		// Compute world coordinate
		while (parent.isPresent()) {
			translate.x += parent.get().geometry.x;
			translate.y += parent.get().geometry.y;
			
			parent = parent.get().parent;
		}
		
		mouseBound.x += translate.x;
		mouseBound.y += translate.y;
		{
			if (isMouseEntered && !mouseBound.contains(point)) {
				isMouseEntered = false;
				result = onMouseExited(event);
			}
		}
		mouseBound.x -= translate.x;
		mouseBound.y -= translate.y;
		
		return result;
	}
	public final boolean _onKeyPressed(KeyEvent event) {
		for (int i = children.size() - 1; i >=0; i--) {
			GameNode child = children.get(i);

			if (child._onKeyPressed(event) == false) {
				return false;
			}
		};

		return onKeyPressed(event);
	}
	public final boolean _onKeyReleased(KeyEvent event) {
		for (int i = children.size() - 1; i >=0; i--) {
			GameNode child = children.get(i);

			if (child._onKeyReleased(event) == false) {
				return false;
			}
		};

		return onKeyReleased(event);
	}

	public boolean onMouseMoved(MouseEvent event) { return true; }
	public boolean onMousePressed(MouseEvent event) { return true; }
	public boolean onMouseReleased(MouseEvent event) { return true; }
	public boolean onMouseEntered(MouseEvent event) { return true; }
	public boolean onMouseExited(MouseEvent event) { return true; }
	
	public boolean onKeyPressed(KeyEvent event) { return true; }
	public boolean onKeyReleased(KeyEvent event) { return true; }
	
	public final void _onAreaEntered(GameNode node, long elapse) {
		if (!enteredAreaSet.contains(node)) {
			enteredAreaSet.add(node);
			onAreaEntered(node, elapse);
		}
	}
	public final void _onAreaExited(GameNode node, long elapse) {
		if (enteredAreaSet.contains(node)) {
			enteredAreaSet.remove(node);
			onAreaExited(node, elapse);
		}
	}

	// Physics event handlers
	public void onCollided(GameNode node, long elapse) {}
	public void onAreaEntered(GameNode node, long elapse) {}
	public void onAreaExited(GameNode node, long elapse) {}
	public boolean isAreaEntred() { return !enteredAreaSet.isEmpty(); }
	
	public final HashSet<Integer> colissionGroup() { return collisionGroupSet; }
	public final void addColissionGroup(int groupId) { collisionGroupSet.add(groupId); }
	
	public Optional<GameNode> parent() {
		return parent;
	}

	public void addChild(GameNode node) {
		if (node.parent.isPresent()) {
			GameNode parent = node.parent.get();
			parent.removeChild(node);
		}
		
		node.parent = Optional.of(this);
		children.add(node);
	}
	public boolean removeChild(GameNode node) {
		boolean childExists = children.remove(node);
		if (childExists) {
			node.parent = Optional.empty();
		}
		
		return childExists;
	}

	public final void _render(GraphicsContext gc) {
		gc.save();
		{
			gc.setGlobalAlpha(alpha);
			render(gc);

			gc.translate(geometry.getX(), geometry.getY());
			children.forEach(node -> {
				node._render(gc);
			});
		}
		gc.restore();
	}

	public final void _update(long elapse) {
		this.update(elapse);
		this.children.forEach(node -> node._update(elapse));
	}
	
	public void update(long elapse) {
			children.forEach(node -> node.update(elapse));
	}

	public void render(GraphicsContext gc) {}
	public void renderDebug(GraphicsContext gc) {
		gc.setStroke(Color.AQUA);
		gc.strokeRect(mouseBound.x, mouseBound.y, mouseBound.width, mouseBound.height);

		gc.save();
		{
			gc.translate(geometry.getX(), geometry.getY());
			children.forEach(node -> {
				node.renderDebug(gc);
			});
		}
		gc.restore();
	}
}
