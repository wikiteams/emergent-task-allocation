package github;

import repast.simphony.random.RandomHelper;
import collaboration.SimulationParameters;

/**
 * Provides final unchangeable sets of input datasets (tasks and skills)
 * 
 * @author Oskar
 * @since 1.4
 * @version 1.4.1
 */
public class DataSetProvider {

	boolean dataSetAll = false;

	private String[] agent_rand_allowed = { "TOP_USERS" };
	private String[] task_rand_allowed = { "TOP_REPOSITORIES" };

	public DataSetProvider(boolean dataSetAll) {
		this.dataSetAll = dataSetAll;
	}

	public String getAgentSkillDataset() {
		if (SimulationParameters.onlyOneBasicDataset) {
			return agent_rand_allowed[0];
		} else if (dataSetAll) {
			return agent_rand_allowed[RandomHelper.nextIntFromTo(0,
					agent_rand_allowed.length - 1)];
		} else {
			return SimulationParameters.agentSkillPoolDataset;
		}
	}

	public String getTaskSkillDataset() {
		if (SimulationParameters.onlyOneBasicDataset) {
			return task_rand_allowed[0];
		} else if (dataSetAll) {
			return task_rand_allowed[RandomHelper.nextIntFromTo(0,
					task_rand_allowed.length - 1)];
		} else {
			return SimulationParameters.taskSkillPoolDataset;
		}
	}

}
