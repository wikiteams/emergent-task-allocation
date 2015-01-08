package collaboration;

import github.DataSet;
import github.MyDatabaseConnector;
import intelligence.TasksDiviner;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import logger.PjiitOutputter;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import strategies.Strategy;
import test.TaskTestUniverse;
import utils.LaunchStatistics;

import com.google.common.collect.Lists;

/***
 * Programming Task - a producer in the simulation
 * 
 * @author Oskar Jarczyk
 * @version 2.0.6
 */
public class Tasks extends DefaultContext<Task> {

	/**
	 * This value is used to automatically generate agent identifiers.
	 * 
	 * If you want to see 1 million digits of Pi, then visit index2.html at the
	 * root of domain http://
	 * 3.141592653589793238462643383279502884197169399375105820974944592.com/
	 * 
	 * @field serialVersionUID
	 */
	public static final long serialVersionUID = 31415926535897L;

	private DataSet dataSet;
	private LaunchStatistics launchStatistics;
	private Integer allowedLoad;

	private static Map<String, Task> getMappedTasks() {
		Iterable<Task> it = CollaborationBuilder.tasks.getObjects(Task.class);
		Map<String, Task> result = new HashMap<String, Task>();
		Iterator<Task> iterator = it.iterator();
		while (iterator.hasNext()) {
			Task task = iterator.next();
			result.put(task.getName(), task);
		}
		return result;
	}

	private static Collection<Task> getUnmappedTasks() {
		Iterable<Task> it = CollaborationBuilder.tasks.getObjects(Task.class);
		return Lists.newArrayList(it);
	}

	public static synchronized Task chooseTask(Agent agent,
			Strategy.TaskChoice strategy) {
		return TasksDiviner.chooseTask(agent, strategy, getMappedTasks());
	}

	public static void considerEnding(Task task) {
		boolean notfinished = false;
		for (TaskInternals taskInternal : task.getTaskInternals().values()) {
			if (taskInternal.getWorkDone().d < taskInternal.getWorkRequired().d) {
				notfinished = true;
				break;
			}
		}
		if (!notfinished) {
			CollaborationBuilder.tasks.remove(task);
			sanity("[Task] id:" + task.getId() + " name:" + task.getName()
					+ " is depleted and leaving the environment");
		}
	}

	public HashMap<Skill, ArrayList<Task>> getTasksPerSkills(Collection<Skill> c) {
		HashMap<Skill, ArrayList<Task>> result = new HashMap<Skill, ArrayList<Task>>();
		for (Skill skill : c) {
			for (Task task : getTasks()) {
				Collection<Skill> ts = task.getSkills();
				if (ts.contains(skill)) {
					ArrayList<Task> value = result.get(skill);
					if (value == null) {
						result.put(skill, new ArrayList<Task>());
						value = result.get(skill);
					}
					value.add(task);
					result.put(skill, value);
				}
			}
		}
		return result;
	}

	public HashMap<Skill, ArrayList<Task>> getTasksWithoutSkills(
			Collection<Skill> c) {
		HashMap<Skill, ArrayList<Task>> result = new HashMap<Skill, ArrayList<Task>>();
		for (Skill skill : c) {
			for (Task task : getTasks()) {
				Collection<Skill> ts = task.getSkills();
				if (!ts.contains(skill)) {
					ArrayList<Task> value = result.get(skill);
					if (value == null) {
						result.put(skill, new ArrayList<Task>());
						value = result.get(skill);
					}
					value.add(task);
					result.put(skill, value);
				}
			}
		}
		return result;
	}

	public static Set<Task> getTasksHavingSkills(Collection<Skill> c) {
		Set<Task> result = new HashSet<Task>();
		for (Skill skill : c) {
			for (Task task : getUnmappedTasks()) {
				Collection<Skill> ts = task.getSkills();
				if (ts.contains(skill)) {
					result.add(task);
				}
			}
		}
		return result;
	}

	private static void sanity(String s) {
		PjiitOutputter.sanity(s);
	}

	public static boolean stillNonEmptyTasks() {
		boolean result = false;
		if (CollaborationBuilder.tasks.size() < 1)
			return result;
		for (Task task : getUnmappedTasks()) {
			if (!task.isClosed())
				result = true;
		}
		return result;
	}

	public static void clearTasks() {
		if (CollaborationBuilder.tasks != null)
			CollaborationBuilder.tasks.clear();
	}

	private static void say(String s) {
		PjiitOutputter.say(s);
	}

	public Tasks(DataSet dataSet, LaunchStatistics launchStatistics,
			Integer allowedLoad) {
		super("Tasks");
		this.dataSet = dataSet;
		this.launchStatistics = launchStatistics;
		this.allowedLoad = allowedLoad;
		initializeTasks(this);
	}

	public Tasks(Integer allowedLoad) {
		this(DataSet.getInstance(), LaunchStatistics.getInstance(), allowedLoad);
	}

	/**
	 * Count all Tasks in the pool
	 * 
	 * @return int - task pool size, in other words, count of the tasks in the
	 *         simulation universe
	 */
	public int getCount() {
		return getCount(false);
	}

	public int getCount(boolean notFinished) {
		if (!notFinished)
			return this.size();
		else {
			int counter = 0;
			for (Task task : getTasks()) {
				if (!task.isClosed())
					counter++;
			}
			return counter;
		}
	}

	public Task getTask(String key) {
		return getMappedTasks().get(key);
	}

	public Collection<Task> getTasks() {
		return getUnmappedTasks();
	}

	/**
	 * Count unfinished Tasks in the pool
	 * 
	 * @return int - task pool size, in other words, count of the tasks in the
	 *         simulation universe
	 */
	public int getUnfinishedCount() {
		return getCount(true);
	}

	public Collection<Task> getUnfinishedTasks() {
		List<Task> result = new ArrayList<Task>();
		for (Task task : getTasks()) {
			if (!task.isClosed())
				result.add(task);
		}
		return result;
	}

	private void initializeTasks(Context<Task> context) {
		if (dataSet.isDb()) {
			initFirstTasks(context);
		} else if (dataSet.isMockup()) {
			initializeTasksNormally(context);
		} else if (dataSet.isTest()) {
			TaskTestUniverse.init();
			initalizeValidationTasks(context);
		} else {
			assert false; // should never happen
		}
	}

	private void initFirstTasks(Context<Task> context) {
		try {
			List<Task> firstTasks = MyDatabaseConnector.get(this.allowedLoad);
			for (Task task : firstTasks) {
				// addTask(task);
				context.add(task);
			}
		} catch (SQLException e) {
			say("Error during init of first " + this.allowedLoad + " [Tasks]");
			e.printStackTrace();
		}
	}

	private void initializeTasksNormally(Context<Task> context) {
		Integer howMany = SimulationParameters.multipleAgentSets ? allowedLoad
				: SimulationParameters.taskCount;
		for (int i = 0; i < howMany; i++) {
			Task task = new Task();
			say("Creating Task " + task.getId());
			// addTask(task);
			say("Initializing Task " + task.getId() + " " + task.getName());
			task.initialize(howMany);
			context.add(task);
		}
		launchStatistics.taskCount = getCount();
	}

	private void initalizeValidationTasks(Context<Task> context) {
		for (Task task : TaskTestUniverse.DATASET) {
			say("Adding validation task to pool..");
			// addTask(task);
			context.add(task);
		}
		launchStatistics.taskCount = getCount();
	}

}
