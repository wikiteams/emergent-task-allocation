package intelligence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import repast.simphony.random.RandomHelper;
import strategies.Strategy;
import tasks.CentralAssignment;
import tasks.HeterophylyExpBased;
import tasks.HomophylyExpBased;
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
 * @version 2.0.8
 */
public class TasksDiviner {

	public static synchronized Task chooseTask(Agent agent,
			Strategy.TaskChoice strategy, Map<String, Task> tasks) {

		Task chosen = null;
		assert strategy != null;

		switch (strategy) {
		case HOMOPHYLY:
			HomophylyExpBased homophylyExp = new HomophylyExpBased(tasks);
			chosen = homophylyExp.concludeMath(agent);
			break;
		case HETEROPHYLY:
			HeterophylyExpBased heterophylyExp = new HeterophylyExpBased(tasks);
			chosen = heterophylyExp.concludeMath(agent);
			break;
		case PREFERENTIAL:
			Preferential preferential = new Preferential(tasks);
			chosen = preferential.concludeMath(agent);
			break;
		case RANDOM:
			List<Task> internalRandomList;
			Collection<Task> coll = tasks.values();
			if (coll instanceof List)
				internalRandomList = (List<Task>) coll;
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

			break;
		case CENTRAL:
			CentralAssignment centralAssignment = new CentralAssignment();
			chosen = centralAssignment.concludeMath(agent);
			break;
		default:
			assert false; // there is no default method, so please never happen
			break;
		}
/*		if (chosen != null) {
			System.out.println("Agent " + agent.toString() + " uses strategy "
					+ agent.getStrategy() + " and chooses task "
					+ chosen.getId() + " by " + strategy + " to work on.");
		}*/
		if (chosen == null) {
/*			System.out.println("Agent (" + agent.getId() + ") "
					+ agent.toString() + " uses strategy "
					+ agent.getStrategy() + " by " + strategy
					+ " but didn't found any task to work on.");*/
			// Choosing any task left
			List<Task> internalRandomList;
			Collection<Task> coll = tasks.values();
			if (coll instanceof List)
				internalRandomList = (List<Task>) coll;
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
			// assert chosen != null;
			//System.out.println("Tick" + agent.getIteration());
			//System.out.println("WARNING - No task has been choosen!");

		}
		return chosen;
	}

}
