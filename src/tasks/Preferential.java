package tasks;

import java.util.Collection;
import java.util.Set;

import utils.ObjectsHelper;
import collaboration.Agent;
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

	public Task concludeMath(Agent agent) {
		Collection<Skill> allAgentSkills = agent.getSkills();
		Task chosen = null;

		// get all tasks with agent skills

		Set<Task> tasks = Tasks.getTasksHavingSkills(allAgentSkills);

		Long mostVisitedCount = null;
		Task mostPopularTask = null;
		// List<Task> consideration = new ArrayList<Task>();

		for (Task task : tasks) {
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
		
		if (mostPopularTask == null){
			Double highestAdv = null;
			Task mostDoneTask = null;
			for (Task task : tasks) {
				Double generalAdv = task.getGeneralAdvance();
				if (ObjectsHelper.is2ndHigher(highestAdv,
						generalAdv)) {
					highestAdv = generalAdv;
					mostDoneTask = task;
				}
			}
		}

		return chosen;
	}

}
