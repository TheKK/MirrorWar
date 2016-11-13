package application;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Stack;

import com.sun.glass.events.MouseEvent;
import com.sun.glass.ui.TouchInputSupport;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;

public final class Game extends Application {
	static Game instance;

	static double width, height;
	static Point2D.Double mousePos = new Point2D.Double(0, 0);

	static Canvas canvas;
	static GraphicsContext gc;
	static Color clearColor = Color.web("0xcccccc");

	static Class<? extends GameScene> firstGameScene;
	static Stack<GameScene> gameSceneStack = new Stack<GameScene>();
	
	static boolean isClickBoundDebugMode = false;
	static boolean isPhysicEngineDebugMode = false;
	
	public Game() {
		assert(instance == null);
		instance = this;

		width = 800;
		height = 450;
	}
	
	public static double canvasWidth() {
		return canvas.getWidth();
	}
	
	public static double canvasHeight() {
		return canvas.getHeight();
	}

	public static void swapScene(GameScene scene) {
		gameSceneStack.pop();
		gameSceneStack.push(scene);
	}

	public static void pushScene(GameScene scene) {
		gameSceneStack.push(scene);
	}

	public static void popScene() {
		gameSceneStack.pop();
	}
	
	public static Point2D.Double getMousePos() {
		return new Point2D.Double(mousePos.x, mousePos.y);
	}
	
	public static Color clearColor() { return clearColor; }
	public static void setClearColor(Color color) { clearColor = color; }
	
	private void startGameLoop() {
		canvas.setOnMouseMoved(event -> {
			mousePos.setLocation(event.getX(), event.getY());

			gameSceneStack.peek().onMouseMoved(event);
			gameSceneStack.peek().onMouseEntered(event);
			gameSceneStack.peek().onMouseExited(event);
		});
		canvas.setOnMouseDragged(event -> {
			mousePos.setLocation(event.getX(), event.getY());

			gameSceneStack.peek().onMouseMoved(event);
			gameSceneStack.peek().onMouseEntered(event);
			gameSceneStack.peek().onMouseExited(event);
		});
		canvas.setOnMousePressed(event -> {
			gameSceneStack.peek().onMousePressed(event);
		});
		canvas.setOnMouseReleased(event -> {
			gameSceneStack.peek().onMouseReleased(event);
		});
		canvas.setOnKeyPressed(event -> {
			gameSceneStack.peek().onKeyPressed(event);
		});
		canvas.setOnKeyReleased(event -> {
			gameSceneStack.peek().onKeyReleased(event);
		});

		new AnimationTimer() {
			long prevTime = System.nanoTime();

			@Override
			public void handle(long now) {
				if (gameSceneStack.isEmpty()) {
					prevTime = now;
					return;
				}

				long elapse = (now - prevTime) / ((long) 1000000.0);
				GameScene currentGameScene = gameSceneStack.peek();
				
				currentGameScene.physicEngineUpdate(elapse);
				currentGameScene.update(elapse);
				
				gc.setFill(clearColor);
				gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

				currentGameScene.render(gc);
				if (isClickBoundDebugMode) currentGameScene.renderDebug(gc);
				if (isPhysicEngineDebugMode) currentGameScene.renderPhysicEngineDebug(gc);
				
				prevTime = now;
			}
		}.start();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			Canvas canvas = new Canvas();
			canvas.setWidth(width);
			canvas.setHeight(height);
			
			Game.canvas = canvas;
			Game.gc = canvas.getGraphicsContext2D();

			Group root = new Group();
			root.getChildren().add(canvas);

			Scene scene = new Scene(root, width, height, Color.BLACK);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			
			canvas.requestFocus();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		Game.pushScene(Game.firstGameScene.newInstance());
		startGameLoop();
	}
	
	public static void run(Class<? extends GameScene> firstGameScene) {
		Game.firstGameScene = firstGameScene;

		launch();
	}
}