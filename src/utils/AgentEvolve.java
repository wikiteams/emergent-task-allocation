package utils;

import java.util.ArrayList;

import repast.simphony.random.RandomHelper;
import logger.PjiitOutputter;
import collaboration.Agent;
import collaboration.Agents;
import collaboration.GameController;
import collaboration.SimulationParameters;


public class AgentEvolve {

	private static ArrayList<Agent> chooseAgents(Object context) {
		say("Choosing all agents to participate in evolution");
		return GameController.chooseAllAgents(context);
	}

	public static void evolve(Object context) {
		say("Executing stochasting universal sampling (SUS)");
		ArrayList<Agent> agents = chooseAgents(context);
		say("There are " + agents.size() + " agents ");
		assert agents.size() == SimulationParameters.agentCount;
		Agent testAgent = agents.get(0);
		Agent randomAgent = agents.get(RandomHelper.nextIntFromTo(0, agents.size() - 1));
		say("Strategy of 1st [Agent] before evolution: " + testAgent.getStrategy().toString());
		say("Experience of 1st [Agent] before evolution: " + testAgent.describeExperience());
		say("Strategy of random [Agent] before evolution: " + randomAgent.getStrategy().toString());
		say("Experience of random [Agent] before evolution: " + randomAgent.describeExperience());
		Agents.stochasticSampling(agents);
		say("Strategy of 1st [Agent] after evolution: " + testAgent.getStrategy().toString());
		say("Experience of 1st [Agent] after evolution: " + testAgent.describeExperience());
		say("Strategy of random [Agent] after evolution: " + testAgent.getStrategy().toString());
		say("Experience of random [Agent] after evolution: " + testAgent.describeExperience());
	}

	private static void say(String s) {
		PjiitOutputter.say(s);
	}

}
