package utils;

import intelligence.UtilityTypes;
import collaboration.SimulationParameters;
import collaboration.Utility.UtilityType;

public class UtilityFactory {

	public static void randomizeUtility(UtilityType[] utilityTypes) {
		UtilityType chosen = ObjectsHelper.randomFrom(utilityTypes);
		if (chosen.equals(UtilityTypes.LearningSkills)) {
			SimulationParameters.isAgentOrientedUtility = true;
			SimulationParameters.isTaskOrientedUtility = false;
		} else if (chosen.equals(UtilityTypes.LeftLearningSkills)) {
			SimulationParameters.isAgentOrientedUtility = true;
			SimulationParameters.isTaskOrientedUtility = false;
		} else if (chosen.equals(UtilityTypes.RightLearningSkills)) {
			SimulationParameters.isAgentOrientedUtility = true;
			SimulationParameters.isTaskOrientedUtility = false;
		}
		SimulationParameters.utilityType = chosen;
	}

}
