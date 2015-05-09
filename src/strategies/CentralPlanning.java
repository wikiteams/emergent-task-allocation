package strategies;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import logger.VerboseLogger;
import repast.simphony.util.collections.IndexedIterable;
import tasks.CentralAssignmentOrders;
import utils.ObjectsHelper;
import collaboration.Agent;
import collaboration.Skill;
import collaboration.Task;
import collaboration.TaskInternals;
import collaboration.Tasks;

/**
 * Algorithm of central work planning, heuristic is based on an entity called a
 * Central Planner which sorts tasks descending by those least finished, and
 * finds an agent most experienced in those tasks.
 * 
 * @author Oskar Jarczyk, prof. Adam Wierzbicki
 * @since 1.3
 * @version 2.0.4
 */
public class CentralPlanning {

	/**
	 * This value is used to automatically generate agent identifiers.
	 * 
	 * In nuclear physics, a magic number is a number of nucleons (either
	 * protons or neutrons) such that they are arranged into complete shells
	 * within the atomic nucleus. The seven most widely recognised magic numbers
	 * as of 2007 are 2, 8, 20, 28, 50, 82, and 126 (sequence A018226 in OEIS).
	 * 
	 * @field serialVersionUID
	 */
	public static final long serialVersionUID = 2820285082126L;

	private static CentralPlanning singletonInstance;

	private CentralPlanning() {
		say("getSingletonInstance() prevents any other class from instantiating");
	}

	public static CentralPlanning getSingletonInstance() {
		if (null == singletonInstance) {
			singletonInstance = new CentralPlanning();
		}
		return singletonInstance;
	}

	public void zeroAgentsOrders(IndexedIterable<Agent> listAgent) {
		say("Zeroing central planer orders for " + listAgent.size() + "agents");
		for (Agent agent : listAgent) {
			say("Zeroing orders for " + agent.getName());
			agent.setCentralAssignmentOrders(null);
		}
	}

	/***
	 * A proper central planner is a problem in a category of work(load) balance
	 * known e.g. from designing planet rovers etc. where decisions must be made
	 * in a short period of time to e.g. minimise power use etc. We present here
	 * a sample central planner algorithm which seems to do the job as assessed
	 * through the rule of thumb.
	 * 
	 * @TODO: compare time with previous version of central assignment
	 *        algorithm, as described in our previous paper
	 * 
	 * @param Iterable
	 *            <Object> agents - a pool of agents
	 * @param Tasks
	 *            taskPool - a pool of tasks
	 */
	public void centralPlanningCalc(Iterable<Agent> agents, Tasks taskPool) {
		say("[Central planning] working !");

		Map<Agent, Double> measurements = new HashMap<Agent, Double>();
		Map<Agent, TaskInternals> results = new HashMap<Agent, TaskInternals>();

		if (Tasks.stillNonEmptyTasks()) {

			for (Task task : taskPool.getUnfinishedTasks()) {
				for (Object agent : agents) {
					Double highestValue = null;
					Skill highestSkill = null;
					for (TaskInternals taskInternal : task.getTaskInternals()
							.values()) {
						Skill skill = taskInternal.getSkill();
						Double workLeft = taskInternal.getWorkLeft();
						Double measure = workLeft
								* ((Agent) agent).describeExperience(skill,
										true, false);
						if (ObjectsHelper.is2ndHigher(highestValue, measure)) {
							highestValue = measure;
							highestSkill = skill;
						}
					}
					if (ObjectsHelper.isHigherThanMapEntries(measurements,
							agent, highestValue)) {
						results.put((Agent) agent,
								task.getTaskInternals(highestSkill));
						measurements.put((Agent) agent, highestValue);
					}
				}
			}

			// keep it simple - every agent need to work

			for (Entry<Agent, TaskInternals> entry : results.entrySet()) {
				Agent agent = entry.getKey();
				TaskInternals taskInternal = entry.getValue();
				Task task = taskInternal.getOwner();
				agent.setCentralAssignmentOrders(new CentralAssignmentOrders(
						task, taskInternal));
			}
		}
	}

	private void say(String s) {
		VerboseLogger.say(s);
	}

	@Override
	public String toString() {
		return "CentralPlanner intelligence, signature: " + serialVersionUID;
	}

}
