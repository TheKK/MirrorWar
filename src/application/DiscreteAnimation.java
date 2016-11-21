package application;

import java.util.LinkedList;
import java.util.Optional;

public class DiscreteAnimation<T> implements Animation<T> {

	static public class Variant {
		public enum ValueType {
			DOUBLE, BOOLEAN
		}

		ValueType valueType;
		double doubleVal;
		boolean booleanVal;

//		public static <T> Variant of(T value) throws Exception {
//			Variant result = new Variant();
//			Class<?> valueClass = value.getClass();
//			
//			if (valueClass == boolean.class) {
//				result.valueType = ValueType.BOOLEAN;
//				result.booleanVal = (boolean) value;
//			} else if (valueClass == double.class) {
//				result.valueType = ValueType.DOUBLE;
//				result.doubleVal = (double) value;
//			} else {
//				throw new Exception("non support variant type: " + valueClass);
//			}
//
//			return result;
//		}
		
		public Optional<Boolean> asBoolean() {
			switch (this.valueType) {
			case BOOLEAN:
				return Optional.of(booleanVal);
			default:
				return Optional.empty();
			}
		}

		public Optional<Double> asDouble() {
			switch (this.valueType) {
			case DOUBLE:
				return Optional.of(doubleVal);
			default:
				return Optional.empty();
			}
		}
	}

	class TimeValueTuple<T> {
		long time;
		T value;
		
		TimeValueTuple(long time, T value) {
			this.time = time;
			this.value = value;
		}
	}

	LinkedList<TimeValueTuple<T>> timeValueList;
	T target;

	public void addAnchor(long time, T value) {
		for (int i = 0; i < timeValueList.size(); ++i) {
			TimeValueTuple<T> tuple = timeValueList.get(i);

			if (time == tuple.time) {
				tuple.time = time;
				tuple.value = value;
				break;

			} else if (time > tuple.time) {
				timeValueList.add(i, new TimeValueTuple<T>(time, value));
				break;

			} else if (time < tuple.time) {
				timeValueList.add(i - 1, new TimeValueTuple<T>(time, value));
				break;
			}
		}
	}

	@Override
	public void atTime(long time) {
		target = timeValueList.getFirst().value;
	}
}