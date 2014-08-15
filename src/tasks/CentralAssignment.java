package tasks;

import collaboration.Agent;
import collaboration.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import strategies.Strategy.TaskChoice;

/**
 * Central-assignment strategy additional methods
 * 
 * @author Oskar Jarczyk
 * @version 1.4
 */
public class CentralAssignment {

	/**
	 * Returns list of agents which don't have any orders assigned
	 * @param agents Input list of agents
	 * @param bussy Input list of busy agents
	 * @return list of agents which are available for work
	 */
	public static List<Agent> choseAgents(Collection<Agent> agents, 
			Collection<Agent> bussy) {
		List<Agent> list = new ArrayList<Agent>();
		for (Agent agent : agents) {
			if (agent.getStrategy().taskChoice
					.equals(TaskChoice.CENTRAL_ASSIGNMENT)) {
				if (!bussy.contains(agent)){
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
	 * Return orders from a central planer, or a null when no orders exists
	 * @param agent
	 * @return Central assignment planer chosen task
	 */
	public Task concludeMath(Agent agent) {
		Task chosen = null;
		
		CentralAssignmentOrders cao = agent.getCentralAssignmentOrders();
		chosen = cao == null ? null : cao.getChosenTask();

		return chosen;
	}

}
