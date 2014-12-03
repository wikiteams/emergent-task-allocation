package utils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.apache.commons.lang3.SystemUtils;
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

import constants.LoadSet;
import repast.simphony.random.RandomHelper;

/**
 * Defines the sets of number of agents / number of tasks to be used in a
 * Simulation, simulation randomly chooses one of the item which makes for
 * getting all possible sets when many runs
 * 
 * @author Oskar Jarczyk
 * @since ?
 */
public class DescribeUniverseBulkLoad {

	private static LinkedList<String[]> combinations;

	private static String fileName = SystemUtils.IS_OS_LINUX ? "data/agentsTaskCombination.ini"
			: "data\\agentsTaskCombination.ini";

	public static LoadSet init() throws InvalidFileFormatException, IOException {
		combinations = new LinkedList<String[]>();

		Ini ini = new Ini(new File(fileName));
		int count = Integer.parseInt(ini.get("General", "count"));

		for (int i = 1; i <= count; i++) {
			String a1 = ini.get("Agents-" + i, "agentCount");
			String t1 = ini.get("Agents-" + i, "taskCount");
			combinations.add(new String[] { a1, t1 });
		}

		return getSingleRandomCombination();
	}

	public static LinkedList<String[]> getCombinations() {
		return combinations;
	}

	private static LoadSet getSingleRandomCombination() {
		LoadSet result = new LoadSet();
		String[] chosen = combinations.get(RandomHelper.nextIntFromTo(0,
				combinations.size() - 1));
		result.AGENT_COUNT = Integer.parseInt(chosen[0]);
		result.TASK_COUNT = Integer.parseInt(chosen[1]);
		return result;
	}

}
