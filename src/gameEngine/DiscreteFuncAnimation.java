package gameEngine;

import java.util.ArrayList;
import java.util.function.Consumer;

public final class DiscreteFuncAnimation<T> implements Animation {
	class TimeValueTuple {
		long time;
		T value;
		
		TimeValueTuple(long time, T value) {
			this.time = time;
			this.value = value;
		}
	}

	ArrayList<TimeValueTuple> timeValueList = new ArrayList<>();
	Consumer<T> appliedFunc;

	public DiscreteFuncAnimation(Consumer<T> func) {
		appliedFunc = func;
	}

	public void addAnchor(long time, T value) {
		// TODO Remove duplicate anchor with same time stamp
		timeValueList.add(new TimeValueTuple(time, value));
		timeValueList.sort((a, b) -> (a.time < b.time) ? 1 : -1);
	}

	@Override
	public void atTime(long time) {
		for (TimeValueTuple e: timeValueList) {
			if (time >= e.time) {
				appliedFunc.accept(e.value);
				return;
			}
		}
	}

	@Override
	public void reset() {
	}
}