package gameEngine;

import java.util.HashMap;
import java.util.Optional;

public class AnimationPlayer extends GameNode {
	enum PlayMode {
		NORMAL,
		PING_PONG,
	};

	boolean isPlaying = false;
	long totalLength = 0;
	long playedTime = 0;

	long maximumLoopCount = 0;
	long currentLoopCount = 0;

	HashMap<String, Animation> animationsMap = new HashMap<>();
	
	public AnimationPlayer(long animationLength) {
		totalLength = animationLength;
	}
	
	@Override
	public void update(long elapse) {
		if (!isPlaying) {
			return;
		}
		
		playedTime += elapse;
		
		animationsMap.values().forEach(ani -> {
			ani.atTime(playedTime);
		});

		while (playedTime >= totalLength) {
			playedTime -= totalLength;

			animationsMap.values().forEach(ani -> {
				ani.reset();
			});
			
			// When maximumLoopCount less than zero, loop forever
			if (maximumLoopCount >= 0) {
				currentLoopCount += 1;
				if (currentLoopCount >= maximumLoopCount) {
					isPlaying = false;
				}
			}
		}
	}
	
	public void play(long loopCount) {
		maximumLoopCount = loopCount;
		isPlaying = true;
	}
	
	public void playFromStart(long loopCount) {
		playedTime = 0;

		play(loopCount);
	}
	
	public void pause() {
		isPlaying = false;
	}
	
	public void stop() {
		playedTime = 0;
		isPlaying = false;
	}
	
	public boolean isPlaying() {
		return isPlaying;
	}
	
	public long totalLength() {
		return totalLength;
	}

	public void addAnimation(String name, Animation ani) {
		animationsMap.put(name, ani);
	}

	public Optional<Animation> animation(String name) {
		Animation result = animationsMap.get(name);

		return (result == null) ? Optional.empty() : Optional.of(result);
	}
}
