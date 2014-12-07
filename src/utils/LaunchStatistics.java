package utils;

import constants.Constraints;

/***
 * Used for outputting final results!
 * 
 * @author Oskar Jarczyk
 * @since 1.0
 */
public class LaunchStatistics {

	public static LaunchStatistics singleton = null;

	public int agentCount = 0;
	public int taskCount = 0;
	public boolean expDecay = false;
	public boolean fullyLearnedAgentsLeave = false;

	public boolean granularity = false;
	public String granularityType = Constraints.EMPTY_STRING;

	public boolean experienceCutPoint = false;

	public LaunchStatistics() {
		if (singleton != null) {
			throw new UnsupportedOperationException(
					"There is already an instance of LaunchStatistics.");
		} else {
			singleton = this;
		}
	}

}
