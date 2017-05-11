package collaboration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import load.AgentCount;
import load.GenerationLength;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.util.ContextUtils;
import strategies.StrategyDistribution;
import utils.AgentEvolve;
import constants.Constraints;

/**
 * Collaboration controller (evolution controller)
 * 
 * @author Oskar Jarczyk, inspired by code from Paulina Adamska
 * @since 2.0
 * @version 2.0.11
 */
public class GameController {

	/**
	 * This value is used to automatically generate agent identifiers.
	 * 
	 * The first public exhibition of a Foucault pendulum took place in February
	 * 1851 in the Meridian of the Paris Observatory. A few weeks later Foucault
	 * made his most famous pendulum when he suspended a 28 kg brass-coated lead
	 * bob with a 67 meter long wire from the dome of the Pantheon, Paris. The
	 * plane of the pendulum's swing rotated clockwise 11' per hour, making a
	 * full circle in 32.7 hours. The original bob used in 1851 at the Pantheon
	 * was moved in 1855 to the Conservatoire des Arts et Metiers in Paris. A
	 * second temporary installation was made for the 50th anniversary in 1902.
	 * 
	 * @field serialVersionUID
	 */
	public static final long serialVersionUID = 1851L;

	private StrategyDistribution strategyDistribution;

	private int generationNumber;
	private int iterationNumber;
	private int currentGeneration;
	private int currentIteration;

	// TODO: make sure to implement a 2nd part of Collaboration Game, which is
	// checking efficiency of a static strategy set and a central planner

	private boolean firstStage;

	public GameController(StrategyDistribution strategyDistribution, Boolean forceOff) {
		firstStage = !forceOff;
		iterationNumber = GenerationLength.INSTANCE.getChosen();
		// variable iterationNumber states how many ticks long
		// is a single generation, we use mostly value of 200
		generationNumber = Constraints.generationNumber;
		// how many generations we want to simulate
		// mostly it is 10 generations in our batch files
		System.out.println("generationNumber: " + generationNumber);
		System.out.println("iterationNumber: " + iterationNumber);
		this.strategyDistribution = strategyDistribution;
	}

	/**
	 * Tells whether this is a first generation (warming up) or not (generations
	 * are indexed starting from 0)
	 * 
	 * @return true if current generation is the first one
	 */
	public boolean isFirstGeneration() {
		return currentGeneration == 0;
	}

	@ScheduledMethod(start = 1.0, interval = 1.0, priority = -2000)
	public void firstStep() {
		// 1-generation scenarios don't need evolution
		if (isFirstStage()) {
			// hence there is no distinction between
			// first generation and any other generations
			// which you can find e.g. in hyip game or credibility game
			if (currentIteration == (iterationNumber - 1)) {
				System.out.println("counterIteration: " + currentIteration);
				System.out.println("Execute generation end protocols");
				// start evolution of Agents
				AgentEvolve.evolve(this);
				// reset experience state in Agents
				resetAllAgents();
			}
		} else {
			// TODO: implement stage of GameController
			// which happens after evolution is finished
		}
	}

	/**
	 * Resets states in all Agents. This is very important method;
	 * 
	 * Make sure that every new generation will have fresh state of agents, also
	 * reset impact factors and any hidden attributes of agents !
	 */
	private void resetAllAgents() {
		List<Agent> allAgents = chooseAllAgents(this);
		System.out.println("Resetting all Agents, all together " + allAgents.size()
				+ " of them.");
		for (Agent agent : allAgents) {
			agent.resetMe();
		}
	}

	@ScheduledMethod(start = 1.0, interval = 1.0, priority = -3000)
	public void step() {
		if (isFirstStage()) {
			if (currentIteration == (iterationNumber - 2)) {
				if (currentGeneration == (generationNumber - 1)) {
					System.out.println("Waiting for an equilibrium already for "
							+ generationNumber + " generations");
				}
			}
			if (currentIteration == (iterationNumber - 1)) {
				System.out.println("[This is the last iteration in this generation]");
				currentIteration = 0;
				System.out.println("Current generation is: "
						+ currentGeneration);
				System.out.println("[Ending current generation]");
				currentGeneration++;
			} else {
				System.out.println("Incrementing current iterationNumber to: "
						+ (currentIteration + 1));
				currentIteration++;
			}
		} else {
			currentIteration++;
		}
	}

	public int getCurrentGeneration() {
		if (isEvolutionary()) {
			return currentGeneration;
		} else {
			return 0;
		}
	}

	public int getCurrentIteration() {
		return currentIteration;
	}

	public Double getCurrentTick() {
		return RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
	}
	
	public boolean isFirstStage(){
		return firstStage;
	}

	public boolean isSecondStage() {
		return !firstStage;
	}

	/**
	 * Chooses all agents from proper Repast context
	 * 
	 * @param contextBeing
	 * @return
	 */
	public static ArrayList<Agent> chooseAllAgents(Object contextBeing) {
		@SuppressWarnings("unchecked")
		Context<Object> context = ContextUtils.getContext(contextBeing);
		Iterable<Object> it = context.getObjects(Agent.class);
		ArrayList<Agent> result = new ArrayList<Agent>();
		Iterator<Object> iterator = it.iterator();
		while (iterator.hasNext()) {
			result.add((Agent) iterator.next());
		}
		assert (result.size() == AgentCount.INSTANCE.getChosen());
		return result;
	}

	public Integer countHomophilyDistribution(Context<Object> context) {
		Integer result = 0;
		Iterable<Object> it = context.getObjects(Agent.class);
		Iterator<Object> iterator = it.iterator();
		while (iterator.hasNext()) {
			if (((Agent) iterator.next()).usesHomophyly() > 0) {
				result++;
			}
		}
		return result;
	}

	public Integer countHeterophilyDistribution(Context<Object> context) {
		Integer result = 0;
		Iterable<Object> it = context.getObjects(Agent.class);
		Iterator<Object> iterator = it.iterator();
		while (iterator.hasNext()) {
			if (((Agent) iterator.next()).usesHeterophyly() > 0) {
				result++;
			}
		}
		return result;
	}

	public Integer countPreferentialDistribution(Context<Object> context) {
		Integer result = 0;
		Iterable<Object> it = context.getObjects(Agent.class);
		Iterator<Object> iterator = it.iterator();
		while (iterator.hasNext()) {
			if (((Agent) iterator.next()).usesPreferential() > 0) {
				result++;
			}
		}
		return result;
	}

	/***
	 * There are three possibilities.
	 * 
	 * In first, simulation is run with plan number 0 and/or no distribution of
	 * strategies (single distribution). Than no evolution occurs.
	 * 
	 * In second, simulation starts with an evolutionary model.
	 * 
	 * Third options is that evolutionary model finished after finding an
	 * equilibrium, and simulation continues it's run with a central assignment
	 * planner
	 * 
	 * @return
	 */
	public boolean isEvolutionary() {
		boolean result = true;
		if ((SimulationParameters.planNumber == 0)
				|| (strategyDistribution.isSingle())) {
			result = false;
		}
		return result;
	}

}
