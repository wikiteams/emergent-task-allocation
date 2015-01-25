package tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import collaboration.Agent;
import collaboration.Skill;
import collaboration.Task;
import collaboration.TasksUtils;

/***
 * Homophyly strategy simplified to one method
 * 
 * @author Oskar Jarczyk
 * @since 1.0
 * @version 2.0.9 'White fox' release
 */
public class HomophylyExpBased implements StrategyMathInterface {
	
	Map<String, Task> tasks;

	public HomophylyExpBased(Map<String, Task> tasks) {
		this.tasks = tasks;
	}

	public Task concludeMath(Agent agent){
		Task chosen = null;
		assert agent != null;
		
		List<Task> intersection = new ArrayList<Task>(tasks.values());
		Collections.shuffle(intersection);
		
		double expArgMax = Double.MIN_VALUE;
		
		for (Task task : intersection){
			double taskHighestSkillExp = 0;
			
			List<Skill> common = new ArrayList<Skill>(
					TasksUtils.intersectWithAgentSkills(agent, task.getSkills()));
			
			if(!common.isEmpty()){
				Collections.shuffle(common);
				taskHighestSkillExp = Double.MIN_VALUE;
				for(Skill skill : common){
					double agentExp = agent.getExperience(skill);
					if (agentExp > taskHighestSkillExp){
						taskHighestSkillExp = agentExp;
					}
				}
			}
			if (taskHighestSkillExp > expArgMax){
				expArgMax = taskHighestSkillExp;
				chosen = task;
			}
		}
		
		return chosen;
	}
	
}