package collaboration;

import github.DataSet;
import intelligence.TasksDiviner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import logger.PjiitOutputter;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import strategies.Strategy;
import test.TaskTestUniverse;
import utils.LaunchStatistics;

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

	public static synchronized Task chooseTask(Agent agent,
			Strategy.TaskChoice strategy) {
		return TasksDiviner.chooseTask(agent, strategy, tasks);
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
			assert tasks.remove(task.getName()) != null;
			sanity("Task id:" + task.getId() + " name:" + task.getName()
					+ " is depleted and leaving the environment");
		}
	}

	public static HashMap<Skill, ArrayList<Task>> getTasksPerSkills(
			Collection<Skill> c) {
		HashMap<Skill, ArrayList<Task>> result = new HashMap<Skill, ArrayList<Task>>();
		for (Skill skill : c) {
			for (Task task : tasks.values()) {
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

	public static HashMap<Skill, ArrayList<Task>> getTasksWithoutSkills(
			Collection<Skill> c) {
		HashMap<Skill, ArrayList<Task>> result = new HashMap<Skill, ArrayList<Task>>();
		for (Skill skill : c) {
			for (Task task : tasks.values()) {
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
	
	public static Set<Task> getTasksHavingSkills(
			Collection<Skill> c) {
		Set<Task> result = new HashSet<Task>();
		for (Skill skill : c) {
			for (Task task : tasks.values()) {
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
		if (tasks.size() < 1)
			return result;
		for (Task task : tasks.values()) {
			if (!task.isClosed())
				result = true;
		}
		return result;
	}

	private DataSet dataSet;

	private LaunchStatistics launchStatistics;

	private Integer allowedLoad;

	private static Map<String, Task> tasks = new HashMap<String, Task>();

	public static void clearTasks() {
		tasks.clear();
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

	public void addTask(String key, Task task) {
		tasks.put(key, task);
		say("Task added successfully to pool. Pool size: " + getCount());
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
			return tasks.size();
		else {
			int counter = 0;
			for (Task task : tasks.values()) {
				if (!task.isClosed())
					counter++;
			}
			return counter;
		}
	}

	public Task getTask(String key) {
		return tasks.get(key);
	}

	public Collection<Task> getTasks() {
		return tasks.values();
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
		for (Task task : tasks.values()) {
			if (!task.isClosed())
				result.add(task);
		}
		return result;
	}
	
	private void initializeTasks(Context<Task> context) {
		if(dataSet.isMockup()){
			initializeTasksNormally(context);
		} else if (dataSet.isTest()){
			TaskTestUniverse.init();
			initalizeValidationTasks(context);
		} else {
			assert false; // should never happen
		}
	}
	
	private void initializeTasksNormally(Context<Task> context) {
		Integer howMany = SimulationParameters.multipleAgentSets ? allowedLoad
				: SimulationParameters.taskCount;
		for (int i = 0; i < howMany; i++) {
			Task task = new Task();
			say("Creating Task " + task.getId());
			addTask(task.getName(), task);
			say("Initializing Task " + task.getId() + " " + task.getName());
			task.initialize(howMany);
			context.add(task);
		}
		launchStatistics.taskCount = getCount();
	}

	private void initalizeValidationTasks(Context<Task> context) {
		for (Task task : TaskTestUniverse.DATASET) {
			say("Adding validation task to pool..");
			addTask(task.getName(), task);
			context.add(task);
		}
		launchStatistics.taskCount = getCount();
	}
	
}
