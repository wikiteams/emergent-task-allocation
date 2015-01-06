package github;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import collaboration.Experience;
import collaboration.Skill;

public class AgentModelingUtils {

	protected static HashMap<Skill, Experience> getByIndexWithIterate(
			Map<String, HashMap<Skill, Experience>> hMap, int index) {
		int z = 0;
		HashMap<Skill, Experience> found = null;
		for (Map.Entry<String, HashMap<Skill, Experience>> entry : hMap
				.entrySet()) {
			HashMap<Skill, Experience> value = entry.getValue();
			if (z++ == index) {
				found = value;
			}
		}
		return found;
	}

	protected static HashMap<Skill, Experience> getByIndexArrayTransform(
			LinkedHashMap<String, HashMap<Skill, Experience>> hMap, int index) {
		return (HashMap<Skill, Experience>) hMap.values().toArray()[index];
	}

	public static HashMap<Skill, Experience> choseRandom(
			LinkedHashMap<String, HashMap<Skill, Experience>> skillSet) {
		Random generator = new Random();
		int i = generator.nextInt(skillSet.size());
		return getByIndexArrayTransform(skillSet, i);
	}

}
