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
		while (playedTime >= totalLength) {
			playedTime -= totalLength;
			
			// When maximumLoopCount less than zero, loop forever
			if (maximumLoopCount >= 0) {
				currentLoopCount += 1;
				if (currentLoopCount >= maximumLoopCount) {
					isPlaying = false;
				}
			}
		}
		
		animationsMap.values().forEach(ani -> {
			ani.atTime(playedTime);
		});
	}
	
	public void play(long loopCount) {
		maximumLoopCount = loopCount;
		isPlaying = true;
	}
	
	public void stop() {
		isPlaying = false;
	}
	
	public boolean isPlaying() {
		return isPlaying;
	}

	public void addAnimation(String name, Animation ani) {
		animationsMap.put(name, ani);
	}

	public Optional<Animation> animation(String name) {
		Animation result = animationsMap.get(name);

		return (result == null) ? Optional.empty() : Optional.of(result);
	}
}
