package utils;

import logger.PjiitOutputter;
import collaboration.SkillFactory;
import constants.Constraints;

/***
 * A holder class used for outputting final results
 * 
 * @author Oskar Jarczyk
 * @since 1.0
 */
public class LaunchStatistics {

	private static LaunchStatistics instance = null;

	public int agentCount = 0;
	public int taskCount = 0;
	public boolean expDecay = false;
	public boolean fullyLearnedAgentsLeave = false;

	public boolean granularity = false;
	public String granularityType = Constraints.EMPTY_STRING;

	public boolean experienceCutPoint = false;

	private LaunchStatistics() {
		say("[LaunchStatistics] object created");
	}
	
	public static LaunchStatistics getInstance() {
		if (instance == null) {
			instance = new LaunchStatistics();
		}
		return instance;
	}
	
	private static void say(String s) {
		PjiitOutputter.say(s);
	}

}
