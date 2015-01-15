package tasks;

import collaboration.Agent;
import collaboration.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import strategies.Strategy.TaskChoice;

/**
 * Central assignment strategy additional methods
 * 
 * @author Oskar Jarczyk
 * @since 1.4
 * @version 2.0.3
 */
public class CentralAssignment {

	/**
	 * Returns list of agents which don't have any orders assigned
	 * @param Collection<Agent> agents - input list of agents
	 * @param Collection<Agent> busy - input list of busy agents
	 * @return List<Agent> - list of agents which are available for work
	 */
	public static List<Agent> choseAgents(Collection<Agent> agents, 
			Collection<Agent> busy) {
		List<Agent> list = new ArrayList<Agent>();
		for (Agent agent : agents) {
			if (agent.getStrategy().getTaskChoice()
					.equals(TaskChoice.CENTRAL_ASSIGNMENT)) {
				if (!busy.contains(agent)){
					CentralAssignmentOrders cao = agent
							.getCentralAssignmentOrders();
					if (cao == null)
						list.add(agent);
				}
			}
		}
		return list;
	}

	/**
	 * Return orders from a central planner, or a null when no orders exists
	 * @param Agent agent
	 * @return Central assignment planner chosen task
	 */
	public Task concludeMath(Agent agent) {
		Task chosen = null;
		CentralAssignmentOrders cao = agent.getCentralAssignmentOrders();
		if (cao != null)
			chosen = cao.getChosenTask();
		return chosen;
	}

}
