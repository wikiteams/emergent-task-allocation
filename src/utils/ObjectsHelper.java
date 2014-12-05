package utils;

import java.util.Map;

import collaboration.Agent;

public class ObjectsHelper {
	
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

	public static Boolean isHigherThanMapEntries(Map<Agent, Double> measurements,
			Object agent, Double highestValue) {
		if (measurements.get(agent) == null){
			return true;
		} else {
			if (highestValue > measurements.get(agent)){
				return true;
			} else {
				return false;
			}
		}
	}
}
