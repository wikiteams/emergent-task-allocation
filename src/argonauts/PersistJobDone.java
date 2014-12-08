package argonauts;

import collaboration.Agent;
import collaboration.Skill;
import collaboration.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import repast.simphony.engine.environment.RunEnvironment;
import strategies.Strategy.TaskChoice;

/**
 * Persist history of job done by agents, for multiple purpose later.
 * 
 * @author Oskar Jarczyk
 * @version 1.3
 */
public class PersistJobDone {

	/**
	 * <Ai.name : <iterationNu : Task>>
	 * Agent nick - key
	 * value - iteration number, Task
	 */
	private static Map<String, Map<Integer, Task>> jobDone = 
			new HashMap<String, Map<Integer, Task>>();
	
	private static Map<String, Map<Integer, List<Skill>>> skillImproved = 
			new HashMap<String, Map<Integer, List<Skill> > >();
	
	public static void clear(){
		jobDone.clear();
		skillImproved.clear();
	}
	
	/**
	 * Persisting information about a bit of work done on any task
	 * to be used later by exp-based calculations and preferential
	 * @param agentNick
	 * Nick of agent (result of aget.getNick()) used to literally identity
	 * agent by his nick
	 * @param task
	 * Task object on which agent was working
	 */
	public static void addContribution(Agent agent, Task task, List<Skill> skills){
		String agentNick = agent.getNick();
		
		int iteration = (int) RunEnvironment.getInstance().
				getCurrentSchedule().getTickCount();
		
		Map<Integer, Task> value = jobDone.get(agentNick);
		if (value == null){
			jobDone.put(agentNick, new HashMap<Integer, Task>());
			value = jobDone.get(agentNick);
		}
		value.put(iteration, task);
		jobDone.put(agentNick, value);
		
		Map<Integer, List<Skill>> valueS = skillImproved.get(agentNick);
		if (valueS == null){
			skillImproved.put(agentNick, new HashMap<Integer, List<Skill>>());
			valueS = skillImproved.get(agentNick);
		}
		valueS.put(iteration, skills);
		skillImproved.put(agentNick, valueS);

	}
	
	public static Map<Integer, Task> getContributions(String agentNick) {
		return jobDone.get(agentNick);
	}
	
	public static Map<Integer, List<Skill>> getSkillsWorkedOn(Agent agent){
		return skillImproved.get(agent.getNick());
	}

	public static Map<String, Map<Integer, Task>> getJobDone() {
		return jobDone;
	}

	public static void setJobDone(Map<String, Map<Integer, Task>> contributions) {
		PersistJobDone.jobDone = contributions;
	}

}
