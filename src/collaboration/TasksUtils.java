package collaboration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class TasksUtils {
	
	public static Collection<Skill> intersectWithAgentSkills(Agent agent,
			Collection<Skill> skills) {
		List<Skill> result = new ArrayList<Skill>();
		for (Skill skill : skills) {
			if (agent.getSkills().contains(skill)) {
				result.add(skill);
			}
		}
		return result;
	}
	
	/**
	 * This method counts the frequency of a 'Task' in HashMap<Skill,
	 * ArrayList<Task>> and returns collection of tasks and their frequencies
	 * 
	 * @param h
	 *            - HashMap of skills and tasks which require them
	 * @return HashMap<Task, Integer>
	 */
	public static HashMap<Task, Integer> searchForIntersection(
			HashMap<Skill, ArrayList<Task>> h) {

		HashMap<Task, Integer> map = new HashMap<Task, Integer>();
		for (Skill skill : h.keySet()) {
			ArrayList<Task> t = h.get(skill);
			for (Task task : t) {
				if (map.containsKey(task)) {
					map.put(task, map.get(task) + 1);
				} else {
					map.put(task, 0);
				}
			}
		}
		return map;
	}


}
