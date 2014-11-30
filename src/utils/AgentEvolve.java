package utils;

import java.util.ArrayList;

import logger.PjiitOutputter;
import collaboration.Agent;
import collaboration.Agents;
import collaboration.GameController;


public class AgentEvolve {

	private static ArrayList<Agent> chooseAgents(Object context) {
		say("Choosing all agents to participate in evolution");
		return GameController.chooseAllAgents(context);
	}

	public static void evolve(Object context) {
		say("Executing stochasting universal sampling");
		ArrayList<Agent> agents = chooseAgents(context);
		say("There are " + agents.size() + " agents ");
		say("E_use of 1st good hyip before evolution: " + agents.get(0).describeExperience());
		Agents.stochasticSampling(agents);
		say("E_use of 1st good hyip after evolution: " + agents.get(0).describeExperience());
	}

	private static void say(String s) {
		PjiitOutputter.say(s);
	}

}
