package gameEngine;

import java.util.ArrayList;

public final class FunctionTriggerAnimation implements Animation {
	class Anchor {
		long time;
		Runnable func;
		boolean ran = false;
		
		Anchor(long time, Runnable func) {
			this.time = time;
			this.func = func;
		}
	}

	ArrayList<Anchor> anchors = new ArrayList<>();

	public void addAnchor(long time, Runnable func) {
		anchors.add(new Anchor(time, func));
		anchors.sort((a, b) -> (a.time < b.time) ? 1 : -1);
	}

	@Override
	public void atTime(long time) {
		for (Anchor e: anchors) {
			if (!e.ran && time >= e.time) {
				e.ran = true;
				e.func.run();
				return;
			}
		}
	}

	@Override
	public void reset() {
		for (Anchor e: anchors) {
			e.ran = false;
		}
	}
}
