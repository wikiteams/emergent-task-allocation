package collaboration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.lang3.SystemUtils;

import au.com.bytecode.opencsv.CSVReader;

/***
 * Here are all skills known to GitHub read and hold in ArrayList for more info,
 * please visit:
 * https://github.com/github/linguist/blob/master/lib/linguist/languages.yml 
 * In other words, this class loads all know programming languages,
 * and it includes deleted language definitions as well (compability backwards)
 * 
 * @author Oskar Jarczyk
 * @since 1.0
 * @version 2.0.11
 */
public class SkillFactory {

	/**
	 * File format:
	 * 
	 * language1,type\r\n 
	 * language2,type\r\n 
	 * ... 
	 * langauage{i},type
	 * 
	 * around 305 entries
	 */
	private static String filename = SystemUtils.IS_OS_LINUX ? "data/all-languages.csv"
			: "data\\all-languages.csv";
	public static ArrayList<Skill> skills = new ArrayList<Skill>();

	private static SkillFactory instance = null;

	private SkillFactory() {
		System.out.println("[SkillFactory] object created");
	}

	public static SkillFactory getInstance() {
		if (instance == null) {
			instance = new SkillFactory();
		}
		return instance;
	}

	public Skill getSkill(String name) {
		for (Skill skill : skills) {
			if (skill.getName().toLowerCase().equals(name.toLowerCase())) {
				return skill;
			}
		}
		return null;
	}
	
	public int countAllSkills(){
		return skills.size();
	}

	public void buildSkillsLibrary(boolean verbose) throws IOException, FileNotFoundException {
		System.out.println("Searching for [file] in path: " + new File(".").getAbsolutePath());
		CSVReader reader = new CSVReader(new FileReader(filename));
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			Skill skill = new Skill(nextLine[0], nextLine[1], skills.size() + 1);
			skills.add(skill);
			if (verbose)
				System.out.println("[Skill] " + skill.getId() + ": " + skill.getName() + " added to [Skill Factory]");
		}
		reader.close();
	}

}
