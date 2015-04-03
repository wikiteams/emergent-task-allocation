package github;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import logger.PjiitOutputter;

import org.apache.commons.lang3.SystemUtils;

import repast.simphony.random.RandomHelper;
import utils.CharacterConstants;
import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import cern.jet.random.Poisson;
import collaboration.SimulationParameters;
import collaboration.Skill;
import collaboration.SkillFactory;
import collaboration.Task;
import collaboration.TaskInternals;
import collaboration.WorkUnit;

/**
 * A factory for creating of real skill data for COIN TASKS data taken mostly
 * from GitHub portal, possible randomization for bigger variation of results
 * 
 * @since 1.0
 * @version 1.4.1
 * @author Oskar Jarczyk
 * 
 */
@Deprecated
public abstract class TaskSkillsPool {

	private static String filenameFrequencySkills = 
			SystemUtils.IS_OS_LINUX ? "data/skills-probability.csv"
			: "data\\skills-probability.csv";
	private static String filenameGoogleSkills = 
			SystemUtils.IS_OS_LINUX ? "data/tasks-skills.csv"
			: "data\\tasks-skills.csv";
	private static String filenameGithubClusters = 
			SystemUtils.IS_OS_LINUX ? "data/github_clusters.csv"
			: "data\\github_clusters.csv";

	private enum DataSet {
		AT_LEAST_1_COMMIT, MOST_OFTEN_STARRED, 
		TOPREPOS_AND_THEIRUSERS, TOPUSERS_AND_THEIRREPOS, 
		TOPREPOS_AND_TOPUSERS, PUSHES_BY_LANGUAGES, 
		SEVERANCE_FROM_MIDDLE, MOST_COMMON_TECHNOLOGY, 
		ALL_LANGUAGES, TOP_REPOSITORIES, _200_LANGUAGES;
	}

	public enum Method {
		STATIC_FREQUENCY_TABLE, GOOGLE_BIGQUERY_MINED, GITHUB_CLUSTERIZED;
	}

	public TaskSkillFrequency skillTypicalFrequency;
	public static volatile int iterator = 0;

	public static void clear() {
		singleSkillSet.clear();
		skillSetMatrix.clear();
		skillSetArray.clear();
		iterator = 0;
	}

	private static LinkedHashMap<String, Skill> singleSkillSet = 
			new LinkedHashMap<String, Skill>();
	private static LinkedHashMap<Repository, HashMap<Skill, Double>> skillSetMatrix = 
			new LinkedHashMap<Repository, HashMap<Skill, Double>>();
	private static ArrayList<HashMap<Skill, Double>> skillSetArray = 
			new ArrayList<HashMap<Skill, Double>>();
	private static SkillFactory skillFactory = SkillFactory.getInstance();

	public static int static_frequency_counter = 0;

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
		else if (method.toUpperCase().equals("ALL_REPOSITORIES"))
			instantiate(DataSet.TOP_REPOSITORIES);
		else if (method.toUpperCase().equals("200_LANGUAGES"))
			instantiate(DataSet._200_LANGUAGES);
	}

	/**
	 * Reads into simulation universe-creator the Tasks which will be used later
	 * in the simulation, depends on the number of tasks needed, they will be
	 * taken later from the top
	 * 
	 * @param dataset
	 */
	public static void instantiate(DataSet dataset) {
		if (dataset == DataSet._200_LANGUAGES) {
			try {
				sanity("Inside instantiate(DataSet._200_LANGUAGES)");
				parseCsvStatic();
			} catch (FileNotFoundException e) {
				System.err.println(e.getMessage());
				say("File not found!");
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println(e.getMessage());
				say("Input / Output Exception! Details: " + e.getMessage());
				e.printStackTrace();
			}
		} else if (dataset == DataSet.TOP_REPOSITORIES) {
			try {
				sanity("Inside [instantiate(DataSet.TOP_REPOSITORIES)]");
				sanity("Lunching [parseCsvCluster()]");
				parseCsvCluster();
			} catch (FileNotFoundException e) {
				System.err.println(e.getMessage());
				say("File not found!");
				e.printStackTrace();
			} catch (NoSuchAlgorithmException nsae) {
				System.err.println(nsae.getMessage());
				say("No such algorithm exception. Details: "
						+ nsae.getMessage());
				nsae.printStackTrace();
			} catch (IOException e) {
				System.err.println(e.getMessage());
				say("Input / Output Exception! Details: " + e.getMessage());
				e.printStackTrace();
			}
		} else if (dataset == DataSet.PUSHES_BY_LANGUAGES) {
			try {
				parseCsvGoogle();
			} catch (FileNotFoundException e) {
				System.err.println(e.getMessage());
				say("File not found!");
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println(e.getMessage());
				say("Input / Output Exception! Details: " + e.getMessage());
				e.printStackTrace();
			}
		}
		say("Initialized [TaskSkillsPool]");
	}

	private static void parseCsvStatic() throws IOException,
			FileNotFoundException {
		CSVReader reader = new CSVReader(
				new FileReader(filenameFrequencySkills), ',',
				CharacterConstants.DEFAULT_EMPTY_CHARACTER, 1);
		String[] nextLine;
		long count = 0;
		while ((nextLine = reader.readNext()) != null) {
			Skill skill = skillFactory.getSkill(nextLine[0]);
			say("Processing [Skill] + " + nextLine[0]);
			assert skill != null;
			skill.setCardinalProbability(Integer.parseInt(nextLine[1]));
			count += skill.getCardinalProbability();
			singleSkillSet.put(skill.getName(), skill);
		}
		for (Skill skill : singleSkillSet.values()) {
			skill.setProbability(skill.getCardinalProbability() / count);
		}
		reader.close();
	}

	private static void parseCsvGoogle() throws IOException,
			FileNotFoundException {
		CSVReader reader = new CSVReader(new FileReader(filenameGoogleSkills),
				',', CSVWriter.NO_QUOTE_CHARACTER, 1);
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			String repo = nextLine[0];
			if (nextLine[2].trim().equals("null"))
				continue;
			Skill s = skillFactory.getSkill(nextLine[2].trim());
			if (s == null)
				continue;
			Double value = Double.parseDouble(nextLine[1]);
			say("repo:" + repo + " skill:" + s + " value:" + value);
			addRepoSkill(repo, s, value);
		}
		reader.close();
	}

	private static void addRepoSkill(String repo, Skill skill, Double value) {
		Repository r = new Repository(repo);
		if (skillSetMatrix.containsKey(r)) {
			HashMap<Skill, Double> hs = skillSetMatrix.get(r);
			if (hs.containsKey(skill)) {
				Double v = hs.get(skill);
				v += value;
				hs.put(skill, v);
			} else {
				hs.put(skill, value);
			}
			skillSetMatrix.put(r, hs);
		} else {
			HashMap<Skill, Double> hs = new HashMap<Skill, Double>();
			hs.put(skill, value);
			skillSetMatrix.put(r, hs);
		}
	}

	private static void parseCsvCluster() throws IOException,
			FileNotFoundException, NoSuchAlgorithmException {
		say("parseCsvCluster() executes work..");
		CSVReader reader = new CSVReader(
				new FileReader(filenameGithubClusters), ',',
				CSVParser.DEFAULT_QUOTE_CHARACTER);
		String[] nextLine;
		nextLine = reader.readNext();

		List<Skill> headerSkills = new ArrayList<Skill>();
		for (int i = 0; i < 10; i++) {
			Skill skill = skillFactory.getSkill(nextLine[i].replace("sc_", "")
					.trim());
			headerSkills.add(skill);
		}

		while ((nextLine = reader.readNext()) != null) {
			Repository repo = new Repository(nextLine[11], nextLine[12]);
			HashMap<Skill, Double> hmp = parseCluster(nextLine[10]);
			for (int i = 0; i < 10; i++) {
				double howMuch = Double.parseDouble(nextLine[i]);
				if (!(howMuch > 0.))
					continue;
				if (howMuch > TaskSkillFrequency.MAX)
					continue;
				hmp.put(headerSkills.get(i), howMuch);
			}
			skillSetMatrix.put(repo, hmp);
		}
		reader.close();

		skillSetArray = sortBySkillLength(skillSetMatrix);

		TaskSkillFrequency.tasksCheckSum = checksum(skillSetMatrix);

		iterator = 0;
	}

	private static ArrayList<HashMap<Skill, Double>> sortBySkillLength(
			LinkedHashMap<Repository, HashMap<Skill, Double>> map) {
		List<HashMap<Skill, Double>> l = new ArrayList<HashMap<Skill, Double>>(
				map.values());
		Collections.sort(l, new Comparator<HashMap<Skill, Double>>() {
			public int compare(HashMap<Skill, Double> s1,
					HashMap<Skill, Double> s2) {
				return Integer.compare(s2.size(), s1.size());
			}
		});

		ArrayList<HashMap<Skill, Double>> sortedSkillSetArray = 
				new ArrayList<HashMap<Skill, Double>>();

		for (HashMap<Skill, Double> a : l) {
			Iterator<Entry<Repository, HashMap<Skill, Double>>> iter = map
					.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<Repository, HashMap<Skill, Double>> e = iter.next();
				if (e.getValue().equals(a)) {
					sortedSkillSetArray.add(e.getValue());
				}
			}
		}

		return sortedSkillSetArray;
	}

	private static HashMap<Skill, Double> parseCluster(String cluster) {
		HashMap<Skill, Double> r = new HashMap<Skill, Double>();

		if (cluster.contains("|")) {
			for (String s : cluster.split("\\|")) {
				String skillName = s.split(":")[0];
				String intensive = s.split(":")[1];
				r.put(skillFactory.getSkill(skillName),
						TaskSkillFrequency.frequency.get(intensive
								.toUpperCase()));
			}
		} else {
			String skillName = cluster.split(":")[0];
			String intensive = cluster.split(":")[1];
			r.put(skillFactory.getSkill(skillName),
					TaskSkillFrequency.frequency.get(intensive.toUpperCase()));
		}
		return r;
	}

	public static Skill choseRandomSkill() {
		int i = RandomHelper.nextIntFromTo(0, singleSkillSet.size() - 1);
		return getByIndexFromStr(singleSkillSet, i);
	}

	private static Skill getByIndexFromStr(LinkedHashMap<String, Skill> hMap,
			int index) {
		return (Skill) hMap.values().toArray()[index];
	}

	@SuppressWarnings("unchecked")
	private static HashMap<Skill, Double> getByIndex(
			LinkedHashMap<Repository, HashMap<Skill, Double>> hMap, int index) {
		return (HashMap<Skill, Double>) hMap.values().toArray()[index];
	}

	public static void fillWithSkills(Task task, int countAll) {
		if (SimulationParameters.taskSkillPoolDataset
				.equals("STATIC_FREQUENCY_TABLE")) {

			// random element exists here

			int random = 0 + static_frequency_counter++ / countAll;
			int randomIndexOfSkills = (random * skillSetMatrix.size()) - 1;
			if (randomIndexOfSkills < 0)
				randomIndexOfSkills = 0;
			HashMap<Skill, Double> skills = getByIndex(skillSetMatrix,
					randomIndexOfSkills);
			for (Skill skill : skills.keySet()) {
				Double d = skills.get(skill);
				WorkUnit w1 = new WorkUnit(RandomHelper.nextDoubleFromTo(0,
						d / 10));
				WorkUnit w2 = new WorkUnit(d);
				TaskInternals taskInternals = new TaskInternals(skill, w2, w1,
						task);
				task.addSkill(skill.getName(), taskInternals);
				say("Task " + task
						+ " filled with skills from tasks-skills.csv");
			}

		} else if (SimulationParameters.taskSkillPoolDataset.equals("RANDOM")) {

			// random element exists here

			int x = ((int) (RandomHelper.nextDouble() * 
					SimulationParameters.staticFrequencyTableSc)) + 1;
			for (int i = 0; i < x; i++) {
				Skill skill = choseRandomSkill();
				WorkUnit w1 = new WorkUnit(RandomHelper.nextIntFromTo(1,
						SimulationParameters.maxWorkRequired - 1));
				WorkUnit w2 = new WorkUnit(0);
				TaskInternals taskInternals = new TaskInternals(skill, w1, w2,
						task);
				task.addSkill(skill.getName(), taskInternals);
				say("Task " + task + " filled with skills randomly");
			}

		} else if (SimulationParameters.taskSkillPoolDataset
				.equals("GITHUB_CLUSTERIZED")) {

			if (SimulationParameters.gitHubClusterizedDistribution
					.toLowerCase().equals("clusters")) {

				// created task set will be the same for every execution
				// because of no randomness elements, use this
				// when you want to have random only pseudo-random behaviour
				// of step() method and analysing asynchronous decisions
				// made by agents through heuristics

				HashMap<Skill, Double> skillEntity = skillSetArray
						.get(iterator++);
				for (Skill skill : skillEntity.keySet()) {
					assert skillEntity.get(skill) > 0;
					WorkUnit workRequired = new WorkUnit(skillEntity.get(skill));
					WorkUnit workDone = new WorkUnit(skillEntity.get(skill)
							/ (iterator + 1));
					TaskInternals taskInternals = new TaskInternals(skill,
							workRequired, workDone, task);
					task.addSkill(skill.getName(), taskInternals);
					say("Task " + task + " filled with skills");
				}

			} else if (SimulationParameters.gitHubClusterizedDistribution
					.toLowerCase().equals("distribute")) {

				// random element exists here

				Poisson poisson = new Poisson(10,
						Poisson.makeDefaultGenerator());
				double d = poisson.nextDouble() / 20;

				HashMap<Skill, Double> skillSetG = getByIndex(skillSetMatrix,
						(int) (skillSetMatrix.size() * d));
				Iterator<?> it = skillSetG.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<?, ?> pairs = (Map.Entry<?, ?>) it.next();
					Skill key = (Skill) pairs.getKey();
					Double value = (Double) pairs.getValue();
					if (value > 0.0) {
						WorkUnit w1 = new WorkUnit(value);
						WorkUnit w2 = new WorkUnit(0);
						TaskInternals taskInternals = new TaskInternals(key,
								w1, w2, task);
						task.addSkill(key.getName(), taskInternals);
					}
					// it.remove(); // avoids a ConcurrentModificationException
				}
			}
		}
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

	public int getSkillSetMatrixCount() {
		return skillSetMatrix.size();
	}

	public int getSingleSkillSet() {
		return singleSkillSet.size();
	}

	private static void say(String s) {
		PjiitOutputter.say(s);
	}

	private static void sanity(String s) {
		PjiitOutputter.sanity(s);
	}

}
