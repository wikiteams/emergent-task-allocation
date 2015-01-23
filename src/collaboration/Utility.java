package collaboration;

import java.util.Collection;

import utils.ObjectsHelper;

public class Utility {

	private static final double epsilon = 0.05;

	public static Double getLearningUtility(
			Collection<AgentInternals> agentInternals) {
		return getLeftLearningUtility(agentInternals)
				+ getRightLearningUtility(agentInternals);
	}

	public static Double getLeftLearningUtility(
			Collection<AgentInternals> agentInternals) {
		Double result = null;
		for (AgentInternals currentSkill : agentInternals) {
			double delta = currentSkill.getExperience().getDelta();
			if (ObjectsHelper.is2ndLower(result, delta)) {
				result = delta;
			}
		}
		assert result != null;
		return result;
	}

	public static Double getRightLearningUtility(
			Collection<AgentInternals> agentInternals) {
		double result = 0;
		for (AgentInternals currentSkill : agentInternals) {
			result += currentSkill.getExperience().getDelta();
		}
		return epsilon * result;
	}

}
