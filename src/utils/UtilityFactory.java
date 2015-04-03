package utils;

import intelligence.UtilityTypes;
import collaboration.SimulationParameters;
import collaboration.Utility.UtilityType;

/**
 * Setting the utility function of the evolutionary mechanism
 * 
 * @author Oskar Jarczyk
 * @since 2.0.9
 * @version 2.0.10
 */
public class UtilityFactory {

	public static void randomizeUtility(UtilityType[] utilityTypes) {
		UtilityType chosen = ObjectsHelper.randomFrom(utilityTypes);
		setUtility(chosen);
	}
	
	public static void setUtility(UtilityType chosen){
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
