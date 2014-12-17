package intelligence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import logger.PjiitOutputter;
import repast.simphony.random.RandomHelper;
import strategies.Strategy;
import tasks.CentralAssignment;
import tasks.Heterophyly;
import tasks.Homophyly;
import tasks.Preferential;
import collaboration.Agent;
import collaboration.SimulationParameters;
import collaboration.Skill;
import collaboration.Task;

/***
 * Class chooses which math modules to execute
 * 
 * @author Oskar Jarczyk
 * @since 2.0
 * @version 2.0.6
 */
public class TasksDiviner {
	
	public static synchronized Task chooseTask(Agent agent,
			Strategy.TaskChoice strategy, Map<String, Task> tasks) {
		
		Task chosen = null;
		assert strategy != null;

		switch (strategy) {
		case HOMOPHYLY:
			Homophyly homophyly = new Homophyly(tasks);
			chosen = homophyly.concludeMath(agent);
			break;
		case HETEROPHYLY:
			Heterophyly heterophyly = new Heterophyly(tasks);
			chosen = heterophyly.concludeMath(agent);
			break;
		case PREFERENTIAL:
			Preferential preferential = new Preferential(tasks);
			chosen = preferential.concludeMath(agent);
			break;
		case RANDOM:
			if (!SimulationParameters.allwaysChooseTask) {
				ArrayList<Task> tasksWithMatchingSkills = new ArrayList<Task>();
				Collection<Skill> allAgentSkills = agent.getSkills();
				for (Task singleTaskFromPool : tasks.values()) {
					for (Skill singleSkill : allAgentSkills) {
						if (singleTaskFromPool.getTaskInternals().containsKey(
								singleSkill.toString())) {
							tasksWithMatchingSkills.add(singleTaskFromPool);
						}
					}
				}
				if (tasksWithMatchingSkills.size() > 0) {
					chosen = tasksWithMatchingSkills.get(RandomHelper
							.nextIntFromTo(0,
									tasksWithMatchingSkills.size() - 1));
				} else {
					say("Didn't found task with such skills which agent have!");
				}
			} else {
				List<Task> internalRandomList;
				Collection<Task> coll = tasks.values();
				if (coll instanceof List)
					internalRandomList = (List) coll;
				else
					internalRandomList = new ArrayList<Task>(coll);
				Collections.shuffle(internalRandomList);
				for (Task singleTaskFromPool : internalRandomList) {
					if (singleTaskFromPool.getTaskInternals().size() > 0)
						if (singleTaskFromPool.getGeneralAdvance() < 1.) {
							chosen = singleTaskFromPool;
							break;
						}
				}
			}
			break;
		case CENTRAL_ASSIGNMENT:
			CentralAssignment centralAssignment = new CentralAssignment();
			chosen = centralAssignment.concludeMath(agent);
			break;
		default:
			assert false; // there is no default method, so please never happen
			break;
		}
		if (chosen != null) {
			sanity("Agent " + agent.toString() + " uses strategy "
					+ agent.getStrategy() + " and chooses task "
					+ chosen.getId() + " by " + strategy + " to work on.");
		} else {
			sanity("Agent (" + agent.getId() + ") " + agent.toString() + " uses strategy "
					+ agent.getStrategy() + " by " + strategy
					+ " but didn't found any task to work on.");
			if (SimulationParameters.allwaysChooseTask) {
				sanity("Choosing any task left because of param allwaysChooseTask");
				List<Task> internalRandomList;
				Collection<Task> coll = tasks.values();
				if (coll instanceof List)
					internalRandomList = (List) coll;
				else
					internalRandomList = new ArrayList<Task>(coll);
				Collections.shuffle(internalRandomList);
				for (Task singleTaskFromPool : internalRandomList) {
					if (singleTaskFromPool.getTaskInternals().size() > 0)
						if (singleTaskFromPool.getGeneralAdvance() < 1.) {
							chosen = singleTaskFromPool;
							break;
						}
				}
			}
		}
		// assert chosen != null;
		return chosen;
	}
	
	private static void say(String s) {
		PjiitOutputter.say(s);
	}
	
	private static void sanity(String s) {
		PjiitOutputter.sanity(s);
	}

}
