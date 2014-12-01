package collaboration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import logger.PjiitOutputter;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.Seconds;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.util.ContextUtils;
import utils.AgentEvolve;
import constants.Constraints;

public class GameController {

	private int generationNumber;
	private int iterationNumber;
	private int currentGeneration;
	private int currentIteration;

	private DateTime previous = new DateTime();

	public GameController() {
		Parameters params = RunEnvironment.getInstance().getParameters();
		iterationNumber = (Integer) params.getValue("iterationCount");
		// variable iterationNumber states how many ticks long
		// is a single generation, we use mostly value of 200
		generationNumber = Constraints.generationNumber;
		// how many generations we want to simulate
		// mostly it is 10 generations in our batch files
		say("generationNumber: " + generationNumber);
		say("iterationNumber: " + iterationNumber);
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
		if (isFirstGeneration()) {
			// warming up, hold on with evolution,
			// 1-generation scenarios don't need evolution
			if (currentIteration == (iterationNumber - 1)) {
				say("counterIteration: " + currentIteration);
				say("Execute first generation end protocols");
				// resetAllTasks();
				// reset experience state in Agents
				resetAllAgents();
			}
		} else {
			if (currentIteration == (iterationNumber - 1)) {
				say("counterIteration: " + currentIteration);
				say("Execute generation end protocols");
				// start evolution of Agents
				AgentEvolve.evolve(this);
				// resetAllTasks();
				// reset experience state in Agents
				resetAllAgents();
			}
		}
	}

	/**
	 * Resets states in all Agents
	 */
	private void resetAllAgents() {
		List<Agent> allAgents = chooseAllAgents(this);
		say("Resetting all agents, all together " + allAgents.size()
				+ " of them.");
		for (Agent agent : allAgents) {
			agent.resetMe();
		}
	}

	@ScheduledMethod(start = 1.0, interval = 1.0, priority = -3000)
	public void step() {
		DateTime dateTime = new DateTime();
		Seconds seconds = Seconds.secondsBetween(previous, dateTime);
		Minutes minutes = Minutes.minutesBetween(previous, dateTime);
		say("It took " + minutes.getMinutes() + " minutes and "
				+ seconds.getSeconds() + " seconds between ticks.");

		// check whether this is the last generation/iteration
		if (currentIteration == (iterationNumber - 2)) {
			if (currentGeneration == (generationNumber - 1)) {
				say("Ending instance run");
				RunEnvironment.getInstance().endRun();
			}
		}

		if (currentIteration == (iterationNumber - 1)) {
			say("This is the last iteration in this gen");
			currentIteration = 0;
			say("Ending current generation");
			System.out.println(currentGeneration);
			currentGeneration++;
		} else {
			say("Incrementing current iteration number to: "
					+ (currentIteration + 1));
			currentIteration++;
		}
		previous = new DateTime();
	}

	public int getCurrentGeneration() {
		return currentGeneration;
	}

	public int getCurrentIteration() {
		return currentIteration;
	}
	
	public double getCurrentTick(){
		return RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
	}

	public boolean isWarmedUp() {
		return currentIteration >= (iterationNumber * 0.05);
	}

	public static ArrayList<Agent> chooseAllAgents(Object contextBeing) {
		Context<Object> context = ContextUtils.getContext(contextBeing);
		Iterable<Object> it = context.getObjects(Agent.class);
		ArrayList<Agent> result = new ArrayList<Agent>();
		Iterator<Object> iterator = it.iterator();
		while (iterator.hasNext()) {
			result.add((Agent) iterator.next());
		}
		return result;
	}

	private static void say(String s) {
		PjiitOutputter.say(s);
	}

}
