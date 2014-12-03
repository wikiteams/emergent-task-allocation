package collaboration;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import logger.PjiitOutputter;

import org.apache.commons.lang3.SystemUtils;

import repast.simphony.random.RandomHelper;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public abstract class AgentSkillsPool {

	/*****************************************
	 * Input format of a .CSV file: 
	 * username, skill1, skill2, skill3 
	 * e.g.:
	 * 'fabpot', 'PHP', 'Shell', 'JavaScript'
	 *****************************************/
	private static String filename = 
			SystemUtils.IS_OS_LINUX ? "data/top-users-final.csv"
			: "data\\top-users-final.csv";
	private static String filename_ext = 
			SystemUtils.IS_OS_LINUX ? "data/users-and-their-pull-requests.csv"
			: "data\\users-and-their-pull-requests.csv";

	private enum DataSet {
		TOP_USERS, BRAIN_JAR;
	}

	private enum Method {
		TOP_ACTIVE, RANDOM_FROM_GENERAL_POOL;
	}

	private static LinkedHashMap<String, HashMap<Skill, Double>> skillSet = 
			new LinkedHashMap<String, HashMap<Skill, Double>>();
	private static SkillFactory skillFactory = new SkillFactory();

	public static void instantiate(String method) {
		if (method.toUpperCase().equals("TOP_USERS")
				|| method.toUpperCase().equals("STATIC_PULL_REQUESTS"))
			instantiate(DataSet.TOP_USERS);
		if (method.toUpperCase().equals("BRAIN_JAR"))
			instantiate(DataSet.BRAIN_JAR);
	}

	public static void clear() {
		skillSet.clear();
	}

	public static void instantiate(DataSet method) {
		if (method == DataSet.TOP_USERS) {
			try {
				parseCsvTopUsers(true);
				parseCsvUsersByPushes();
				AgentSkillsFrequency.tasksCheckSum = AgentSkillsUtils
						.checksum(skillSet);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		say("Initialized Agent Skills Matrix");
	}

	/*
	 * Here is parsing real data - top active 960 GitHub users and their 3 most
	 * often used skills. No random character of this method.
	 * 
	 * @since 1.1
	 */
	private static void parseCsvTopUsers(boolean nickOnly) throws IOException,
			FileNotFoundException {
		CSVReader reader = new CSVReader(new FileReader(filename), ',', '\'');
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			if (nickOnly) {
				skillSet.put(nextLine[0], new HashMap<Skill, Double>());
			} else {
				HashMap<Skill, Double> l = new HashMap<Skill, Double>();
				for (int i = 1; i < nextLine.length; i++) {
					l.put(skillFactory.getSkill(nextLine[i]), null);
					say("Parsed from CSV: " + nextLine[i]);
				}
				skillSet.put(nextLine[0], l);
			}
		}
		reader.close();
	}

	/*
	 * Here is parsing real data - pull requests of top active 960 GitHub users
	 * and their used skills [1..n]
	 * 
	 * @since 1.2
	 */
	private static void parseCsvUsersByPushes() throws IOException,
			FileNotFoundException {
		CSVReader reader = new CSVReader(new FileReader(filename_ext), ',',
				CSVWriter.NO_QUOTE_CHARACTER, 1);
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			String user = nextLine[3];
			if (nextLine[2].trim().equals("null"))
				continue;
			Skill s = skillFactory.getSkill(nextLine[2]);
			Double value = Double.parseDouble(nextLine[1]);
			say("user:" + user + " skill:" + s + " value:" + value);
			addExtSkill(user, s, value);
		}
		reader.close();
	}

	private static void addExtSkill(String user, Skill skill, Double value) {
		HashMap<Skill, Double> h = skillSet.get(user);
		if (h == null) {
			skillSet.put(user, new HashMap<Skill, Double>());
			h = skillSet.get(user);
		}
		Double x = h.get(skill);
		if (x == null) {
			h.put(skill, value);
		} else {
			x += value;
			h.put(skill, x);
		}
		skillSet.put(user, h);
	}

	public static void fillWithSkills(Agent agent) {
		if (SimulationParameters.fillAgentSkillsMethod.toUpperCase().equals(
				"RANDOM_FROM_GENERAL_POOL"))
			fillWithSkills(agent, Method.RANDOM_FROM_GENERAL_POOL);
		else if (SimulationParameters.fillAgentSkillsMethod.toUpperCase()
				.equals("TOP_ACTIVE"))
			fillWithSkills(agent, Method.TOP_ACTIVE);
	}

	public static void fillWithSkills(Agent agent, Method method) {
		if (method == Method.TOP_ACTIVE) {

			// here we fill agent skills with experience
			// analysed by their pushed, no random character here
			// all taken from users-and-their-pull-requests.csv

			HashMap<Skill, Double> iterationSkills = AgentSkillsUtils
					.getByIndex(skillSet, agent.getId());
			for (Skill iterationSkill : iterationSkills.keySet()) {
				AgentInternals builtAgentInternals = new AgentInternals(
						iterationSkill, new Experience(
								iterationSkills.get(iterationSkill), 8500));
				agent.addSkill(iterationSkill.getName(), builtAgentInternals);
			}
		} else if (method == Method.RANDOM_FROM_GENERAL_POOL) {
			// randomise how many skills
			int how_many = RandomHelper.nextIntFromTo(0,
					SimulationParameters.agentSkillsPoolRandomize1 - 1);
			ArrayList<Skill> skillsForConsider = new ArrayList<Skill>();
			for (int i = 0; i < how_many; i++) {
				Skill s1 = null;
				while (true) {
					s1 = skillFactory.getRandomSkill();
					if (skillsForConsider.contains(s1)) {
						continue;
					} else {
						skillsForConsider.add(s1);
						break;
					}
				}
				int topExperience = SimulationParameters.agentSkillsMaximumExperience;
				int experienceRandomized = RandomHelper.nextIntFromTo(0,
						topExperience - 1);
				// double exp__d = (double)exp__ / (double)top;
				// dont do that, we want to persist integer experience
				// not result of delta(exp) function !
				say("exp randomized to: " + experienceRandomized);
				AgentInternals builtAgentInternals = new AgentInternals(s1,
						new Experience(experienceRandomized, topExperience));
				agent.addSkill(s1.getName(), builtAgentInternals);
			}
		}
	}

	private static void say(String s) {
		PjiitOutputter.say(s);
	}

}
