package collaboration;

import github.TaskSkillsPool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import networking.CollaborationNetwork;
import logger.PjiitOutputter;
import repast.simphony.context.Context;
import repast.simphony.random.RandomHelper;
import repast.simphony.util.ContextUtils;
import strategies.CentralAssignmentTask;
import strategies.GreedyAssignmentTask;
import strategies.ProportionalTimeDivision;
import strategies.Strategy;
import tasks.CentralAssignmentOrders;
import argonauts.GranulatedChoice;
import argonauts.PersistJobDone;
import constants.Constraints;

/**
 * Task is a collection of a three-element set of skill, number of work units,
 * and work done. Literally, a representation of a simulation Task object.
 * 
 * @author Oskar Jarczyk
 * @since 1.0
 * @version 2.0.6
 */
public class Task {

	/**
	 * This value is used to automatically generate agent identifiers.
	 * 
	 * 75 is the fourth ordered Bell number, and counts the number of weak
	 * orderings on a set of four items.
	 * 
	 * As the sum of the first five pentagonal numbers, 75 is a pentagonal
	 * pyramidal number. It is also an enneagonal number.
	 * 
	 * It is also a Keith number, because it recurs in a Fibonacci-like sequence
	 * started from its base 10 digits: 7, 5, 12, 17, 29, 46, 75... But there is
	 * no integer that added up to its own digits adds up to 75, hence 75 is a
	 * self number.
	 * 
	 * Excluding the infinite sets, there are 75 uniform polyhedra (or 76 if
	 * edges are allowed to coincide).
	 * 
	 * @field serialVersionUID
	 */
	public static final long serialVersionUID = 75L;

	private static int idIncrementalCounter = 0;
	private static GameController gameController;
	private Long numberOfVisits;

	private String name;
	private final int id = ++idIncrementalCounter;

	private Map<String, TaskInternals> skills = new HashMap<String, TaskInternals>();
	private Map<Skill, Double> persistAdvance = new HashMap<Skill, Double>();

	public Task() {
		this.name = "Task_" + this.id;
		this.numberOfVisits = 0L;
		say("Task object " + this + " created");
	}

	public GameController initGameController() {
		Context<Task> context = ContextUtils.getContext(this);
		Context<Object> parentContext = ContextUtils.getParentContext(context);
		gameController = (GameController) parentContext.getObjects(
				GameController.class).get(0);
		return gameController;
	}

	public void addSkill(String key, TaskInternals taskInternals) {
		skills.put(key, taskInternals);
	}

	public void removeSkill(String key) {
		skills.remove(key);
	}

	public TaskInternals getTaskInternals(String skillName) {
		return skills.get(skillName);
	}

	public TaskInternals getTaskInternals(Skill skill) {
		return skills.get(skill.getName());
	}

	public TaskInternals getRandomTaskInternals() {
		return (TaskInternals) skills.values().toArray()[RandomHelper
				.nextIntFromTo(0, skills.size() - 1)];
	}

	public synchronized void initialize(int countAll) {
		TaskSkillsPool.fillWithSkills(this, countAll);
		say("Task object initialized with id: " + this.id);
	}

	public Map<String, TaskInternals> getTaskInternals() {
		return skills;
	}

	public void setTaskInternals(Map<String, TaskInternals> skills) {
		this.skills = skills;
	}

	public int countTaskInternals() {
		return skills.size();
	}

	public int countUnfinishedTaskInternals() {
		int result = 0;
		for (TaskInternals taskInternals : skills.values()) {
			if (taskInternals.getWorkDone().d < taskInternals.getWorkRequired().d) {
				result++;
				break;
			}
		}
		return result;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getSimplifiedAdvance(Skill skill) {
		double persistTaskAdvance = 0;
		TaskInternals ti = this.getTaskInternals(skill.getName());
		if (ti == null) {
			persistAdvance.put(skill, 1.);
			// persistTaskAdvance += 1 / this.getTaskInternals().size();
		} else {
			double progress = ti.getProgress();
			persistAdvance.put(skill, progress);
			// persistTaskAdvance += (progress-before) /
			// this.getTaskInternals().size();
		}
		for (Double d : persistAdvance.values()) {
			persistTaskAdvance += d;
		}
		return (persistTaskAdvance / ((double) persistAdvance.values().size()));
	}

	/**
	 * Gets the general completion of the Task. Calculates work done inside the
	 * Skills and divides by the number of skills.
	 * 
	 * @return Always the value between [0;1]
	 */
	public double getGeneralAdvance() {
		double result = 0;
		double count = 0;
		for (TaskInternals skill : skills.values()) {
			double progress = skill.getProgress();
			say("skill " + skill.getSkill().getName() + " progress " + progress);
			result += progress > 1. ? 1. : progress;
			say("result " + result);
			persistAdvance.put(skill.getSkill(), progress);
			count++;
		}
		say("skills count " + count);
		if (count == 0) {
			// all TaskInternals are gone, thus the Task is finished 100% !
			return 1;
		}
		assert count > 0.; // avoid dividing by 0;
		result = (result / count);
		assert result >= 0.;
		assert result <= 1.;
		// persistTaskAdvance = result;
		return result;
	}

	/**
	 * For an Agent, get skills common with argument Collection<TaskInternals>
	 * skillsValues return intersection of agent skills and argument skillsValue
	 * 
	 * @param agent
	 * @param skillsValues
	 * @return return intersection of agent skills and argument skillsValue
	 */
	private Collection<TaskInternals> computeIntersection(Agent agent,
			Collection<TaskInternals> skillsValues) {
		Collection<TaskInternals> returnCollection = new ArrayList<TaskInternals>();
		for (TaskInternals singleTaskInternal : skillsValues) {
			if (agent
					.getAgentInternals(singleTaskInternal.getSkill().getName()) != null) {
				returnCollection.add(singleTaskInternal);
			}
		}
		return returnCollection;
	}

	public void workOnTaskCentrallyControlled(Agent agent) {
		List<Skill> skillsImprovedList = new ArrayList<Skill>();
		CentralAssignmentOrders cao = agent.getCentralAssignmentOrders();
		CentralAssignmentTask centralAssignmentTask = new CentralAssignmentTask();
		TaskInternals taskInternal = this.getTaskInternals(cao
				.getChosenSkillName());

		assert taskInternal != null;

		sanity("Choosing Si:{" + taskInternal.getSkill().getName()
				+ "} inside Ti:{" + this.toString() + "}");

		Experience experience = agent.getAgentInternalsOrCreate(
				cao.getChosenSkillName()).getExperience();
		double delta = experience.getDelta();
		centralAssignmentTask.increment(this, taskInternal, 1, delta);
		experience.increment(1);

		if (SimulationParameters.deployedTasksLeave)
			Tasks.considerEnding(this);
		skillsImprovedList.add(taskInternal.getSkill());

		PersistJobDone.addContribution(agent, this, skillsImprovedList);
	}

	public Boolean workOnTaskFromContinuum(Agent agent,
			GranulatedChoice granulated, Strategy.SkillChoice strategy) {
		return workOnTask(agent, strategy);
	}

	public Boolean workOnTask(Agent agent, Strategy.SkillChoice strategy) {
		CollaborationNetwork.addEdge(agent, this);
		
		Collection<TaskInternals> intersection;
		List<Skill> skillsImprovedList = new ArrayList<Skill>();

		intersection = computeIntersection(agent, skills.values());

		GreedyAssignmentTask greedyAssignmentTask = new GreedyAssignmentTask();
		TaskInternals singleTaskInternal = null;
		double highest = -1.;

		assert intersection != null;
		// if ((SimulationParameters.granularity) && (intersection.size() < 1))
		// return false; // happens when agent tries to work on
		// task with no intersection of skills

		// assert intersection.size() > 0; // assertion for the rest of cases
		if (intersection.size() < 1) {
			intersection = skills.values(); // experience - genesis action
											// needed!
			if (intersection.size() < 1)
				return false;
		}

		switch (strategy) {
		case PROPORTIONAL_TIME_DIVISION:
			say(Constraints.INSIDE_PROPORTIONAL_TIME_DIVISION);
			ProportionalTimeDivision proportionalTimeDivision = new ProportionalTimeDivision();
			for (TaskInternals singleTaskInternalFromIntersect : new CopyOnWriteArrayList<TaskInternals>(
					intersection)) {
				sanity("Choosing Si:{"
						+ singleTaskInternalFromIntersect.getSkill().getName()
						+ "} inside Ti:{"
						+ singleTaskInternalFromIntersect.toString() + "}");
				double n = intersection.size();
				double alpha = 1d / n;
				Experience experience = agent.getAgentInternalsOrCreate(
						singleTaskInternalFromIntersect.getSkill().getName())
						.getExperience();
				double delta = experience.getDelta();
				proportionalTimeDivision.increment(this,
						singleTaskInternalFromIntersect, 1, alpha, delta);
				experience.increment(alpha);
				skillsImprovedList.add(singleTaskInternalFromIntersect
						.getSkill());
			}
			break;
		case GREEDY_ASSIGNMENT:
			say(Constraints.INSIDE_GREEDY_ASSIGNMENT_BY_TASK);
			CopyOnWriteArrayList<TaskInternals> copyIntersection = new CopyOnWriteArrayList<TaskInternals>(
					intersection);
			/**
			 * Tutaj sprawdzamy nad ktorymi taskami juz pracowano w tym tasku, i
			 * bierzemy wlasnie te najbardziej rozpoczete. Jezeli zaden nie jest
			 * rozpoczety, to bierzemy losowy
			 */
			for (TaskInternals searchTaskInternal : copyIntersection) {
				if (searchTaskInternal.getWorkDone().d > highest) {
					highest = searchTaskInternal.getWorkDone().d;
					singleTaskInternal = searchTaskInternal;
				}
			}
			/**
			 * zmienna highest zawsze jest w przedziale od [0..*]
			 */
			assert highest > -1.;
			/**
			 * musimy miec jakis pojedynczy task internal (skill) nad ktorym
			 * bedziemy pracowac..
			 */
			assert singleTaskInternal != null;

			{
				sanity("Choosing Si:{"
						+ singleTaskInternal.getSkill().getName()
						+ "} inside Ti:{" + singleTaskInternal.toString() + "}");
				// int n = skills.size();
				// double alpha = 1 / n;
				Experience experience = agent.getAgentInternalsOrCreate(
						singleTaskInternal.getSkill().getName())
						.getExperience();
				double delta = experience.getDelta();
				greedyAssignmentTask.increment(this, singleTaskInternal, 1,
						delta);
				experience.increment(1);
				skillsImprovedList.add(singleTaskInternal.getSkill());
			}
			break;
		case CHOICE_OF_AGENT:
			say(Constraints.INSIDE_CHOICE_OF_AGENT);

			/**
			 * Pracuj wylacznie nad tym skillem, w ktorym agent ma najwiecej
			 * doswiadczenia
			 */
			for (TaskInternals searchTaskInternal : new CopyOnWriteArrayList<TaskInternals>(
					intersection)) {
				if (agent.describeExperience(searchTaskInternal.getSkill(),
						true, true) > highest) {
					highest = agent.describeExperience(
							searchTaskInternal.getSkill(), true, true);
					singleTaskInternal = searchTaskInternal;
				}
			}
			/**
			 * zmienna highest zawsze jest w przedziale od [0..*]
			 */
			assert highest != -1.;
			/**
			 * musimy miec jakis pojedynczy task internal (skill) nad ktorym
			 * bedziemy pracowac..
			 */
			assert singleTaskInternal != null;

			{
				sanity("Choosing Si:{"
						+ singleTaskInternal.getSkill().getName()
						+ "} inside Ti:{" + singleTaskInternal.toString() + "}");
				Experience experience = agent.getAgentInternalsOrCreate(
						singleTaskInternal.getSkill().getName())
						.getExperience();
				double delta = experience.getDelta();
				greedyAssignmentTask.increment(this, singleTaskInternal, 1,
						delta);
				experience.increment(1);
				skillsImprovedList.add(singleTaskInternal.getSkill());
			}
			break;
		case RANDOM:
			say(Constraints.INSIDE_RANDOM);
			List<TaskInternals> intersectionToShuffle = new ArrayList<TaskInternals>();
			for (TaskInternals taskInternalsR : intersection) {
				intersectionToShuffle.add(taskInternalsR);
			}
			Collections.shuffle(intersectionToShuffle);
			TaskInternals randomTaskInternal = (intersectionToShuffle)
					.get(RandomHelper.nextIntFromTo(0,
							intersectionToShuffle.size() - 1));
			{
				sanity("Choosing Si:{"
						+ randomTaskInternal.getSkill().getName()
						+ "} inside Ti:{" + randomTaskInternal.toString() + "}");
				Experience experience = agent.getAgentInternalsOrCreate(
						randomTaskInternal.getSkill().getName())
						.getExperience();
				double delta = experience.getDelta();
				greedyAssignmentTask.increment(this, randomTaskInternal, 1,
						delta);
				experience.increment(1);
				skillsImprovedList.add(randomTaskInternal.getSkill());
			}
			break;
		default:
			assert false; // there is no default method, so please never happen
			break;
		}

		if (SimulationParameters.deployedTasksLeave)
			Tasks.considerEnding(this);

		if (skillsImprovedList.size() > 0) {
			PersistJobDone.addContribution(agent, this, skillsImprovedList);
			increaseNumberOfVisits();
			return true;
		} else {
			return false;
		}
	}

	public boolean isClosed() {
		boolean result = true;
		for (TaskInternals taskInternals : skills.values()) {
			if (taskInternals.getWorkDone().d < taskInternals.getWorkRequired().d) {
				result = false;
				break;
			}
		}
		return result;
	}

	/**
	 * Returns a collection of skills inside internals of current task
	 * 
	 * @return Collection of skills inside all TaskInternals
	 */
	public Collection<Skill> getSkills() {
		ArrayList<Skill> skillCollection = new ArrayList<Skill>();
		Collection<TaskInternals> internals = this.getTaskInternals().values();
		for (TaskInternals ti : internals) {
			skillCollection.add(ti.getSkill());
		}
		return skillCollection;
	}

	@Override
	public String toString() {
		return "Task " + id + " " + name;
	}

	@Override
	public int hashCode() {
		return name.hashCode() * id;
	}

	@Override
	public boolean equals(Object obj) {
		if ((this.name.toLowerCase().equals(((Task) obj).name.toLowerCase()))
				&& (this.id == ((Task) obj).id))
			return true;
		else
			return false;
	}

	private void say(String s) {
		PjiitOutputter.say(s);
	}

	private void sanity(String s) {
		PjiitOutputter.sanity(s);
	}

	public GameController getGameController() {
		return gameController == null ? initGameController() : gameController;
	}

	public int getIteration() {
		return getGameController().getCurrentIteration() + 1;
	}

	public int getGeneration() {
		return getGameController().getCurrentGeneration() + 1;
	}

	public Long getNumberOfVisits() {
		return numberOfVisits;
	}

	public void setNumberOfVisits(Long numberOfVisits) {
		this.numberOfVisits = numberOfVisits;
	}

	public void increaseNumberOfVisits() {
		if (numberOfVisits + 1 == Long.MAX_VALUE)
			throw new UnsupportedOperationException(
					Constraints.MAX_VALUE_REACHED);
		this.numberOfVisits++;
	}

}
