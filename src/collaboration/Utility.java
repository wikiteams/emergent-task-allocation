package collaboration;

import java.util.Collection;

import utils.ObjectsHelper;

public class Utility {

	public enum UtilityType {
		NormalizedSum, MaxSkill, WorstSkill
	}

	public static Double getNormalizedSum( 
			Collection<AgentInternals> agentInternals) {
		Double sum = 0d;
		for (AgentInternals currentSkill : agentInternals) {
			sum += currentSkill.getExperience().getDelta();
		}
		return sum / SkillFactory.getInstance().countAllSkills();
	}

	public static Double getBestSkill(Collection<AgentInternals> agentInternals) {
		Double result = null;
		for (AgentInternals currentSkill : agentInternals) {
			double delta = currentSkill.getExperience().getDelta();
			if (ObjectsHelper.is2ndHigher(result, delta)) {
				result = delta;
			}
		}
		assert result != null;
		return result;
	}
	
	public static Double getWorstSkill(Collection<AgentInternals> agentInternals) {
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

}
