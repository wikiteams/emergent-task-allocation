package collaboration;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class AgentSkills {

	private Map<String, AgentInternals> startSkills;
	private Map<String, AgentInternals> skills;

	public AgentSkills() {
		startSkills = new HashMap<String, AgentInternals>();
		skills = new HashMap<String, AgentInternals>();
	}

	public Map<String, AgentInternals> getStartSkills() {
		return startSkills;
	}

	public void setStartSkills(Map<String, AgentInternals> startSkills) {
		this.startSkills = startSkills;
	}

	public Map<String, AgentInternals> getSkills() {
		return skills;
	}

	public void setSkills(Map<String, AgentInternals> skills) {
		this.skills = skills;
	}

	public void reset() {
		skills = deepCopy(startSkills);
	}

	private Map<String, AgentInternals> deepCopy(
			Map<String, AgentInternals> skills) {
		Map<String, AgentInternals> result = new HashMap<String, AgentInternals>();
		for (Entry<String, AgentInternals> entry : skills.entrySet()) {

		}
		return result;
	}

}
