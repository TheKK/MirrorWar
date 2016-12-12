package netGameNodeSDK;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import gameEngine.GameNode;
import gameEngine.GameScene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;

public abstract class NetGameNode<UPDATE, INPUT> extends GameNode {
	public List<INPUT> inputQueue = Collections.synchronizedList(new ArrayList<>());
	public List<UPDATE> updateQueue = Collections.synchronizedList(new ArrayList<>());

	public boolean isControlling = false;

	protected Consumer<Long> updateFunc = elapse -> {};
	protected Consumer<GraphicsContext> renderFunc = gc -> {};
	protected Function<KeyEvent, Boolean> onKeyPressedFunc = key -> { return true; };
	protected Function<KeyEvent, Boolean> onKeyReleasedFunc = key -> { return true; };

	abstract protected void clientInitialize(GameScene scene);
	abstract protected void serverInitialize(GameScene scene, boolean debugMode);

	abstract protected void clientUpdate(long elapse);
	abstract protected void serverUpdate(long elapse);

	abstract protected void clientHandleServerUpdate(UPDATE update);
	abstract protected void serverHandleClientInput(INPUT input);

	abstract public UPDATE getStates();

	@Override
	public final void update(long elapse) {
		updateFunc.accept(elapse);
	}

	@Override
	public final void render(GraphicsContext gc) {
		renderFunc.accept(gc);
	}

	@Override
	public final boolean onKeyPressed(KeyEvent event) {
		if (isControlling) {
			return onKeyPressedFunc.apply(event);
		} else {
			return true;
		}
	}

	@Override
	public final boolean onKeyReleased(KeyEvent event) {
		if (isControlling) {
			return onKeyReleasedFunc.apply(event);
		} else {
			return true;
		}
	}
}