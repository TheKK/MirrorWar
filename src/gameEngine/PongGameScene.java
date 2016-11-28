package gameEngine;

import java.lang.invoke.ConstantCallSite;
import java.util.HashSet;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class PongGameScene extends GameScene {
	Integer leftScore = 0;
	Integer rightScore = 0;

	public PongGameScene() {
		final Integer UPPER_BOUND_GROUP_ID = 0;
		final Integer LOWER_BOUND_GROUP_ID = 1;
		final Integer RIGHT_BOUND_GROUP_ID = 2;
		final Integer LEFT_BOUND_GROUP_ID = 3;
		final Integer LEFT_PADDLE_GROUP_ID = 4;
		final Integer RIGHT_PADDLE_GROUP_ID = 5;
		final Integer BALL_GROUP_ID = 6;

		final double PADDLE_X_MARGIN = 30;
		final double PADDLE_Y_START = 30;
		final double PADDLE_WIDTH = 30;
		final double PADDLE_HEIGHT = 100;
		final double PADDLE_MOVE_SPEED = 0.7;
		
		final double BALL_START_X = 300;
		final double BALL_START_Y = 300;
		final double BALL_SIZE = 25;
		final double BALL_START_VX = 0.4;
		final double BALL_START_VY = 0.4;

		Game.setClearColor(Color.BLACK);
		
		TextGameNode leftScoreLable = new TextGameNode(leftScore.toString());
		leftScoreLable.strokeColor = Color.WHITE;
		leftScoreLable.geometry.x = 300;
		leftScoreLable.geometry.y = 100;
		rootNode.addChild(leftScoreLable);

		TextGameNode rightScoreLabel = new TextGameNode(rightScore.toString());
		rightScoreLabel.strokeColor = Color.WHITE;
		rightScoreLabel.geometry.x = Game.canvasWidth() - 300;
		rightScoreLabel.geometry.y = 100;
		rootNode.addChild(rightScoreLabel);

		GameNode leftPaddle = new RectangleGameNode(
				PADDLE_X_MARGIN, PADDLE_Y_START,
				PADDLE_WIDTH, PADDLE_HEIGHT,
				Color.WHITE) {
			@Override
			public void update(long elapse) {
				vy = 0;
				if (Game.getKeyboardState(KeyCode.R)) vy -= PADDLE_MOVE_SPEED;
				if (Game.getKeyboardState(KeyCode.F)) vy += PADDLE_MOVE_SPEED;
			}
		};
		leftPaddle.addColissionGroup(LEFT_PADDLE_GROUP_ID);
		rootNode.addChild(leftPaddle);
		physicEngine.addDynamicNode(leftPaddle);

		GameNode rightPaddle = new RectangleGameNode(
				Game.canvasWidth() - PADDLE_X_MARGIN - PADDLE_WIDTH, PADDLE_Y_START,
				PADDLE_WIDTH, PADDLE_HEIGHT,
				Color.WHITE) {
			@Override
			public void update(long elapse) {
				vy = 0;
				if (Game.getKeyboardState(KeyCode.U)) vy -= PADDLE_MOVE_SPEED;
				if (Game.getKeyboardState(KeyCode.J)) vy += PADDLE_MOVE_SPEED;
			}
		};
		rightPaddle.addColissionGroup(RIGHT_PADDLE_GROUP_ID);
		rootNode.addChild(rightPaddle);
		physicEngine.addDynamicNode(rightPaddle);
		
		GameNode ball = new RectangleGameNode(BALL_START_X, BALL_START_Y, BALL_SIZE, BALL_SIZE, Color.WHITE) {
			{
				vx = BALL_START_VX;
				vy = BALL_START_VY;
				
				addColissionGroup(BALL_GROUP_ID);
			}
			
			void resetBall() {
				geometry.x = BALL_START_X;
				geometry.y = BALL_START_Y;
				vx = BALL_START_VX;
				vy = BALL_START_VY;
			}

			@Override
			public boolean onKeyPressed(KeyEvent event) {
				switch (event.getCode()) {
				case W:
					resetBall();
					break;
				default:
					break;
				}
				return true;
			}
			@Override
			public void onCollided(GameNode node, long elapse) {
				HashSet<Integer> collisionGroups = node.colissionGroup();

				if (collisionGroups.contains(LEFT_PADDLE_GROUP_ID)) vx = BALL_START_VX;
				if (collisionGroups.contains(RIGHT_PADDLE_GROUP_ID)) vx = -BALL_START_VX;
				if (collisionGroups.contains(UPPER_BOUND_GROUP_ID)) vy = BALL_START_VY;
				if (collisionGroups.contains(LOWER_BOUND_GROUP_ID)) vy = -BALL_START_VY;

				if (collisionGroups.contains(LEFT_BOUND_GROUP_ID)) {
					rightScore += 1;
					rightScoreLabel.text = rightScore.toString();
					resetBall();
				}
				if (collisionGroups.contains(RIGHT_BOUND_GROUP_ID)) {
					leftScore += 1;
					leftScoreLable.text = leftScore.toString();
					resetBall();
				}
			}
		};
		rootNode.addChild(ball);
		physicEngine.addDynamicNode(ball);

		RectangleGameNode button = new RectangleGameNode(0, 0, 50, 50, Color.RED) {
			public boolean onMousePressed(MouseEvent event) {
				Game.setClearColor(Color.web("0xcccccc"));
				Game.popScene();
				return false;
			}
		};
		rootNode.addChild(button);
		
		GameNode upperBound = new RectangleGameNode(0, -50, Game.canvasWidth(), 50, Color.TRANSPARENT);
		upperBound.addColissionGroup(UPPER_BOUND_GROUP_ID);
		physicEngine.addStaticNode(upperBound);

		GameNode lowerBound = new RectangleGameNode(0, Game.canvasHeight(), Game.canvasWidth(), 50, Color.TRANSPARENT);
		lowerBound.addColissionGroup(LOWER_BOUND_GROUP_ID);
		physicEngine.addStaticNode(lowerBound);

		GameNode rightBound = new RectangleGameNode(Game.canvasWidth(), 0, 50, Game.canvasHeight(), Color.TRANSPARENT);
		rightBound.addColissionGroup(RIGHT_BOUND_GROUP_ID);
		physicEngine.addStaticNode(rightBound);

		GameNode leftBound = new RectangleGameNode(-50, 0, 50, Game.canvasHeight(), Color.TRANSPARENT);
		leftBound.addColissionGroup(LEFT_BOUND_GROUP_ID);
		physicEngine.addStaticNode(leftBound);
	}
}