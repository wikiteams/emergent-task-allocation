package github;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

import collaboration.Experience;
import collaboration.Skill;

public class AgentModelingUtils {

	protected static HashMap<Skill, Experience> getByIndex(
			LinkedHashMap<String, HashMap<Skill, Experience>> hMap, int index) {
		return (HashMap<Skill, Experience>) hMap.values().toArray()[index];
	}

	public static HashMap<Skill, Experience> choseRandom(
			LinkedHashMap<String, HashMap<Skill, Experience>> skillSet) {
		Random generator = new Random();
		int i = generator.nextInt(skillSet.size());
		return getByIndex(skillSet, i);
	}

}
