package strategies;

import java.util.List;

import collaboration.Agent;
import strategies.Strategy.SkillChoice;
import strategies.Strategy.TaskChoice;
import strategies.Strategy.TaskMinMaxChoice;
import constants.ModelFactory;
import repast.simphony.random.RandomHelper;

public class StrategyDistribution {

	public static final int SINGULAR = 0;
	public static final int MULTIPLE = 1;

	private String[] taskChoiceSet = { "homophyly", "homophyly_experience",
			"heterophyly", "preferential", "heterophyly_experience", "random",
			"social_vector", "machine_learned", "comparision", "minmax",
			"central" };
	private String[] skillChoiceSet = { "proportional", "greedy", "choice",
			"random" };
	private String[] taskMinMaxChoiceSet = { "maxmax", "maxmin", "minmax",
			"minmin" };

	private int type;

	private String skillChoice;
	private String taskChoice;
	private String taskMinMaxChoice;

	public TaskChoice getTaskStrategy() {
		if (type == SINGULAR) {
			if (taskChoice.equals(taskChoiceSet[0])) {
				return Strategy.TaskChoice.HOMOPHYLY_CLASSIC;
			} else if (taskChoice.equals(taskChoiceSet[1])) {
				return Strategy.TaskChoice.HOMOPHYLY_EXP_BASED;
			} else if (taskChoice.equals(taskChoiceSet[2])) {
				return Strategy.TaskChoice.HETEROPHYLY_CLASSIC;
			} else if (taskChoice.equals(taskChoiceSet[3])) {
				return Strategy.TaskChoice.PREFERENTIAL;
			} else if (taskChoice.equals(taskChoiceSet[4])) {
				return Strategy.TaskChoice.HETEROPHYLY_EXP_BASED;
			} else if (taskChoice.equals(taskChoiceSet[5])) {
				return Strategy.TaskChoice.RANDOM;
			} else if (taskChoice.equals(taskChoiceSet[6])) {
				return Strategy.TaskChoice.SOCIAL_VECTOR;
			} else if (taskChoice.equals(taskChoiceSet[7])) {
				return Strategy.TaskChoice.MACHINE_LEARNED;
			} else if (taskChoice.equals(taskChoiceSet[8])) {
				return Strategy.TaskChoice.COMPARISION;
			} else if (taskChoice.equals(taskChoiceSet[9])) {
				return Strategy.TaskChoice.ARG_MIN_MAX;
			} else if (taskChoice.equals(taskChoiceSet[10])) {
				return Strategy.TaskChoice.CENTRAL_ASSIGNMENT;
			}
		}
		else{
			assert false;
		}
		return null;
	}
	
	public TaskChoice getTaskStrategy(List<Agent> agents) {
		if (type == MULTIPLE) {
			if (taskChoice.equals(taskChoiceSet[0])) {
				return Strategy.TaskChoice.HOMOPHYLY_CLASSIC;
			} else if (taskChoice.equals(taskChoiceSet[1])) {
				return Strategy.TaskChoice.HOMOPHYLY_EXP_BASED;
			} else if (taskChoice.equals(taskChoiceSet[2])) {
				return Strategy.TaskChoice.HETEROPHYLY_CLASSIC;
			} else if (taskChoice.equals(taskChoiceSet[3])) {
				return Strategy.TaskChoice.PREFERENTIAL;
			} else if (taskChoice.equals(taskChoiceSet[4])) {
				return Strategy.TaskChoice.HETEROPHYLY_EXP_BASED;
			} else if (taskChoice.equals(taskChoiceSet[5])) {
				return Strategy.TaskChoice.RANDOM;
			} else if (taskChoice.equals(taskChoiceSet[6])) {
				return Strategy.TaskChoice.SOCIAL_VECTOR;
			} else if (taskChoice.equals(taskChoiceSet[7])) {
				return Strategy.TaskChoice.MACHINE_LEARNED;
			} else if (taskChoice.equals(taskChoiceSet[8])) {
				return Strategy.TaskChoice.COMPARISION;
			} else if (taskChoice.equals(taskChoiceSet[9])) {
				return Strategy.TaskChoice.ARG_MIN_MAX;
			} else if (taskChoice.equals(taskChoiceSet[10])) {
				return Strategy.TaskChoice.CENTRAL_ASSIGNMENT;
			}
		}
		else{
			assert false;
		}
		return null;
	}

	public SkillChoice getSkillStrategy() {
		if (type == SINGULAR) {
			if (skillChoice.equals(skillChoiceSet[0])) {
				return Strategy.SkillChoice.PROPORTIONAL_TIME_DIVISION;
			} else if (skillChoice.equals(skillChoiceSet[1])) {
				return Strategy.SkillChoice.GREEDY_ASSIGNMENT_BY_TASK;
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

	public TaskMinMaxChoice getTaskMaxMinStrategy() {
		if (type == SINGULAR) {
			if (taskMinMaxChoice.equals("maxmax")) {
				return Strategy.TaskMinMaxChoice.ARGMAX_ARGMAX;
			} else if (taskMinMaxChoice.equals("maxmin")) {
				return Strategy.TaskMinMaxChoice.ARGMAX_ARGMIN;
			} else if (taskMinMaxChoice.equals("minmax")) {
				return Strategy.TaskMinMaxChoice.ARGMIN_ARGMAX;
			} else if (taskMinMaxChoice.equals("minmin")) {
				return Strategy.TaskMinMaxChoice.ARGMIN_ARGMIN;
			}
		} else {
			assert false;
		}
		return null;
	}

	public String getTaskMinMaxChoice() {
		return taskMinMaxChoice;
	}

	public void setTaskMinMaxChoice(String taskMinMaxChoice) {
		this.taskMinMaxChoice = taskMinMaxChoice;
	}
	
	public void setTaskMinMaxChoice(ModelFactory modelFactory, String taskMinMaxChoice) {
		if (modelFactory.getFunctionality().isMultipleValidation()) {
			int intRandomized = RandomHelper.nextIntFromTo(0,
					taskMinMaxChoiceSet.length - 1);
			assert (intRandomized >= 0)
					&& (intRandomized <= taskChoiceSet.length - 1);
			this.taskMinMaxChoice = taskMinMaxChoiceSet[intRandomized];
		} else
			this.taskMinMaxChoice = taskMinMaxChoice;
	}

}
