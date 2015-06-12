package collaboration;

import java.util.Collection;

import utils.ObjectsHelper;

public class Utility {

	public enum UtilityType {
		LearningSkills, LeftLearningSkills, RightLearningSkills, ImpactFactor, ImpactFactorMax
	}

	public static final int LEFT_MIN = 1;
	// public static final int LEFT_AVG = 2;
	public static final int LEFT_MAX = 3;

	private static final double epsilon = 0.05;

	public static Double getLearningUtility(
			Collection<AgentInternals> agentInternals) {
		return getLeftLearningUtility(agentInternals)
				+ getRightLearningUtility(agentInternals);
	}

	public static Double getLeftLearningUtility(
			Collection<AgentInternals> agentInternals) {
		return getLeftLearningUtility(
				SimulationAdvancedParameters.minimum ? LEFT_MIN : LEFT_MAX,
				agentInternals);
	}

	public static Double getLeftLearningUtility(int type,
			Collection<AgentInternals> agentInternals) {
		Double result = null;
		for (AgentInternals currentSkill : agentInternals) {
			double delta = currentSkill.getExperience().getDelta();
			if (type == LEFT_MIN) {
				if (ObjectsHelper.is2ndLower(result, delta)) {
					result = delta;
				}
			} else if (type == LEFT_MAX) {
				if (ObjectsHelper.is2ndHigher(result, delta)) {
					result = delta;
				}
			}
		}
		assert result != null;
		return result;
	}

	public static Double getRightLearningUtility(
			Collection<AgentInternals> agentInternals) {
		return getRightLearningUtility(true, agentInternals);
	}

	public static Double getRightLearningUtility(boolean useEpsilon,
			Collection<AgentInternals> agentInternals) {
		double result = 0;
		for (AgentInternals currentSkill : agentInternals) {
			result += currentSkill.getExperience().getDelta();
		}
		return (useEpsilon ? epsilon : 1) * result;
	}

}
