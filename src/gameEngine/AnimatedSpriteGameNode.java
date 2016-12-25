package gameEngine;

import java.awt.geom.Rectangle2D;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class AnimatedSpriteGameNode extends GameNode {
	static class Frame {
		Rectangle2D.Double clilp;
		long duration;
	}

	Image image;

	ArrayList<Frame> frameClips = new ArrayList<>();

	long frameDurationRemains = 0;
	Integer currentFrame = 0;

	private int loopCountRemains = -1;

	public boolean autoPlayed = true;

	public boolean flipVertical = false;
	public boolean flipHorizontal = false;

	// TODO It should be possible to load image from path which provided by JSON file
	public static AnimatedSpriteGameNode fromAsepriteJson(Image image, String filePath) throws FileNotFoundException {
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(new FileReader(filePath));

		JsonObject aseprite = element.getAsJsonObject();
		JsonArray asepriteFrames = aseprite.get("frames").getAsJsonArray();

		AnimatedSpriteGameNode result = new AnimatedSpriteGameNode(image, 0, 0);

		for (JsonElement asepriteFrame: asepriteFrames) {
			JsonObject frame = asepriteFrame.getAsJsonObject()
					.get("frame").getAsJsonObject();

			int x = frame.get("x").getAsInt();
			int y = frame.get("y").getAsInt();
			int width = frame.get("w").getAsInt();
			int height = frame.get("h").getAsInt();

			int duration = asepriteFrame.getAsJsonObject()
					.get("duration").getAsInt();

			result.addFrame(new Rectangle2D.Double(x, y, width, height), duration);
		}

		if (!result.frameClips.isEmpty()) {
			Rectangle2D.Double clip = result.frameClips.get(0).clilp;

			result.geometry.width = clip.width;
			result.geometry.height = clip.height;
		}

		return result;
	}

	public AnimatedSpriteGameNode(Image image, int width, int height) {
		this.image = image;

		this.geometry.width = width;
		this.geometry.height = height;
	}

	public void play(int loops) {
		loopCountRemains = loops;
		autoPlayed = true;
	}

	public void playFromStart(int loops) {
		play(loops);

		currentFrame = 0;
	}

	public void addFrame(Rectangle2D.Double frameClip, long displayPeriod) {
		Frame newFrame = new Frame();

		newFrame.clilp = frameClip;
		newFrame.duration = displayPeriod;

		frameClips.add(newFrame);
	}

	public void setFrameIndex(int index) throws Exception {
		if (index <= frameClips.size()) {
			throw new Exception("frame index out of bound!");
		}

		currentFrame = index;
	}

	public void nextFrame() {
		currentFrame += 1;
		if (currentFrame >= frameClips.size()) {
			currentFrame = 0;

			if (loopCountRemains > 0) {
				loopCountRemains -= 1;
			}
		}

		frameDurationRemains += frameClips.get(currentFrame).duration;
	}

	@Override
	public void update(long elapse) {
		if (!autoPlayed || (loopCountRemains == 0)) {
			return;
		}

		frameDurationRemains -= elapse;

		while (frameDurationRemains < 0) {
			nextFrame();
		}
	}

	@Override
	public void render(GraphicsContext gc) {
		Rectangle2D clipRect = frameClips.get(currentFrame).clilp;

		double dx = 0;
		double dy = 0;
		double dw = geometry.width;
		double dh = geometry.height;

		if (flipVertical) {
			dx += Math.abs(dw);
			dw = -Math.abs(dw);
		}

		if (flipHorizontal) {
			dy += Math.abs(dh);
			dh = -Math.abs(dh);
		}

		gc.drawImage(
				image,
				clipRect.getX(), clipRect.getY(), clipRect.getWidth(), clipRect.getHeight(),
				dx, dy, dw, dh);
	}
}