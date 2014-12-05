package strategies;

import repast.simphony.random.RandomHelper;
import strategies.Strategy.SkillChoice;
import strategies.Strategy.TaskChoice;
import constants.ModelFactory;

/***
 * Tells whether evolutionary model is enabled and/or what kind of strategy
 * distribution there is currently set.
 * 
 * @author Oskar Jarczyk
 * @version 2.0.6
 */
public class StrategyDistribution {

	public static final int SINGULAR = 0;
	public static final int MULTIPLE = 1;

	private String[] taskChoiceSet = { "homophyly", "heterophyly",
			"preferential", "random", "central" };
	private String[] skillChoiceSet = { "proportional", "greedy", "choice",
			"random" };

	private int type;

	private String skillChoice;
	private String taskChoice;

	public Boolean isSingle() {
		return this.type == SINGULAR;
	}

	public Boolean isMultiple() {
		return this.type == MULTIPLE;
	}

	public TaskChoice getTaskStrategy() {
		if (type == SINGULAR) {
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
		} else {
			assert false;
		}
		return null;
	}

	public SkillChoice getSkillStrategy() {
		if (type == SINGULAR) {
			if (skillChoice.equals(skillChoiceSet[0])) {
				return Strategy.SkillChoice.PROPORTIONAL_TIME_DIVISION;
			} else if (skillChoice.equals(skillChoiceSet[1])) {
				return Strategy.SkillChoice.GREEDY_ASSIGNMENT;
			} else if (skillChoice.equals(skillChoiceSet[2])) {
				return Strategy.SkillChoice.CHOICE_OF_AGENT;
			} else if (skillChoice.equals(skillChoiceSet[3])) {
				return Strategy.SkillChoice.RANDOM;
			}
		} else {
			assert false;
		}
		return null;
	}

	public String getSkillChoice() {
		return skillChoice;
	}

	public void setSkillChoice(String skillChoice) {
		this.skillChoice = skillChoice;
	}

	public void setSkillChoice(ModelFactory modelFactory, String skillChoice) {
		if (modelFactory.getFunctionality().isMultipleValidation()) {
			
			// this is only important if you launch a "multiple validation" model
			
			int intRandomized = RandomHelper.nextIntFromTo(0,
					skillChoiceSet.length - 1);
			assert (intRandomized >= 0)
					&& (intRandomized <= skillChoiceSet.length - 1);
			this.skillChoice = skillChoiceSet[intRandomized];
			
		} else
			this.skillChoice = skillChoice;
	}

	public String getTaskChoice() {
		return taskChoice;
	}

	public void setTaskChoice(String taskChoice) {
		this.taskChoice = taskChoice;
	}

	public void setTaskChoice(ModelFactory modelFactory, String taskChoice) {
		if (modelFactory.getFunctionality().isMultipleValidation()) {
			
			// this is only important if you launch a "multiple validation" model
			
			int intRandomized = RandomHelper.nextIntFromTo(0,
					taskChoiceSet.length - 1);
			assert (intRandomized >= 0)
					&& (intRandomized <= taskChoiceSet.length - 1);
			this.taskChoice = taskChoiceSet[intRandomized];
			
		} else
			this.taskChoice = taskChoice;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
