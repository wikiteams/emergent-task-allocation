package tasks;

import collaboration.Agent;
import collaboration.Skill;
import collaboration.Task;
import collaboration.TaskPool;
import collaboration.TaskPoolHandy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import logger.PjiitOutputter;
import argonauts.PersistJobDone;
import constants.Constraints;

public class HomophylyExpBased {
	
	private Map<String, Task> tasks;
	private TaskPoolHandy tools;
	
	public HomophylyExpBased(Map<String, Task> tasks){
		this.tasks = tasks;
		this.tools = new TaskPoolHandy();
	}
	
	public Task concludeMath(Agent agent){
		Task chosen = null;
		
		assert agent != null;
		Collection<Skill> skillsByExperienceHmphly = null;
		say("Starting chooseTask consideration inside homophyly for "
				+ agent.getNick());
		if (agent.wasWorkingOnAnything()) {
			// describe what he was working on..
			Map<Integer, Task> desc = PersistJobDone.getContributions(agent
					.getNick());
			assert desc.size() > 0;
			say("Agent " + agent.getNick()
					+ " already have experience in count of: "
					+ desc.size());
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

			skillsByExperienceHmphly = tools.intersectWithAgentSkills(agent,
					mostOften.getSkills());
		} else {
			// he wasn't working on anything, take skill matrix
			skillsByExperienceHmphly = agent.getSkills();
			// take all the skills
		}

		if (skillsByExperienceHmphly.size() < 1) {
			skillsByExperienceHmphly = agent.getSkills();
		}

		assert skillsByExperienceHmphly.size() > 0;

		// create list of tasks per a skill
		HashMap<Skill, ArrayList<Task>> tasksPerSkillsHmphly = 
				TaskPool.getTasksPerSkills(skillsByExperienceHmphly);
		// there are no tasks left with such experience ?
		// there is nothing to do
		if (tasksPerSkillsHmphly.size() < 1) {
			// tasksPerSkillsHmphly = getTasksPerSkills(agent.getSkills());
			say(Constraints.DIDNT_FOUND_TASK);
			return chosen;
		}

		HashMap<Task, Integer> intersectionHomophyly = null;

		if (tasksPerSkillsHmphly != null)
			intersectionHomophyly = TaskPoolHandy.
				searchForIntersection(tasksPerSkillsHmphly);
		// search for intersections of n-size

		if (intersectionHomophyly == null
				|| intersectionHomophyly.size() == 0) {
			say(Constraints.DIDNT_FOUND_TASK);
			return chosen;
		}

		Collection<Integer> collectionIntersection = intersectionHomophyly
				.values();
		Integer maximumFromIntersect = Collections
				.max(collectionIntersection);

		ArrayList<Task> tasksWithMaxHomophyly = new ArrayList<Task>();
		for (Task commonTask : intersectionHomophyly.keySet()) {
			if (intersectionHomophyly.get(commonTask) == maximumFromIntersect) {
				tasksWithMaxHomophyly.add(commonTask);
			}
		}
		// take biggest intersection set possible
		chosen = tasksWithMaxHomophyly.get((int) ((new Random()
				.nextDouble()) * tasksWithMaxHomophyly.size()));
		// random
		
		return chosen;
	}
	
	private static void say(String s) {
		PjiitOutputter.say(s);
	}

}
