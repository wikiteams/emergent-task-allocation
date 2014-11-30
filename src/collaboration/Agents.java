package collaboration;

import java.util.ArrayList;
import java.util.List;

import logger.PjiitOutputter;
import logger.ValidationOutputter;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import strategies.Strategy;
import strategies.StrategyDistribution;
import test.AgentTestUniverse;
import test.Model;
import utils.LaunchStatistics;
import utils.NamesGenerator;
import constants.ModelFactory;

public class Agents extends DefaultContext<Agent> {

	private List<Agent> listAgents;
	private ModelFactory modelFactory;
	private StrategyDistribution strategyDistribution;
	private LaunchStatistics launchStatistics;
	private String universeDescription;

	public Agents(ModelFactory modelFactory,
			StrategyDistribution strategyDistribution,
			LaunchStatistics launchStatistics, String universeDescription) {
		super("Agents");

		this.modelFactory = modelFactory;
		this.strategyDistribution = strategyDistribution;
		this.launchStatistics = launchStatistics;
		this.universeDescription = universeDescription;
		
		initializeAgents(this);
	}

	private void addAgents(Context<Agent> context) {
		Integer agentCnt = SimulationParameters.multipleAgentSets ? Integer
				.parseInt(universeDescription)
				: SimulationParameters.agentCount;

		listAgents = NamesGenerator.getnames(agentCnt);
		for (int i = 0; i < agentCnt; i++) {
			Agent agent = listAgents.get(i);

			Strategy strategy = new Strategy(
					strategyDistribution.getTaskStrategy(),
					strategyDistribution.getTaskMaxMinStrategy(),
					strategyDistribution.getSkillStrategy());

			agent.setStrategy(strategy);
			say(agent.toString());
			say("in add aggent i: " + i);
			// Required adding agent to context
			
			for (AgentInternals ai : agent.getAgentInternals()) {
				assert ai.getExperience().getValue() > 0;
				say("For a=" + agent.toString() + " delta is "
						+ ai.getExperience().getDelta());
				say("For a=" + agent.toString() + " value is "
						+ ai.getExperience().getValue());
				say("For a=" + agent.toString() + " top is "
						+ ai.getExperience().getTop());
			}
			context.add(agent);
		}
		launchStatistics.agentCount = agentCnt;
	}

	private void initializeAgents(Context<Agent> context) {
		Model model = modelFactory.getFunctionality();
		if (model.isNormal() && model.isValidation()) {
			throw new UnsupportedOperationException();
		} else if (model.isNormal()) {
			addAgents(context);
		} else if (model.isSingleValidation()) {
			listAgents = new ArrayList<Agent>();
			AgentTestUniverse.init();
			initializeValidationAgents(context);
		} else if (model.isValidation()) {
			listAgents = new ArrayList<Agent>();
			AgentTestUniverse.init();
			initializeValidationAgents(context);
		}
	}

	private void initializeValidationAgents(Context<Agent> context) {
		for (Agent agent : AgentTestUniverse.DATASET) {
			say("Adding validation agent to pool..");
			Strategy strategy = new Strategy(
					strategyDistribution.getTaskStrategy(),
					strategyDistribution.getTaskMaxMinStrategy(),
					strategyDistribution.getSkillStrategy());
			
			agent.setStrategy(strategy);
			listAgents.add(agent);
			say(agent.toString() + " added to pool.");
			
			// Required adding agent to context
			context.add(agent);
		}
	}

	private void say(String s) {
		PjiitOutputter.say(s);
	}

}