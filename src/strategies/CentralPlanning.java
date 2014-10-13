package strategies;

import collaboration.Agent;
import collaboration.Task;
import collaboration.TaskInternals;
import collaboration.TaskPool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import logger.PjiitOutputter;
import repast.simphony.random.RandomHelper;
import tasks.CentralAssignment;
import tasks.CentralAssignmentOrders;
import utils.LaunchStatistics;

/**
 * Algorithm of central work planning, heuristic is based on en entity called a
 * Central Planner which sorts tasks descending by those least finished, and
 * finds an agent most experienced in those tasks.
 * 
 * @author Oskar Jarczyk
 * @since 1.3
 * @version 2.0
 */
public class CentralPlanning {

	private static CentralPlanning singletonInstance;

	// private List<Agent> bussy;
	// this is deprecated, in 2.0 version of the algorithm we don't use
	// blocking anymore
	private static final double zero = 0;

	// SingletonExample prevents any other class from instantiating
	private CentralPlanning() {
	}
	
    // Providing Global point of access
    public static CentralPlanning getSingletonInstance() {
        if (null == singletonInstance) {
            singletonInstance = new CentralPlanning();
        }
        return singletonInstance;
    }

	public void zeroAgentsOrders(List<Agent> listAgent) {
		say("Zeroing central planer orders for " + listAgent.size() + "agents");
		for (Agent agent : listAgent) {
			agent.setCentralAssignmentOrders(null);
		}
		bussy = bussy == null ? new ArrayList<Agent>() : bussy;
		bussy.clear();
	}

	public void centralPlanningCalc(List<Agent> listAgent, TaskPool taskPool) {
		say("Central planning working !");

		Collections.shuffle(listAgent);

		List<Task> shuffledTasksFirstInit = new ArrayList<Task>(
				taskPool.getTasks());
		Collections.shuffle(shuffledTasksFirstInit);

		SortedMap<Double, TaskInternals> sortedMap = new TreeMap<Double, TaskInternals>(
				new Comparator<Double>() {
					public int compare(Double o1, Double o2) {
						return -o1.compareTo(o2);
					}
				});

		int ensureDuplicatesFactor = 0;
		// Find Task {i} and Skill {j}, with highest work left
		for (Task singleTaskFromPool : shuffledTasksFirstInit) {
			TaskInternals singleChosen = null;
			double wl = 0;
			for (TaskInternals singleSkill : singleTaskFromPool
					.getTaskInternals().values()) {
				// if (checkIfApplicable(singleTaskFromPool, singleSkill)) {
				// double gMinusW = singleSkill.getWorkLeft();
				// ile pozostalo pracy
				if (!singleSkill.isWorkDone()) {
					// chosen = singleTaskFromPool;
					// skill = singleSkill;
					double gMinusW = singleSkill.getWorkLeft();
					if (gMinusW > wl) {
						wl = gMinusW;
						singleChosen = singleSkill;
					}
				}

			}
			if (singleChosen != null)
				sortedMap
						.put(singleChosen.getWorkLeft()
								- ((++ensureDuplicatesFactor) / (10 * 6)),
								singleChosen);
		}

		// Iterate mainIterationCount times
		// if there are less tasks than agent, iterate taskCount times
		int mainIterationCount = sortedMap.size() < LaunchStatistics.singleton.agentCount ? sortedMap
				.size() : listAgent.size();

		Object[] sortedArray = sortedMap.values().toArray();

		for (int i = 0; i < mainIterationCount; i++) {

			TaskInternals skill = (TaskInternals) sortedArray[i];
			Task chosen = skill.getOwner();

			assert chosen != null;
			assert skill != null;

			Agent chosenAgent = null;

			// Choose Agent m, which have highest delta() in Skill j
			List<Agent> listOfAgentsNotBussy = CentralAssignment.choseAgents(
					listAgent, bussy);

			assert listOfAgentsNotBussy != null;
			assert listOfAgentsNotBussy.size() > 0;
			// stad te asserty bo w koncu planner iteruje po ilosci agentow,
			// wiec pracujemy nad choc jednym wolnym!
			double max_delta = zero;
			for (Agent agent : listOfAgentsNotBussy) {
				double local_delta = agent.getAgentInternals(skill
						.getSkillName()) != null ? agent
						.getAgentInternals(skill.getSkillName())
						.getExperience().getDelta() : 0;
				// zero w przypadku gdy agent nie ma w ogole doswiadczenia w tym
				// tasku!
				if (local_delta > max_delta) {
					max_delta = local_delta;
					chosenAgent = agent;
				}
			}

			if (chosenAgent == null) {
				// nie ma zadnego agenta o takich skillach, wybierz losowo !
				Collections.shuffle(listOfAgentsNotBussy);
				chosenAgent = listOfAgentsNotBussy.get(RandomHelper
						.nextIntFromTo(0, listOfAgentsNotBussy.size() - 1));
			}

			assert chosenAgent != null;
			assert chosen != null;
			assert skill != null;

			chosenAgent.setCentralAssignmentOrders(new CentralAssignmentOrders(
					chosen, skill));

			bussy.add(chosenAgent);
		}
	}

	private void say(String s) {
		PjiitOutputter.say(s);
	}

}
