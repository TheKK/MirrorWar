package gameEngine;

import java.util.ArrayList;
import java.util.function.Consumer;

import gameEngine.TransitionFuncs.EaseType;
import gameEngine.TransitionFuncs.TransitionType;
import gameEngine.TransitionFuncs.LinearTransitionFuncs;
import gameEngine.TransitionFuncs.SinusoidalTransitionFuncs;
import gameEngine.TransitionFuncs.QuadraticTransitionFuncs;

public class ContinuousFuncAnimation<T extends Number> implements Animation {

	class Anchor {
		long time;
		T value;
		TransitionType transType;
		EaseType easeType;
		 
		Anchor(long time, T value, TransitionType transType, EaseType easeType) {
			this.time = time;
			this.value = value;
			this.transType = transType;
			this.easeType = easeType;
		}
	}


	Consumer<Double> appliedFunc = null;
	TransitionFuncs [] transitionFuncs = null;
	ArrayList<Anchor> timeValueList = new ArrayList<>();

	public ContinuousFuncAnimation(Consumer<Double> func) {
		appliedFunc = func;
		transitionFuncs = getTransitionFuncsArray();
	}

	public void addAnchor(long time, T value, TransitionType transType, EaseType easeType) {
		timeValueList.add(new Anchor(time, value, transType, easeType));
		timeValueList.sort((a, b) -> (a.time < b.time) ? 1 : -1);
	}

	@Override
	public void atTime(long time) {
		Anchor startPoint = null, endPoint = null;

		for (int i = 0; i < timeValueList.size(); ++i) {
			Anchor e = timeValueList.get(i);

			if (time >= e.time) {
				// If it locates after our last anchor, use that value directly
				if (i == 0) {
					appliedFunc.accept(e.value.doubleValue());
					return;
				}

				startPoint = e;
				endPoint = timeValueList.get(i - 1);

				break;
			}
		}
		
		TransitionType transType = startPoint.transType;
		EaseType easeType = startPoint.easeType;

		double t = time - startPoint.time;
		double b = startPoint.value.doubleValue();
		double d = endPoint.time - startPoint.time;
		double c = endPoint.value.doubleValue() - startPoint.value.doubleValue();

		double val = 0;
		TransitionFuncs transFuncs = transitionFuncs[transType.getValue()];
		
		switch (easeType) {
		case IN:
			val = transFuncs.in(t, b, c, d);
			break;
		case OUT:
			val = transFuncs.out(t, b, c, d);
			break;
		case IN_OUT:
			val = transFuncs.inOut(t, b, c, d);
			break;
		default:
			System.err.println("Unknown ease type!");
			break;
		}

		appliedFunc.accept(val);
	}

	// TODO Can we place this function inside TransitionFuncs.java ?
	private static TransitionFuncs[] getTransitionFuncsArray() {
		TransitionFuncs[] result = new TransitionFuncs[TransitionType.values().length];
		
		// TODO Is there any way that I can store static function instead instance ?
		result[TransitionType.LINEAR.getValue()] = new LinearTransitionFuncs();
		result[TransitionType.SIN.getValue()] = new SinusoidalTransitionFuncs();
		result[TransitionType.QUADRATIC.getValue()] = new QuadraticTransitionFuncs();
		
		for (TransitionFuncs func: result) {
			if (func == null) {
				throw new AssertionError("Missing transition function!");
			}

			// TODO Find out how to turn assert on
//			assert func != null;
		}
		
		return result;
	}
}