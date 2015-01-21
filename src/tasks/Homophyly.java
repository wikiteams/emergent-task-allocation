package tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import logger.PjiitOutputter;
import collaboration.Agent;
import collaboration.Skill;
import collaboration.Task;
import collaboration.Tasks;
import collaboration.TasksUtils;
import constants.Constraints;

/***
 * Homophyly strategy simplified to one method
 * 
 * @author Oskar Jarczyk
 * @since 1.0
 * @version 2.0.8
 */
public class Homophyly {

	public Homophyly() {

	}

	public Task concludeMath(Agent agent) {

		Task chosen = null;
		assert agent != null;
		
		Double agentSkillsCount = agent.getAgentSkills().count();

		Collection<Skill> skillsByExperienceHmphly = agent.getSkills();
		// there should be no situation when there is Agent with no skills
		// even at start they have some
		assert skillsByExperienceHmphly.size() > 0;

		// create list of tasks per a skill
		Set<Task> intersection = Tasks
				.getTasksHavingSkills(skillsByExperienceHmphly);
		// there are no tasks left with such experience ?
		// there is nothing to do
		if (intersection.size() < 1) {
			say(Constraints.DIDNT_FOUND_TASK);
			return chosen;
		}
		Double lowest = Double.MAX_VALUE;
		Set<Task> candidates = new HashSet<Task>();
		
		for (Task task : intersection){
			Collection<Skill> common = 
					TasksUtils.intersectWithAgentSkills(agent, task.getSkills());
			Double loveFactor = agentSkillsCount / common.size();
			
			if (loveFactor <= lowest){
				lowest = loveFactor;
				candidates.add(task);
			}
		}
		
		assert candidates.size() > 0;
		if (candidates.size() > 1){
			// select tasks with the fewest number of skills;
			Iterator<Task> it = candidates.iterator();
			List<Task> furtherCandidates = new ArrayList<Task>();
			while (it.hasNext()){
				Task task = it.next();
				furtherCandidates.add(task);
			}
			Collections.sort(furtherCandidates, new TaskSizeComparator());
			
			Integer fewest = Integer.MAX_VALUE;
			List<Task> finalCandidates = new ArrayList<Task>();
			
			for (Task task : furtherCandidates){
				int taskActuallSize = task.getSkills().size();
				if (taskActuallSize <= fewest){
					fewest = taskActuallSize;
					finalCandidates.add(task);
				} else {
					break;
				}
			}
			assert finalCandidates.size() > 0;
			if (finalCandidates.size() > 1){
				Double considerExp = Double.MIN_VALUE;
				for (Task task : finalCandidates){
					Collection<Skill> common = 
							TasksUtils.intersectWithAgentSkills(agent, task.getSkills());
					Double agentExp = agent.getFilteredExperience(common);
					if (agentExp > considerExp){
						chosen = task;
						considerExp = agentExp;
					}
				}
			} else {
				chosen = (Task) finalCandidates.toArray()[0];
			}
		} else {
			chosen = (Task) candidates.toArray()[0];
		}

		return chosen;
	}

	private static void say(String s) {
		PjiitOutputter.say(s);
	}

}

class TaskSizeComparator implements Comparator<Task> {

	@Override
	public int compare(Task t1, Task t2) {
		double diff = t1.getSkills().size() - t2.getSkills().size();
		if(diff==0) return 0;
		else return (diff>0?1:-1);
	}

}
