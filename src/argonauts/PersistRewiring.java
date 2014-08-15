package argonauts;

import collaboration.Agent;
import collaboration.Task;
import collaboration.TaskInternals;

import java.util.HashMap;
import java.util.Map;

public class PersistRewiring {
	
	private static Map<Agent, GranulatedChoice> currentTask = 
			new HashMap<Agent, GranulatedChoice>();
	
	public static void clear(){
		currentTask.clear();
	}
	
	public static void setOccupation(Agent agent, Task task, TaskInternals taskInternals){
		GranulatedChoice existing = currentTask.get(agent);
		
		if (existing == null){
			GranulatedChoice granulated = new GranulatedChoice(task, taskInternals, 1);
			currentTask.put(agent, granulated);
		} else {
			existing.incrementHowManyTimes(1);
			currentTask.put(agent, existing);
		}
	}
	
	public static void setOccupation(Agent agent, Task task){
		GranulatedChoice existing = currentTask.get(agent);
		
		if (existing == null){
			GranulatedChoice granulated = new GranulatedChoice(task, null, 1);
			currentTask.put(agent, granulated);
		} else {
			existing.incrementHowManyTimes(1);
			currentTask.put(agent, existing);
		}
	}
	
	public static Task getTask(Agent agent) {
		return currentTask.get(agent).getTaskChosen();
	}
	
	public static TaskInternals getTaskInternals(Agent agent) {
		return currentTask.get(agent).getSkillChosen();
	}
	
	/**
	 * Gets choice for given agent - last Task worked on during previous tick
	 * @param agent
	 * @return Granulated choice (last Task and Skill worked on)
	 */
	public static GranulatedChoice getGranulatedChoice(Agent agent) {
		return currentTask.get(agent);
	}

}
