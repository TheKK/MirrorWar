package mirrorWar;

import gameEngine.AnimationPlayer;
import gameEngine.ContinuousFuncAnimation;
import gameEngine.FunctionTriggerAnimation;
import gameEngine.Game;
import gameEngine.GameNode;
import gameEngine.SpriteGameNode;
import gameEngine.TransitionFuncs.EaseType;
import gameEngine.TransitionFuncs.TransitionType;
import javafx.scene.media.MediaPlayer;

public class CountdownGameNode extends GameNode {
	private SpriteGameNode one = new SpriteGameNode(Game.loadImage("./src/mirrorWar/pic/mirrorOne.png"));
	private SpriteGameNode two = new SpriteGameNode(Game.loadImage("./src/mirrorWar/pic/mirrorTwo.png"));
	private SpriteGameNode three = new SpriteGameNode(Game.loadImage("./src/mirrorWar/pic/mirrorThree.png"));
	private SpriteGameNode go = new SpriteGameNode(Game.loadImage("./src/mirrorWar/pic/mirrorGo.png"));

	private MediaPlayer countDownSe = new MediaPlayer(Game.loadMedia("./src/mirrorWar/sounds/mirrorCountdown.wav"));
	private MediaPlayer goSe = new MediaPlayer(Game.loadMedia("./src/mirrorWar/sounds/mirrorGo.wav"));

	public CountdownGameNode() {
		enable = false;

		geometry.width = one.geometry.width;
		geometry.height = one.geometry.height;

		AnimationPlayer aniPlayer = new AnimationPlayer(4000);
		aniPlayer.play(1);
		addChild(aniPlayer);

		FunctionTriggerAnimation funcAni = new FunctionTriggerAnimation();
		funcAni.addAnchor(0, () -> {
			countDownSe.stop();
			countDownSe.play();

			addChild(three);
		});
		funcAni.addAnchor(1000, () -> {
			countDownSe.stop();
			countDownSe.play();

			three.visible = false;
			addChild(two);
		});
		funcAni.addAnchor(2000, () -> {
			countDownSe.stop();
			countDownSe.play();

			two.visible = false;
			addChild(one);
		});
		funcAni.addAnchor(3000, () -> {
			countDownSe.stop();

			goSe.play();

			one.visible = false;
			addChild(go);
		});

		ContinuousFuncAnimation<Double> contiAni = new ContinuousFuncAnimation<>(val -> {
			one.scaleY = two.scaleY = three.scaleY = go.scaleY = val;
		});
		contiAni.addAnchor(0, 0.0, TransitionType.SIN, EaseType.IN_OUT);
		contiAni.addAnchor(150, 1.0, TransitionType.SIN, EaseType.IN_OUT);

		contiAni.addAnchor(1000, 1.0, TransitionType.SIN, EaseType.IN_OUT);
		contiAni.addAnchor(1001, 0.0, TransitionType.SIN, EaseType.IN_OUT);
		contiAni.addAnchor(1150, 1.0, TransitionType.SIN, EaseType.IN_OUT);

		contiAni.addAnchor(2000, 1.0, TransitionType.SIN, EaseType.IN_OUT);
		contiAni.addAnchor(2001, 0.0, TransitionType.SIN, EaseType.IN_OUT);
		contiAni.addAnchor(2150, 1.0, TransitionType.SIN, EaseType.IN_OUT);

		contiAni.addAnchor(3000, 1.0, TransitionType.SIN, EaseType.IN_OUT);
		contiAni.addAnchor(3001, 0.0, TransitionType.SIN, EaseType.IN_OUT);
		contiAni.addAnchor(3150, 1.0, TransitionType.SIN, EaseType.IN_OUT);

		funcAni.addAnchor(aniPlayer.totalLength(), () -> {
			detachFromParent();
		});

		aniPlayer.addAnimation("funcAni", funcAni);
		aniPlayer.addAnimation("contiAni", contiAni);
	}
}
