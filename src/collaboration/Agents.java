package collaboration;

import intelligence.AgentComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.random.RandomHelper;
import strategies.Strategy;
import strategies.StrategyDistribution;
import utils.NamesGenerator;

/***
 * Agents context, hence the contex.xml where Repast Simphony holds the context
 * structure. Context holds all simulation Agents.
 * 
 * @author Oskar Jarczyk
 * @version 3.0
 * @since 1.0
 */
public class Agents extends DefaultContext<Agent> {

	private List<Agent> listAgents;
	private StrategyDistribution strategyDistribution;
	private Integer allowedLoad;

	public Agents(StrategyDistribution strategyDistribution, Integer allowedLoad) {
		super("Agents");

		this.strategyDistribution = strategyDistribution;
		this.allowedLoad = allowedLoad;

		initializeAgents(this);
	}

	private void addAgents(Context<Agent> context) {
		Integer agentCnt = allowedLoad;

		listAgents = NamesGenerator.getnames(agentCnt);
		for (int i = 0; i < agentCnt; i++) {
			Agent agent = listAgents.get(i);

			Strategy strategy = strategyDistribution.isMultiple() ? Strategy
					.getInstance(strategyDistribution, i, agentCnt)
					: new Strategy(strategyDistribution.getTaskStrategy(),
							strategyDistribution.getSkillStrategy());
			System.out.println("[Strategy] prepared for agent is: " + strategy.toString());

			agent.setStrategy(strategy);
			System.out.println(agent.toString());
			System.out.println("In add [agent] i: " + i);
			// Required adding agent to context

			for (AgentInternals ai : agent.getAgentInternals()) {
				assert ai.getExperience().getValue() > 0;
				System.out.println("For a=" + agent.toString() + " delta is "
						+ ai.getExperience().getDelta());
				System.out.println("For a=" + agent.toString() + " value is "
						+ ai.getExperience().getValue());
				System.out.println("For a=" + agent.toString() + " top is "
						+ ai.getExperience().getTop());
			}
			context.add(agent);
		}

	}

	private void initializeAgents(Context<Agent> context) {
		addAgents(context);
	}

	/***
	 * Evolution with Stochasting Universal Sampling (SUS)
	 * 
	 * @author Paulina Adamska
	 * @since 2.0, partially 1.3
	 * @version 2.0.6
	 * @param agents
	 *            - list of agents to take part in evolution
	 */
	public static void stochasticSampling(ArrayList<Agent> population) {
		if (population.size() == 0)
			return;
		Collections.sort(population, new AgentComparator());
		double min = population.get(population.size() - 1).getUtility();
		double scaling = min < 0 ? ((-1) * min) : 0;
		double maxRange = 0;
		ArrayList<Double> ranges = new ArrayList<Double>();
		ArrayList<Strategy> strategiesBackup = new ArrayList<Strategy>();

		for (Agent p : population) {
			maxRange += (p.getUtility() + scaling);
			ranges.add(maxRange);
			strategiesBackup.add(p.getStrategy().copy());
		}

		double step = maxRange / population.size();
		double start = RandomHelper.nextDoubleFromTo(0, 1) * step;
		for (int i = 0; i < population.size(); i++) {
			int selectedPlayer = population.size() - 1;
			for (int j = 0; j < ranges.size(); j++) {
				double pointer = start + i * step;
				if (pointer < ranges.get(j)) {
					selectedPlayer = j;
					break;
				}
			}
			Agent nextAgent = population.get(i);
			nextAgent.getStrategy().copyStrategy(
					strategiesBackup.get(selectedPlayer));

			nextAgent.mutate();
		}
	}

}
