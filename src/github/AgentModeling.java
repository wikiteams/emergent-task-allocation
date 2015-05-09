package github;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import logger.VerboseLogger;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.math3.analysis.function.Sigmoid;

import au.com.bytecode.opencsv.CSVReader;
import collaboration.Agent;
import collaboration.AgentInternals;
import collaboration.Experience;
import collaboration.Skill;
import collaboration.SkillFactory;

public class AgentModeling {

	private static LinkedHashMap<String, HashMap<Skill, Experience>> skillSet = 
			new LinkedHashMap<String, HashMap<Skill, Experience>>();
	private static SkillFactory skillFactory = SkillFactory.getInstance();

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
		say("Initialized Agent Skills Matrix");
	}

	private static void parseCsvTopUsers() throws IOException,
			FileNotFoundException {
		Integer counter = 0;
		CSVReader reader = new CSVReader(new FileReader(filename), ';', '\"');
		String[] nextLine;
		String previousId = "-1";
		while ((nextLine = reader.readNext()) != null) {
			String id = nextLine[0];
			String nick = nextLine[1] + counter;
			String language = nextLine[2];
			int workDone = Integer.parseInt(nextLine[3]);
			int cluster = Integer.parseInt(nextLine[4]);
			if (previousId.equals(id)) {
				HashMap<Skill, Experience> l = skillSet.get(nick);
				say("Parsed from CSV new language to existing person: " + nick
						+ " - " + language);
				Experience experience = calculateExperience(workDone, cluster);
				l.put(skillFactory.getSkill(language), experience);
				// skillSet.put(nick, l);
			} else {
				// add new user
				counter++;
				nick = nextLine[1] + counter;
				HashMap<Skill, Experience> l = new HashMap<Skill, Experience>();
				say("Parsed from CSV new person: " + nick + " - " + language);
				Experience experience = calculateExperience(workDone, cluster);
				l.put(skillFactory.getSkill(language), experience);
				skillSet.put(nick, l);
			}
			previousId = id;
		}
		assert skillSet.size() > 99;
		reader.close();
	}

	private static Experience calculateExperience(int experience, int cluster) {
		if (experience / cluster > 0.99) {
			return new Experience(experience, experience + 1);
		} else if (experience / cluster > 0.9) {
			double part = new Sigmoid().value(6.0 * (experience / cluster));
			assert part <= 1.0;
			return new Experience(experience * part, experience);
		} else {
			return new Experience(experience, cluster);
		}
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

	private static void say(String s) {
		VerboseLogger.say(s);
	}

}
