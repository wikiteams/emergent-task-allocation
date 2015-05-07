package strategies;

import strategies.Strategy.SkillChoice;
import strategies.Strategy.TaskChoice;

/***
 * Tells whether evolutionary model is enabled and/or what kind of strategy
 * distribution there is currently set.
 * 
 * @author Oskar Jarczyk
 * @version 2.0.11
 */
public class StrategyDistribution {

	public static final int SINGULAR = 0;
	public static final int EVOLUTIONARY = 1;

	private static final String[] taskChoiceSet = { "homophyly", "heterophyly",
			"preferential", "random", "central" };
	private static final String[] skillChoiceSet = { "proportional", "greedy",
			"choice", "random" };

	private int type;

	private String skillChoice;
	private String taskChoice;
	private StrategySet strategySet;

	public Boolean isSingle() {
		return this.type == SINGULAR;
	}

	public Boolean isMultiple() {
		return this.type == EVOLUTIONARY;
	}

	public Boolean isDistributionLoaded() {
		return this.strategySet != null;
	}

	public TaskChoice getTaskStrategy() {
		if (taskChoice.equals(taskChoiceSet[0])) {
			return Strategy.TaskChoice.HOMOPHYLY;
		} else if (taskChoice.equals(taskChoiceSet[1])) {
			return Strategy.TaskChoice.HETEROPHYLY;
		} else if (taskChoice.equals(taskChoiceSet[2])) {
			return Strategy.TaskChoice.PREFERENTIAL;
		} else if (taskChoice.equals(taskChoiceSet[3])) {
			return Strategy.TaskChoice.RANDOM;
		} else if (taskChoice.equals(taskChoiceSet[4])) {
			return Strategy.TaskChoice.CENTRAL_ASSIGNMENT;
		}
		return null;
	}

	public SkillChoice getSkillStrategy() {
		if (skillChoice.equals(skillChoiceSet[0])) {
			return Strategy.SkillChoice.PROPORTIONAL_TIME_DIVISION;
		} else if (skillChoice.equals(skillChoiceSet[1])) {
			return Strategy.SkillChoice.GREEDY_ASSIGNMENT;
		} else if (skillChoice.equals(skillChoiceSet[2])) {
			return Strategy.SkillChoice.CHOICE_OF_AGENT;
		} else if (skillChoice.equals(skillChoiceSet[3])) {
			return Strategy.SkillChoice.RANDOM;
		}
		return null;
	}

	public String getSkillChoice() {
		return skillChoice;
	}

	public void setSkillChoice(String skillChoice) {
		assert skillChoice != null;
		this.skillChoice = skillChoice;
	}

	public String getTaskChoice() {
		return taskChoice;
	}

	public void setTaskChoice(String taskChoice) {
		assert taskChoice != null;
		this.taskChoice = taskChoice;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Boolean isCentralPlannerEnabled() {
		return (this.taskChoice != null) && (this.taskChoice.equals("central"));
	}

	public StrategySet getStrategySet() {
		return strategySet;
	}

	public void setTaskChoiceSet(Integer planNumber) {
		this.strategySet = StrategySet.get(planNumber);
	}

}
