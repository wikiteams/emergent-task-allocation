package github;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import logger.PjiitOutputter;

import org.apache.commons.lang3.SystemUtils;

import au.com.bytecode.opencsv.CSVReader;
import collaboration.Skill;
import collaboration.SkillFactory;

public class AgentModeling {
	
	private static LinkedHashMap<String, HashMap<Skill, Double>> skillSet = 
			new LinkedHashMap<String, HashMap<Skill, Double>>();
	private static SkillFactory skillFactory = SkillFactory.getInstance();
	
	private final static String filename = SystemUtils.IS_OS_LINUX ? 
			"data/agents-model/results.csv"
			: "data\\agents-model\\results.csv";

	public static void instantiate(String method) {
		if (method.toUpperCase().equals("OSRC")) {
			say("Reading data and creating [Agent's Skill Pool]...");
			instantiate();
		}
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
		CSVReader reader = new CSVReader(new FileReader(filename), ';', '\"');
		String[] nextLine;
		String previousId = "-1";
		while ((nextLine = reader.readNext()) != null) {
			String id = nextLine[0];
			String nick = nextLine[1];
			String language = nextLine[2];
			double workDone = Double.parseDouble(nextLine[3]);
			String cluster = nextLine[4];
			if (previousId.equals(id)){
				HashMap<Skill, Double> l = skillSet.get(nick);
				say("Parsed from CSV new language to existing person: " 
						+ nick + " - " + language);
				l.put(skillFactory.getSkill(language), workDone);
				skillSet.put(nick, l);
			}else{
				// add new user
				HashMap<Skill, Double> l = new HashMap<Skill, Double>();
				say("Parsed from CSV new person: " + nick + " - " + language);
				l.put(skillFactory.getSkill(language), workDone);
				skillSet.put(nick, l);
			}
			previousId = id;
		assert skillSet.size() > 99;
		}
		reader.close();
	}

	private static void say(String s) {
		PjiitOutputter.say(s);
	}

}
