package gameEngine;

public interface TransitionFuncs {
	public enum TransitionType {
		LINEAR(0),
		SIN(1),
		QUADRATIC(2),
		;

		private final int val;
		
		private TransitionType(int val) {
			this.val = val;
		}
		
		public int getValue() {
			return val;
		}
	}

	public enum EaseType {
		IN(0), OUT(1), IN_OUT(2);

		private final int val;
		
		private EaseType(int val) {
			this.val = val;
		}
		
		public int getValue() {
			return val;
		}
	}

	double in(double t, double b, double c, double d);
	double out(double t, double b, double c, double d);
	double inOut(double t, double b, double c, double d);

	public final class LinearTransitionFuncs implements TransitionFuncs {
		public double in(double t, double b, double c, double d) {
			return c * t / d + b;
		}
		public double out(double t, double b, double c, double d) {
			return c * t / d + b;
		}
		public double inOut(double t, double b, double c, double d) {
			return c * t / d + b;
		}
	}

	public final class SinusoidalTransitionFuncs implements TransitionFuncs {
		public double in(double t, double b, double c, double d) {
			return -c * Math.cos(t / d * (Math.PI / 2)) + c + b;	
		}
		public double out(double t, double b, double c, double d) {
			return c * Math.sin(t / d * (Math.PI / 2)) + b;
		}
		public double inOut(double t, double b, double c, double d) {
			return (-c / 2) * (Math.cos(Math.PI * t / d) - 1) + b;
		}
	}

	public final class  QuadraticTransitionFuncs implements TransitionFuncs {
		public double in(double t, double b, double c, double d) {
			t /= d;
			return (c * t * t) + b;
		}
		public double out(double t, double b, double c, double d) {
			t /= d;
			return (-c * t * (t - 2.)) + b;
		}
		public double inOut(double t, double b, double c, double d) {
			t /= (d / 2.);
			if (t < 1.) {
				return (c / 2. * t * t) + b;
			} else {
				t--;
				return (-c / 2. * (t * (t - 2.) - 1.)) + b;
			}
		}
	}
}
