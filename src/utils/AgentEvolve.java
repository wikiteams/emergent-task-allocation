package utils;

import java.util.ArrayList;

import constants.LoadSet;
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
		say("Strategy of 1st [Agent] before evolution: " + agents.get(0).getStrategy().toString());
		say("Experience of 1st [Agent] before evolution: " + agents.get(0).describeExperience());
		Agents.stochasticSampling(agents);
		say("Strategy of 1st [Agent] after evolution: " + agents.get(0).getStrategy().toString());
		say("Experience of 1st [Agent] after evolution: " + agents.get(0).describeExperience());
	}

	private static void say(String s) {
		PjiitOutputter.say(s);
	}

}
