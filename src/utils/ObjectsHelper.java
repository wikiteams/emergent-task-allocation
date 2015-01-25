package utils;

import java.util.Map;
import java.util.Map.Entry;

import repast.simphony.random.RandomHelper;
import strategies.Strategy.TaskChoice;
import collaboration.Agent;

public class ObjectsHelper {

	public static final Double notApplicable = -1.0;

	public static <T> T randomFrom(T... items) {
		return items[RandomHelper.nextIntFromTo(0, items.length - 1)];
	}

	public static Integer fromDouble(double value) {
		return (int) value;
	}
	
	public static Boolean isSecondEqual(Map m1, Map m2) {
		if (m2 == null) {
			return true;
		} else {
			if (m1.hashCode() == m2.hashCode()) {
				return true;
			} else {
				return false;
			}
		}
	}

	public static Boolean is2ndHigher(Double d1, Double d2) {
		if (d1 == null) {
			return true;
		} else {
			if (d2 > d1) {
				return true;
			} else {
				return false;
			}
		}
	}

	public static Boolean is2ndLower(Double d1, Double d2) {
		if (d1 == null) {
			return true;
		} else {
			if (d2 < d1) {
				return true;
			} else {
				return false;
			}
		}
	}

	public static Boolean is2ndHigher(Long d1, Long d2) {
		if (d1 == null) {
			return true;
		} else {
			if (d2 > d1) {
				return true;
			} else {
				return false;
			}
		}
	}

	public static Boolean isHigherThanMapEntries(
			Map<Agent, Double> measurements, Object agent, Double highestValue) {
		if (measurements.get(agent) == null) {
			return true;
		} else {
			if (highestValue > measurements.get(agent)) {
				return true;
			} else {
				return false;
			}
		}
	}

	public static TaskChoice getProbKey(double nextDoubleFromTo,
			Map<TaskChoice, Double> p) {
		TaskChoice result = null;
		for (Entry<TaskChoice, Double> entrySet : p.entrySet()) {
			if (nextDoubleFromTo <= entrySet.getValue()) {
				result = entrySet.getKey();
			}
		}
		return result;
	}
}
