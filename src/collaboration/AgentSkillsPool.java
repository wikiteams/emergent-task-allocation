package collaboration;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

import org.apache.commons.lang3.SystemUtils;

import repast.simphony.random.RandomHelper;
import logger.PjiitOutputter;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public abstract class AgentSkillsPool {

	private static String filenameMostOftenStarred = SystemUtils.IS_OS_LINUX ? "data/users_top_stars.csv"
			: "data\\users_top_stars.csv";

	/***
	 * Input format of a .CSV file:
	 * 
	 * username, skill1, skill2, skill3
	 * 
	 * i.e. 'fabpot', 'PHP', 'Shell', 'JavaScript'
	 */
	private static String filename = SystemUtils.IS_OS_LINUX ? "data/top-users-final.csv"
			: "data\\top-users-final.csv";
	private static String filename_ext = SystemUtils.IS_OS_LINUX ? "data/users-and-their-pull-requests.csv"
			: "data\\users-and-their-pull-requests.csv";

	private enum DataSet {
		AT_LEAST_1_COMMIT, MOST_OFTEN_STARRED, TOPREPOS_AND_THEIRUSERS, TOPUSERS_AND_THEIRREPOS, TOPREPOS_AND_TOPUSERS, PUSHES_BY_LANGUAGES, SEVERANCE_FROM_MIDDLE, MOST_COMMON_TECHNOLOGY, ALL_LANGUAGES, TOP_USERS, _200_LANGUAGES;
	}

	private enum Method {
		TOP_ACTIVE, RANDOM_FROM_GENERAL_POOL, RANDOM;
	}

	/***
	 * <String:user, {<skill, intensivity>}>
	 */
	private static LinkedHashMap<String, HashMap<Skill, Double>> skillSet = new LinkedHashMap<String, HashMap<Skill, Double>>();
	private static SkillFactory skillFactory = new SkillFactory();

	public static void instantiate(String method) {
		if (method.toUpperCase().equals("MOST_OFTEN_STARRED"))
			instantiate(DataSet.MOST_OFTEN_STARRED);
		else if (method.toUpperCase().equals("AT_LEAST_1_COMMIT"))
			instantiate(DataSet.AT_LEAST_1_COMMIT);
		else if (method.toUpperCase().equals("TOPUSERS_AND_THEIRREPOS"))
			instantiate(DataSet.TOPUSERS_AND_THEIRREPOS);
		else if (method.toUpperCase().equals("TOPREPOS_AND_THEIRUSERS"))
			instantiate(DataSet.TOPREPOS_AND_THEIRUSERS);
		else if (method.toUpperCase().equals("TOPREPOS_AND_TOPUSERS"))
			instantiate(DataSet.TOPREPOS_AND_TOPUSERS);
		else if (method.toUpperCase().equals("PUSHES_BY_LANGUAGES"))
			instantiate(DataSet.PUSHES_BY_LANGUAGES);
		else if (method.toUpperCase().equals("TOP_USERS"))
			instantiate(DataSet.TOP_USERS);
		else if (method.toUpperCase().equals("200_LANGUAGES"))
			instantiate(DataSet._200_LANGUAGES);
	}

	public static void clear() {
		skillSet.clear();
	}

	public static void instantiate(DataSet method) {
		if (method == DataSet.MOST_OFTEN_STARRED) {
			try {
				parseCsvMostOftenStarred();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (method == DataSet.TOP_USERS) {
			try {
				parseCsvTopUsers(true);
				parseCsvUsersByPushes();
				AgentSkillsFrequency.tasksCheckSum = checksum(skillSet);
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
		} else if (method == DataSet.PUSHES_BY_LANGUAGES) {
			try {
				parseCsvTopUsers(true);
				parseCsvUsersByPushes();
				AgentSkillsFrequency.tasksCheckSum = checksum(skillSet);
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
		say("initialized TaskSkillsPool");
	}

	private static void parseCsvMostOftenStarred() throws IOException,
			FileNotFoundException {
		CSVReader reader = new CSVReader(new FileReader(
				filenameMostOftenStarred), ',', '\'');
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			// if (nickOnly) {
			// skillSet.put(nextLine[0], new HashMap<Skill, Double>());
			// } else {
			// HashMap<Skill, Double> l = new HashMap<Skill, Double>();
			// for (int i = 1; i < nextLine.length; i++) {
			// l.put(skillFactory.getSkill(nextLine[i]), null);
			// say("Parsed from CSV: " + nextLine[i]);
			// }
			// skillSet.put(nextLine[0], l);
			// }
		}
		reader.close();
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

	public static HashMap<Skill, Double> choseRandom() {
		Random generator = new Random();
		int i = generator.nextInt(skillSet.size());
		return getByIndex(skillSet, i);
	}

	@SuppressWarnings("unchecked")
	public static HashMap<Skill, Double> getByIndex(
			LinkedHashMap<String, HashMap<Skill, Double>> hMap, int index) {
		return (HashMap<Skill, Double>) hMap.values().toArray()[index];
	}

	public static void fillWithSkills(Agent agent) {
		if (SimulationParameters.fillAgentSkillsMethod.toUpperCase().equals(
				"RANDOM"))
			fillWithSkills(agent, Method.RANDOM);
		else if (SimulationParameters.fillAgentSkillsMethod.toUpperCase()
				.equals("RANDOM_FROM_GENERAL_POOL"))
			fillWithSkills(agent, Method.RANDOM_FROM_GENERAL_POOL);
		else if (SimulationParameters.fillAgentSkillsMethod.toUpperCase()
				.equals("TOP_ACTIVE"))
			fillWithSkills(agent, Method.TOP_ACTIVE);
	}

	public static void fillWithSkills(Agent agent, Method method) {
		if (method == Method.RANDOM) {
			throw new UnsupportedOperationException();
		} else if (method == Method.TOP_ACTIVE) {

			// here we fill agent skills with experience
			// analyzed by their pushed, no random character here
			// all taken from users-and-their-pull-requests.csv

			HashMap<Skill, Double> iterationSkills = getByIndex(skillSet,
					agent.getId());
			for (Skill iterationSkill : iterationSkills.keySet()) {
				AgentInternals builtAgentInternals = new AgentInternals(
						iterationSkill, new Experience(
								iterationSkills.get(iterationSkill), 8500));
				agent.addSkill(iterationSkill.getName(), builtAgentInternals);
			}
		} else if (method == Method.RANDOM_FROM_GENERAL_POOL) {
			// randomize HOW MANY SKILLS
			// Random generator = new Random();
			int how_many = RandomHelper.nextIntFromTo(0,
					SimulationParameters.agentSkillsPoolRandomize1 - 1);
			ArrayList<Skill> __skills = new ArrayList<Skill>();
			for (int i = 0; i < how_many; i++) {
				Skill s1 = null;
				while (true) {
					s1 = skillFactory.getRandomSkill();
					if (__skills.contains(s1)) {
						continue;
					} else {
						__skills.add(s1);
						break;
					}
				}
				// Random generator_exp = new Random();
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
		// ArrayList skill = getByIndex(skillSet, COUNTABLE);
		// Experience experience = new Experience();
		// AgentInternals agentInternals = new AgentInternals(skill,
		// experience);
		// agent.addSkill(key, agentInternals);
		// say("Agent " + agent + " filled with skills");
	}

	private static BigInteger checksum(Object obj) throws IOException,
			NoSuchAlgorithmException {

		if (obj == null) {
			return BigInteger.ZERO;
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(obj);
		oos.close();

		MessageDigest m = MessageDigest.getInstance("MD5");
		m.update(baos.toByteArray());

		return new BigInteger(1, m.digest());
	}

	private static void say(String s) {
		PjiitOutputter.say(s);
	}

}
