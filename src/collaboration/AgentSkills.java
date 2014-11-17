package collaboration;

import java.util.HashMap;
import java.util.Map;

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

}
