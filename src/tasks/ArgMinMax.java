package tasks;

import collaboration.Agent;
import collaboration.Skill;
import collaboration.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Random;

import logger.PjiitOutputter;
import strategies.Aggregate;

public class ArgMinMax {
	
	private Map<String, Task> tasks;
	
	public ArgMinMax(Map<String, Task> tasks){
		this.tasks = tasks;
	}
	
	public Task concludeMath(Agent agent){
		ArrayList<Task> tasksWithMatchingSkillsMinMax = new ArrayList<Task>();
		Collection<Skill> allAgentSkillsPreMinMax = agent.getSkills();
		Task chosen = null;
		
		for (Task singleTaskFromPool : tasks.values()) {
			for (Skill singleSkill : allAgentSkillsPreMinMax) {
				if (singleTaskFromPool.getTaskInternals().containsKey(
						singleSkill.toString())) {
					tasksWithMatchingSkillsMinMax.add(singleTaskFromPool);
				}
			}
		}

		if (tasksWithMatchingSkillsMinMax.size() < 1) {
			say("Didn't found task with such skills which agent have!");
			return chosen;
		}

		Collection<Skill> allAgentSkillsMinMax = agent.getSkills();

		Random generator = new Random();
		Task randomValue = tasksWithMatchingSkillsMinMax.get(generator
				.nextInt(tasksWithMatchingSkillsMinMax.size()));

		Task argMaxMax = randomValue;
		Task argMaxMin = randomValue;
		Task argMinMax = randomValue;
		Task argMinMin = randomValue;

		for (Task singleTaskFromPool : tasks.values()) {
			boolean consider = false;
			for (Skill singleSkill : allAgentSkillsMinMax) {
				if (singleTaskFromPool.getTaskInternals().containsKey(
						singleSkill.toString())) {
					consider = true;
					break;
				}
			}
			if (consider) {
				Aggregate aggregate = singleTaskFromPool.argmaxmin();
				assert aggregate != null;
				switch (agent.getStrategy().taskMinMaxChoice) {
				case ARGMAX_ARGMAX:
					if (aggregate.argmax > argMaxMax.argmax()) {
						argMaxMax = singleTaskFromPool;

					}
					chosen = argMaxMax;
					break;
				case ARGMAX_ARGMIN:
					if (aggregate.argmin > argMaxMax.argmin()) {
						argMaxMin = singleTaskFromPool;

					}
					chosen = argMaxMin;
					break;
				case ARGMIN_ARGMAX:
					if (aggregate.argmax < argMaxMax.argmax()) {
						argMinMax = singleTaskFromPool;

					}
					chosen = argMinMax;
					break;
				case ARGMIN_ARGMIN:
					if (aggregate.argmin < argMaxMax.argmin()) {
						argMinMin = singleTaskFromPool;

					}
					chosen = argMinMin;
					break;
				default:
					assert false; // should never happen
					break;
				}
			}
		}
		
		return chosen;
	}
	
	private static void say(String s) {
		PjiitOutputter.say(s);
	}

}
