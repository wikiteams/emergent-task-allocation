package collaboration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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
 * @version 3.0
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
	

	private static SkillFactory instance = null;

	public static SkillFactory getInstance() {
		if (instance == null) {
			instance = new SkillFactory();
		}
		return instance;
	}

	public Set<Skill> buildSkillsLibrary() throws IOException, FileNotFoundException {
		System.out.println("Searching for file in path: " + new File(".").getAbsolutePath());
		CSVReader reader = new CSVReader(new FileReader(filename));
		
		Set<Skill> listSkills = new HashSet<Skill>();
		
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			Skill skill = new Skill(nextLine[0], nextLine[1], listSkills.size() + 1);
			listSkills.add(skill);
			// System.out.println("Skill " + skill.getName() + " added to factory");
		}
		reader.close();
		
		return listSkills;
	}

}
