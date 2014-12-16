package collaboration;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/***
 * 
 * Represents Agent's skills (knowledge of programming language) and the initial
 * value as used later in evolutionary model
 * 
 * @version 2.0.6
 * @author Oskar Jarczyk
 * 
 */
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

	public void removeSkill(String key) {
		this.skills.remove(key);
	}

	public void reset() {
		skills = deepCopy(startSkills);
	}

	public void backup() {
		assert startSkills != null;
		startSkills.clear();
		for (Entry<String, AgentInternals> entry : skills.entrySet()) {
			startSkills.put(entry.getKey(), entry.getValue().deepCopy());
		}
	}

	private Map<String, AgentInternals> deepCopy(
			Map<String, AgentInternals> source) {
		Map<String, AgentInternals> result = new HashMap<String, AgentInternals>();
		for (Entry<String, AgentInternals> entry : source.entrySet()) {
			result.put(entry.getKey(), entry.getValue().deepCopy());
		}
		return result;
	}

}
