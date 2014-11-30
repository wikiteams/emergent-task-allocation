package argonauts;

import collaboration.Skill;
import collaboration.Task;
import collaboration.TaskInternals;
import collaboration.Tasks;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import repast.simphony.context.Context;
import logger.PjiitOutputter;

public class PersistAdvancement {

	public static HashMap<Skill, SortedMap<Double, Task>> advance = 
			new HashMap<Skill, SortedMap<Double, Task>>();

	public static void reportTaskInternal(Task task, List<Skill> skills){
		for (Skill skill : skills) {
			if (advance.containsKey(skill)) {
				SortedMap<Double, Task> sm = advance.get(skill);
				if (sm.containsValue(task)){
					sm.remove(getKeyByValue(sm, task));
					sm.put(task.getSimplifiedAdvance(skill), task);
				} else {
					sm.put(task.getSimplifiedAdvance(skill), task);
				}
				advance.put(skill, sm);
			} else {
				SortedMap<Double, Task> sm = new TreeMap<Double, Task>(
						new Comparator<Double>() {
							public int compare(Double o1, Double o2) {
								say(o1 + " compared to " + o2 + " returns " + (-o1.compareTo(o2)));
								return -o1.compareTo(o2);
							}
						});
				sm.put(task.getSimplifiedAdvance(skill), task);
				advance.put(skill, sm);
			}
		}
	}
	
	public static void reportTask(Task task) {
		for (TaskInternals ti : task.getTaskInternals().values()) {
			if (advance.containsKey(ti.getSkill())) {
				SortedMap<Double, Task> sm = advance.get(ti.getSkill());
				if (sm.containsValue(task)){
					sm.remove(getKeyByValue(sm, task));
					sm.put(task.getGeneralAdvance(), task);
				} else {
					sm.put(task.getGeneralAdvance(), task);
				}
				advance.put(ti.getSkill(), sm);
			} else {
				SortedMap<Double, Task> sm = new TreeMap<Double, Task>(
						new Comparator<Double>() {
							public int compare(Double o1, Double o2) {
								say(o1 + " compared to " + o2 + " returns " + (-o1.compareTo(o2)));
								return -o1.compareTo(o2);
							}
						});
				sm.put(task.getGeneralAdvance(), task);
				advance.put(ti.getSkill(), sm);
			}
		}
	}
	
	/**
	 * Usually launched once before start of a run
	 * @param taskPool
	 */
	public static void calculateAll(Tasks taskPool){
		Collection<Task> t = taskPool.getTasks();
		for(Task task : t){
			reportTask(task);
		}
	}
	
	public static Object[] getMostAdvanced(Skill skill){
		SortedMap<Double, Task> sm = advance.get(skill);
		if (sm == null){
			// no such skill in a table
			return new Object[]{-1.0, null};
		}
		if (sm.size() < 1){
			// empty table
			return new Object[]{-1.0, null};
		}
		say ("sm.firstKey() is " + sm.firstKey());
		return new Object[]{sm.firstKey(), sm.get(sm.firstKey())};
	}
	
	public static int size(){
		return advance.size();
	}
	
	public static void clear(){
		advance.clear();
	}
	
	public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
	    for (Entry<T, E> entry : map.entrySet()) {
	        if (value.equals(entry.getValue())) {
	            return entry.getKey();
	        }
	    }
	    assert false; // should never happen
	    return null;
	}
	
	public static void say(String s) {
		PjiitOutputter.say(s);
	}

}
