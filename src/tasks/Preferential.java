package tasks;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import utils.ObjectsHelper;
import collaboration.Agent;
import collaboration.CollaborationBuilder;
import collaboration.GameController;
import collaboration.Skill;
import collaboration.Task;
import collaboration.Tasks;

/***
 * New Preferential Strategy
 * 
 * Firstly - in case of a newly existing network with no connection between
 * edges - we tell pioneer agents to choose tasks with matching skills and
 * highest 'work done' there.
 * 
 * In general, for agent to be able to take a node, there must be a match of
 * skills.
 * 
 * In case of of no at least once visited nodes which match his skills, take any
 * task with matching skill but sort by general advancement.
 * 
 * @author Oskar Jarczyk
 * @version 2.0.6
 */
public class Preferential {
	
	private Map<String, Task> tasks;
	
	public Preferential(Map<String, Task> tasks){
		this.tasks = tasks;
	}

	public Task concludeMath(Agent agent) {
		Collection<Skill> allAgentSkills = agent.getSkills();
		Task chosen = null;

		// get all tasks with agent skills

		Set<Task> tasksHavingSkills = Tasks.getTasksHavingSkills(allAgentSkills);

		Long mostVisitedCount = null;
		Task mostPopularTask = null;

		for (Task task : tasksHavingSkills) {
			if (task.getNumberOfVisits() > 0) {
				if (ObjectsHelper.is2ndHigher(mostVisitedCount,
						task.getNumberOfVisits())) {
					mostVisitedCount = task.getNumberOfVisits();
					mostPopularTask = task;
				}
			} else {
				// do nothing
			}
		}
		
		chosen = mostPopularTask;
		
		// a situation in which tasks having user skills
		// where never 'worked on' yet
		if (chosen == null){
			Double highestAdv = null;
			Task mostDoneTask = null;
			for (Task task : tasksHavingSkills) {
				Double generalAdv = task.getGeneralAdvance();
				if (ObjectsHelper.is2ndHigher(highestAdv,
						generalAdv)) {
					highestAdv = generalAdv;
					mostDoneTask = task;
				}
			}
			chosen = mostDoneTask;
		}
		
		// a situation in which there are no tasks
		// having user skills
		if (chosen == null){
			Collection<Task> allTasks = tasks.values();
			Double highestAdv = null;
			Task mostDoneTask = null;
			for (Task task : allTasks) {
				Double generalAdv = task.getGeneralAdvance();
				if (ObjectsHelper.is2ndHigher(highestAdv,
						generalAdv)) {
					highestAdv = generalAdv;
					mostDoneTask = task;
				}
			}
			chosen = mostDoneTask;
		}

		return chosen;
	}

}
