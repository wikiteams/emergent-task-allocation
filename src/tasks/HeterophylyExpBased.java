package tasks;

import collaboration.Agent;
import collaboration.Skill;
import collaboration.Task;
import collaboration.Tasks;
import collaboration.TasksUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import logger.PjiitOutputter;
import repast.simphony.random.RandomHelper;
import argonauts.PersistJobDone;

public class HeterophylyExpBased {
	
	private Map<String, Task> tasks;
	private TasksUtils tools;
	
	public HeterophylyExpBased(Map<String, Task> tasks){
		this.tasks = tasks;
		this.tools = new TasksUtils();
	}
	
	public Task concludeMath(Agent agent){
		Task chosen = null;
		
		Collection<Skill> c = null;

		if (agent.wasWorkingOnAnything()) {
			// describe what he was working on..
			Map<Integer, Task> desc = PersistJobDone.getContributions(agent
					.getNick());
			assert desc.size() > 0;

			int highest = 0;
			Task mostOften = null;

			ArrayList<Task> shuffled = new ArrayList<Task>(desc.values());
			Collections.shuffle(shuffled);
			assert shuffled.size() > 0;

			for (Task oneOfTheShuffled : shuffled) {
				int taskFruequency = Collections.frequency(desc.values(),
						oneOfTheShuffled);
				if (taskFruequency > highest) {
					mostOften = oneOfTheShuffled;
				}
			}

			assert mostOften != null;

			c = tools.intersectWithAgentSkills(agent, mostOften.getSkills());
		} else {
			// he wasn't working on anything, take skill matrix
			c = agent.getSkills();
			// take all the skills
		}
		if (c.size() < 1) {
			c = agent.getSkills();
		}

		assert c.size() > 0;

		// create list of tasks per a skill
		HashMap<Skill, ArrayList<Task>> h = Tasks.getTasksWithoutSkills(c);
		// there are no tasks left with such conditions ?
		// try again but now with agent skills
		if (h.size() < 1) {
			h = Tasks.getTasksWithoutSkills(agent.getSkills());
		}
		// there are no tasks left with such conditions ?
		// try again but now with homophyly acceptance
		if (h.size() < 1) {
			h = Tasks.getTasksPerSkills(agent.getSkills());
		}

		HashMap<Task, Integer> inters = null;

		if (h != null)
			inters = tools.searchForIntersection(h);
		// Rewrite the collection to hashed by tasks

		if (inters == null || inters.size() == 0) {
			say("Didn't found task with such skills which agent don't have!");
			return chosen;
		}

		Collection<Integer> ci = inters.values();
		Integer maximum = Collections.max(ci);

		ArrayList<Task> intersection = new ArrayList<Task>();
		for (Task task__ : inters.keySet()) {
			if (inters.get(task__) == maximum) {
				intersection.add(task__);
			}
		}
		// take biggest intersection set possible

		chosen = intersection
				.get((int) ((RandomHelper.nextDoubleFromTo(0,1)) * intersection
						.size()));
		// random
		
		return chosen;
	}
	
	private static void say(String s) {
		PjiitOutputter.say(s);
	}

}
