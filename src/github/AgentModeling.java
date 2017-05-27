package github;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.SystemUtils;

import au.com.bytecode.opencsv.CSVReader;
import collaboration.Agent;
import collaboration.AgentInternals;
import collaboration.CollaborationBuilder;
import collaboration.Experience;
import collaboration.SimulationAdvancedParameters;
import collaboration.Skill;
import collaboration.Skills;

public class AgentModeling {

	private static LinkedHashMap<String, HashMap<Skill, Experience>> skillSet = 
			new LinkedHashMap<String, HashMap<Skill, Experience>>();

	private final static String filename = SystemUtils.IS_OS_LINUX ? 
			"data/agents-model/results.csv"
			: "data\\agents-model\\results.csv";

	public static void clear() {
		skillSet.clear();
	}

	public static void instantiate() {
		try {
			parseCsvTopUsers();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Initialized Agent Skills Matrix");
	}

	private static void parseCsvTopUsers() throws IOException,
			FileNotFoundException {
		Integer counter = 0;
		Map<Skill, Integer> maximums = calculateMaximums(filename);
		CSVReader reader = new CSVReader(new FileReader(filename), ';', '\"');
		String[] nextLine;
		String previousId = "-1";
		while ((nextLine = reader.readNext()) != null) {
			String id = nextLine[0];
			String nick = nextLine[1] + counter;
			String language = nextLine[2];
			int workDone = Integer.parseInt(nextLine[3]);
			if (previousId.equals(id)) {
				HashMap<Skill, Experience> l = skillSet.get(nick);
				System.out.println("Parsed from CSV new language to existing person: " + nick
						+ " - " + language);
				Skill skill = ((Skills) CollaborationBuilder.skills).getSkill(language);
				Experience experience = calculateExperience(workDone,
						maximums.get(skill));
				l.put(skill, experience);
			} else {
				// add new user
				counter++;
				nick = nextLine[1] + counter;
				HashMap<Skill, Experience> l = new HashMap<Skill, Experience>();
				System.out.println("Parsed from CSV new person: " + nick + " - " + language);
				Skill skill = ((Skills) CollaborationBuilder.skills).getSkill(language);
				Experience experience = calculateExperience(workDone,
						maximums.get(skill));
				l.put(skill, experience);
				skillSet.put(nick, l);
			}
			previousId = id;
		}
		assert skillSet.size() > 99;
		reader.close();
	}

	private static Map<Skill, Integer> calculateMaximums(String filename)
			throws NumberFormatException, IOException {
		Map<Skill, Integer> result = new HashMap<Skill, Integer>();

		CSVReader reader = new CSVReader(new FileReader(filename), ';', '\"');
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			String language = nextLine[2];
			int workDone = Integer.parseInt(nextLine[3]);
			Skill skill = ((Skills) CollaborationBuilder.skills).getSkill(language);
			if (result.containsKey(skill)) {
				if (result.get(skill) < workDone) {
					result.put(skill, workDone);
				}
			} else {
				result.put(skill, workDone);
			}
		}
		reader.close();
		return result;
	}

	private static Experience calculateExperience(int experience, int maximum) {
		return new Experience(
				experience,
				maximum < SimulationAdvancedParameters.lowestTop ? 
						SimulationAdvancedParameters.lowestTop
						: maximum);
	}

	public static void fillWithSkills(Agent agent) {
		HashMap<Skill, Experience> iterationSkills = AgentModelingUtils
				.getByIndexWithIterate(skillSet, agent.getId() - 1);
		for (Skill iterationSkill : iterationSkills.keySet()) {
			AgentInternals builtAgentInternals = new AgentInternals(
					iterationSkill, iterationSkills.get(iterationSkill));
			agent.addSkill(iterationSkill.getName(), builtAgentInternals);
		}
	}

}
