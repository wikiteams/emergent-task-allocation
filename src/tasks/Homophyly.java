package tasks;

import collaboration.Agent;
import collaboration.Skill;
import collaboration.Task;

import java.util.Collection;
import java.util.Map;

import repast.simphony.random.RandomHelper;

public class Homophyly {
	
	private Map<String, Task> tasks;
	
	public Homophyly(Map<String, Task> tasks){
		this.tasks = tasks;
	}
	
	public Task concludeMath(Agent agent){
		Task taskWithHighestSG = null;
		double found_sigma_delta = 0;
		Task chosen = null;
		
		Collection<Skill> allAgentSkillsHomCl = agent.getSkills();
		for (Task singleTaskFromPool : tasks.values()) {
			double sigma_delta = 0;
			boolean consider = false;
			for (Skill singleSkill : allAgentSkillsHomCl) {
				if (singleTaskFromPool.getTaskInternals().containsKey(
						singleSkill.toString())) {
					consider = true;
					sigma_delta += singleTaskFromPool.
							getTaskInternals(singleSkill.getName()).getProgress();
				}
			}
			if (consider){
				if (taskWithHighestSG == null) {
					taskWithHighestSG = singleTaskFromPool;
					found_sigma_delta = sigma_delta;
				} else {
					if (found_sigma_delta < sigma_delta) {
						taskWithHighestSG = singleTaskFromPool;
					}
				}
			}
		}
		if (taskWithHighestSG != null){
			chosen = taskWithHighestSG;
		} else {
			// intersection is empty, chose random
			if (tasks.size() > 0)
				chosen = tasks.get(RandomHelper.nextIntFromTo(0,tasks.size()-1));
			else
				chosen = null;
		}
		return chosen;
	}

}
