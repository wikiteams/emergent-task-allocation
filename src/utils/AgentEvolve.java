package utils;

import intelligence.EquilibriumDetector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import load.AgentCount;
import repast.simphony.random.RandomHelper;
import strategies.Strategy.TaskChoice;
import collaboration.Agent;
import collaboration.Agents;
import collaboration.GameController;

/***
 * Agents' evolution mechanism, sampling and equilibrium detection
 * 
 * @author Oskar Jarczyk
 * @version 2.0.11
 */
public class AgentEvolve {

	private static ArrayList<Agent> chooseAgents(Object context) {
		System.out.println("Choosing all Agents to participate in evolution");
		return GameController.chooseAllAgents(context);
	}

	public static void evolve(Object context) {
		System.out.println("Executing stochasting universal sampling (SUS)");
		ArrayList<Agent> agents = chooseAgents(context);
		System.out.println("There are " + agents.size() + " agents ");
		assert agents.size() == AgentCount.INSTANCE.getChosen();
		Agent testAgent = agents.get(0);
		Agent randomAgent = agents.get(RandomHelper.nextIntFromTo(0,
				agents.size() - 1));
		System.out.println("Strategy of 1st [Agent] before evolution: "
				+ testAgent.getStrategy().toString());
		System.out.println("Experience of 1st [Agent] before evolution: "
				+ testAgent.describeExperience());
		System.out.println("Strategy of random [Agent] before evolution: "
				+ randomAgent.getStrategy().toString());
		System.out.println("Experience of random [Agent] before evolution: "
				+ randomAgent.describeExperience());
		Agents.stochasticSampling(agents);
		System.out.println("Strategy of 1st [Agent] after evolution: "
				+ testAgent.getStrategy().toString());
		System.out.println("Experience of 1st [Agent] after evolution: "
				+ testAgent.describeExperience());
		System.out.println("Strategy of random [Agent] after evolution: "
				+ testAgent.getStrategy().toString());
		System.out.println("Experience of random [Agent] after evolution: "
				+ testAgent.describeExperience());
		EquilibriumDetector.report(buildStrategySet(agents));
	}

	private static Map<TaskChoice, Integer> buildStrategySet(List<Agent> agents) {
		Map<TaskChoice, Integer> result = new HashMap<TaskChoice, Integer>();
		for (Agent agent : agents) {
			TaskChoice strategy = agent.getTaskStrategy();
			if (result.containsKey(strategy)) {
				Integer value = result.get(strategy);
				value += 1;
				result.put(strategy, value);
			} else {
				result.put(strategy, 1);
			}
		}
		return result;
	}

}
