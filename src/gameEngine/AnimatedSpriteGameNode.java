package gameEngine;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Optional;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class AnimatedSpriteGameNode extends GameNode {
	Image image;

	ArrayList<Long> timeSegments = new ArrayList<>();
	ArrayList<Rectangle2D.Double> frameClips = new ArrayList<>();

	long currentTimeElapse = 0;
	Integer currentFrame = 0;

	public boolean autoPlayed = true;
	public Optional<Rectangle2D.Double> clip = Optional.empty();

	public AnimatedSpriteGameNode(Image image, int width, int height) {
		this.image = image;
		this.timeSegments.add(0L);

		this.geometry.width = width;
		this.geometry.height = height;
	}

	public void addFrame(Rectangle2D.Double frameClip, long displayPeriod) {
		Long lastTimeSegment = timeSegments.get(timeSegments.size() - 1);

		frameClips.add(frameClip);
		timeSegments.add(lastTimeSegment + displayPeriod);
	}

	public void setFrameIndex(int index) throws Exception {
		if (index <= timeSegments.size()) {
			throw new Exception("frame index out of bound!");
		}

		currentFrame = index;
	}

	public void nextFrame() {
		currentFrame += 1;
		if (currentFrame >= frameClips.size()) {
			currentFrame = 0;
		}

		currentTimeElapse = timeSegments.get(currentFrame);
	}

	public void previousFrame() {
		currentFrame -= 1;
		if (currentFrame <= 0) {
			currentFrame = frameClips.size() - 1;
		}

		currentTimeElapse = timeSegments.get(currentFrame);
	}

	@Override
	public void update(long elapse) {
		// FIXME This is just a stub, remove all of this when Animation was done
		if (!autoPlayed) {
			return;
		}

		currentTimeElapse += elapse;

		long timeOffset = currentTimeElapse - timeSegments.get(currentFrame + 1);

		if (timeOffset >= 0) {
			nextFrame();
			currentTimeElapse += timeOffset;
		}
	}

	@Override
	public void render(GraphicsContext gc) {
		Rectangle2D clipRect = frameClips.get(currentFrame);

		if (clip.isPresent()) {
			Rectangle2D.Double userClip = (Rectangle2D.Double) clip.get().clone();
			userClip.x += clipRect.getX();
			userClip.y += clipRect.getY();

			clipRect = clipRect.createIntersection(userClip);
		}

		gc.drawImage(
				image,
				clipRect.getX(), clipRect.getY(), clipRect.getWidth(), clipRect.getHeight(),
				0, 0, geometry.width, geometry.height);
	}
}