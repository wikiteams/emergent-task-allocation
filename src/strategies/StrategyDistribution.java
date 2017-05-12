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
		return TaskChoice.valueOf(taskChoice);
	}

	public SkillChoice getSkillStrategy() {
		return SkillChoice.valueOf(skillChoice);
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
		return (this.taskChoice != null) && (this.taskChoice.equalsIgnoreCase("central"));
	}

	public StrategySet getStrategySet() {
		return strategySet;
	}

	public void setTaskChoiceSet(Integer planNumber) {
		this.strategySet = StrategySet.get(planNumber);
	}

}
