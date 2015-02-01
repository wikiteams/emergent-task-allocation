package collaboration;

import github.AgentModeling;
import github.DataSet;
import intelligence.ImpactFactor;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import logger.PjiitOutputter;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.moment.Mean;

import collaboration.Utility.UtilityType;
import repast.simphony.annotate.AgentAnnot;
import repast.simphony.context.Context;
import repast.simphony.context.space.graph.NodeCreator;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.ui.probe.ProbeID;
import repast.simphony.util.ContextUtils;
import strategies.Strategy;
import strategies.Strategy.SkillChoice;
import strategies.Strategy.TaskChoice;
import tasks.CentralAssignmentOrders;
import utils.ObjectsHelper;
import argonauts.GranularityType;
import argonauts.GranulatedChoice;
import argonauts.PersistJobDone;
import argonauts.PersistRewiring;

/***
 * Simulation agent - a GitHub programmer as restored from data on users
 * activity with a help of aggregators like BrainJar etc.
 * 
 * @author Oskar Jarczyk
 * @since 1.0
 * @version 2.0.9 'White fox' edition
 */
@AgentAnnot(displayName = "Agent")
public class Agent implements NodeCreator<Agent> {

	/**
	 * This value is used to automatically generate agent identifiers.
	 * 
	 * 42 - Answer to life the universe and everything, even GitHub
	 * 
	 * @field serialVersionUID
	 */
	public static final long serialVersionUID = 42L;
	private static GameController gameController;
	public static int totalAgents = 0;

	private AgentSkills agentSkills;
	private Strategy strategy;

	private final int id = ++totalAgents;
	private String firstName;
	private String lastName;
	private String nick;

	private CentralAssignmentOrders centralAssignmentOrders;

	public Agent() {
		this("Undefined name", "Undefined", "Agent_");
	}

	public Agent(String firstName, String lastName, String nick) {
		this(firstName, lastName, nick, DataSet.getInstance().isMockup());
	}

	public Agent(String firstName, String lastName, String nick,
			Boolean fillWithOldSkills) {
		this.agentSkills = new AgentSkills();
		say("[Agent] constructor called");
		if (fillWithOldSkills) {
			AgentSkillsPool.fillWithSkills(this);
		} else {
			// calls for filling data got from OSRC
			// TODO: add BrainJar or OpenHUB (Ohloh) mechanism
			AgentModeling.fillWithSkills(this);
		}
		this.firstName = firstName;
		this.lastName = lastName;
		this.nick = nick + this.id;
		this.agentSkills.backup();
	}

	@SuppressWarnings("unchecked")
	public GameController initGameController() {
		Context<Agent> context = ContextUtils.getContext(this);
		Context<Object> parentContext = ContextUtils.getParentContext(context);
		gameController = (GameController) parentContext.getObjects(
				GameController.class).get(0);
		return gameController;
	}

	public Double getUtility() {
		if (SimulationParameters.isAgentOrientedUtility) {
			if (SimulationParameters.utilityType.equals(UtilityType.LearningSkills))
				return getLearningUtility();
			else if (SimulationParameters.utilityType.equals(UtilityType.LeftLearningSkills))
				return getLeftAgentUtility();
			else {
				assert (SimulationParameters.utilityType.equals(UtilityType.RightLearningSkills));
				return getRightAgentUtility();
			}
		} else {
			if (SimulationParameters.utilityType.equals(UtilityType.ImpactFactor))
				return getImpactUtility();
			else {
				assert (SimulationParameters.utilityType.equals(UtilityType.ImpactFactorMax));
				return getImpactHUtility();
			}
		}
	}

	public String getDecimalFormatLeftUtility() {
		return new DecimalFormat("#.######").format(getLeftAgentUtility());
	}

	public String getDecimalFormatRightUtility() {
		return new DecimalFormat("#.######").format(getRightAgentUtility());
	}

	private Double getLeftAgentUtility() {
		if (SimulationParameters.isAgentOrientedUtility) {
			return Utility.getLeftLearningUtility(getAgentInternals());
		} else
			return ObjectsHelper.notApplicable;
	}

	private Double getRightAgentUtility() {
		if (SimulationParameters.isAgentOrientedUtility) {
			return Utility.getRightLearningUtility(getAgentInternals());
		} else
			return ObjectsHelper.notApplicable;
	}

	/***
	 * "Impact Factor"
	 * 
	 * @since 2.0.7
	 * @return Double - Avg Impact Factor
	 */
	public Double getImpactUtility() {
		Mean mean = new Mean();
		// below I return average from 3 last impact factors
		// registered for exactly this agent
		return mean.evaluate(ArrayUtils.toPrimitive(ImpactFactor.get(this)));
	}

	/***
	 * "H" factor
	 * 
	 * @since 2.0.7
	 * @return Double - Avg Highest Impact Factor
	 */
	public Double getImpactHUtility() {
		Mean mean = new Mean();
		// below I return average from 3 highest impact factors
		// registered for exactly this agent
		return mean.evaluate(ArrayUtils.toPrimitive(ImpactFactor
				.getHighest(this)));
	}

	public Double getLearningUtility() {
		return Utility.getLearningUtility(getAgentInternals());
	}

	public String getDecimalFormatUtility() {
		return new DecimalFormat("#.######").format(getUtility());
	}

	public void mutate() {
		// 1% chances for deleting (abandoning) skill
		if (RandomHelper.nextDoubleFromTo(0, 1) <= SimulationParameters.mutateChances) {
			Object[] allSkills = agentSkills.getSkills().keySet().toArray();
			agentSkills.removeSkill((String) allSkills[RandomHelper
					.nextIntFromTo(0, allSkills.length - 1)]);
		}
		for (AgentInternals a : agentSkills.getSkills().values()) {
			// 2% chances for self-improvement in a skill, Bernoulli trial
			if (RandomHelper.nextDoubleFromTo(0, 1) <= 0.02) {
				a.getExperience().incrementAbsolutly(0.05);
			}
		}
	}

	public void addSkill(String key, AgentInternals agentInternals) {
		getCurrentSkills().put(key, agentInternals);
	}

	/**
	 * Actually used very rarely, as far as I know - only when experience after
	 * decayExp operation returns 0 Assertion that agent possess this skill
	 * before removal
	 * 
	 * @param key
	 *            - name of the Skill to remove
	 */
	public void removeSkill(String key, boolean skipAssertion) {
		assert skipAssertion ? true : getCurrentSkills().containsKey(key);
		getCurrentSkills().remove(key);
	}

	public void removeSkill(Skill key, boolean skipAssertion) {
		removeSkill(key.getName(), skipAssertion);
	}

	public Collection<AgentInternals> getAgentInternals() {
		return getCurrentSkills().values();
	}

	public AgentInternals getAgentInternals(String key) {
		return getCurrentSkills().get(key);
	}

	public AgentInternals getAgentInternalsOrCreate(String key) {
		AgentInternals result = null;
		if (getCurrentSkills().get(key) == null) {
			result = (new AgentInternals(SkillFactory.getInstance().getSkill(
					key), new Experience(true)));
			getCurrentSkills().put(key, result);
			result = getCurrentSkills().get(key);
		} else {
			result = getCurrentSkills().get(key);
		}
		return result;
	}

	public Collection<Skill> getSkills() {
		ArrayList<Skill> skillCollection = new ArrayList<Skill>();
		Collection<AgentInternals> internals = this.getAgentInternals();
		for (AgentInternals ai : internals) {
			skillCollection.add(ai.getSkill());
		}
		return skillCollection;
	}

	public void resetMe() {
		getAgentSkills().reset();
	}

	public int getId() {
		return this.id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@ScheduledMethod(start = 1, interval = 1, priority = 100)
	public void step() {
		say("Step(" + getTick() + ") of Agent " + this.id
				+ " scheduled method launched.");

		if (SimulationParameters.granularity) {
			GranulatedChoice granulated = PersistRewiring
					.getGranulatedChoice(this);

			if (granulated != null) {
				// randomize decision
				double leavingCurrentChance = RandomHelper.nextDoubleFromTo(0,
						100);
				if (leavingCurrentChance <= (double) (SimulationParameters.granularityObstinacy)) {
					say("Step(" + getTick() + ") of Agent " + this.id
							+ " continuuing granularity");
					// continue work on the same skill
					// but check if the is any work left in this particular task
					Boolean workDone = granulated.getTaskChosen()
							.workOnTaskFromContinuum(this, granulated,
									this.strategy.skillChoice);
					if (!workDone) {
						// chose new task for granulated choice !
						Task taskToWork = Tasks.chooseTask(this,
								this.strategy.getTaskChoice());
						executeJob(taskToWork);
						if (taskToWork != null) {
							switch (GranularityType
									.desc(SimulationParameters.granularityType)) {
							case TASKANDSKILL:
								// PersistRewiring.setOccupation(this,
								// taskToWork, PersistJobDone.);
								break;
							case TASKONLY:
								PersistRewiring.setOccupation(this, taskToWork);
								break;
							default:
								break;
							}
						}
					}
					// EnvironmentEquilibrium.setActivity(true);
				} else {
					say("Step(" + getTick() + ") of Agent " + this.id
							+ " choosing new task for granulated choice");
					// chose new task for granulated choice !
					Task taskToWork = Tasks.chooseTask(this,
							this.strategy.getTaskChoice());
					executeJob(taskToWork);
					if (taskToWork != null) {
						switch (GranularityType
								.desc(SimulationParameters.granularityType)) {
						case TASKANDSKILL:
							// PersistRewiring.setOccupation(this, taskToWork,
							// PersistJobDone.);
							break;
						case TASKONLY:
							PersistRewiring.setOccupation(this, taskToWork);
							break;
						default:
							break;
						}
					}
				}
			} else {
				say("Step("
						+ getTick()
						+ ") of Agent "
						+ this.id
						+ " first run, chose new task and assign granulated choice");
				// first run
				// chose new task and assign granulated choice !
				Task taskToWork = Tasks.chooseTask(this,
						this.strategy.getTaskChoice());
				executeJob(taskToWork);
				if (taskToWork != null) {
					switch (GranularityType
							.desc(SimulationParameters.granularityType)) {
					case TASKANDSKILL:
						// PersistRewiring.setOccupation(this, taskToWork,
						// PersistJobDone.);
						break;
					case TASKONLY:
						PersistRewiring.setOccupation(this, taskToWork);
						break;
					default:
						break;
					}
				}
			}
			/*****************************
			 * Granularity ends here
			 *****************************/
		} else { // block without granularity
			// [Agent] Aj uses Aj_S ([Strategy] for choosing tasks)
			// and chooses a Task Ti to work on
			Task taskToWork = Tasks.chooseTask(this,
					this.strategy.getTaskChoice());
			executeJob(taskToWork);
		}
	}

	private void executeJob(Task taskToWork) {
		// This agent will work on task Task taskToWork
		if ((taskToWork != null) && (taskToWork.getTaskInternals().size() > 0)) {

			assert taskToWork.getTaskInternals().size() > 0;
			say("Agent " + this.id + " will work on task " + taskToWork.getId());
			if ((this.getCentralAssignmentOrders() != null)
					&& (this.getCentralAssignmentOrders()
							.getChosenTask()
							.getTaskInternals(
									this.getCentralAssignmentOrders()
											.getChosenSkillName()) != null)) {
				taskToWork.workOnTaskCentrallyControlled(this);
				EnvironmentEquilibrium.setActivity(true);
			} else {
				taskToWork.workOnTask(this, this.strategy.skillChoice);
				EnvironmentEquilibrium.setActivity(true);
			}
		} else {

			if (SimulationParameters.allwaysChooseTask
					&& Tasks.stillNonEmptyTasks()) {
				Task randomTaskToWork = Tasks.chooseTask(this,
						Strategy.TaskChoice.RANDOM);
				assert randomTaskToWork.getTaskInternals().size() > 0;
				say("Agent " + this.id + " will work on task "
						+ randomTaskToWork.getId());
				if ((this.getCentralAssignmentOrders() != null)
						&& (this.getCentralAssignmentOrders()
								.getChosenTask()
								.getTaskInternals(
										this.getCentralAssignmentOrders()
												.getChosenSkillName()) != null)) {
					randomTaskToWork.workOnTaskCentrallyControlled(this);
				} else
					randomTaskToWork.workOnTask(this, SkillChoice.RANDOM);
				EnvironmentEquilibrium.setActivity(true);
			} else {
				say("Agent " + this.id + " didn't work on anything");
				sanity("Agent " + this.id
						+ " don't have a task to work on in step " + getTick());
			}
		}

	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		say("Agent's login set to: " + nick);
		this.nick = nick;
	}

	public String getName() {
		return this.toString() + " (" + this.firstName + " " + this.lastName;
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

	private double getTick() {
		return getGameController().getCurrentTick();
	}

	public Strategy getStrategy() {
		return strategy;
	}

	public Strategy.SkillChoice getSkillStrategy() {
		return strategy.skillChoice;
	}

	public Strategy.TaskChoice getTaskStrategy() {
		return strategy.getTaskChoice();
	}

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

	public CentralAssignmentOrders getCentralAssignmentOrders() {
		return centralAssignmentOrders;
	}

	public void setCentralAssignmentOrders(
			CentralAssignmentOrders centralAssignmentOrders) {
		if (centralAssignmentOrders != null) {
			say("Agent " + this.nick + " got an order to work on "
					+ centralAssignmentOrders);
		}
		this.centralAssignmentOrders = centralAssignmentOrders;
	}

	public String describeExperience() {
		Collection<AgentInternals> internals = this.getAgentInternals();
		Map<String, String> deltaE = new HashMap<String, String>();
		for (AgentInternals ai : internals) {
			deltaE.put(ai.getSkill().getName(), (new DecimalFormat("#.######"))
					.format(ai.getExperience().getDelta()));
		}
		return deltaE.entrySet().toString();
	}

	public String getGeneralExperience() {
		Collection<AgentInternals> internals = this.getAgentInternals();
		double sum = 0.0;
		for (AgentInternals ai : internals) {
			sum += ai.getExperience().getDelta();
		}
		return new DecimalFormat("#.######").format((sum / internals.size()));
	}

	public Double getFilteredExperience(Collection<Skill> common) {
		Collection<AgentInternals> internals = this.getAgentInternals();
		double sum = 0.0;
		int count = 0;
		for (AgentInternals ai : internals) {
			if (common.contains(ai.getSkill())) {
				count++;
				sum += ai.getExperience().getDelta();
			}
		}
		return sum / count;
	}

	public Double getExperience(Skill skill) {
		return this.describeExperience(skill, false, false);
	}

	/***
	 * Returns the experience of Agent in a particular programming Skill
	 * 
	 * @param skill
	 * @param unknownSkillIsZero
	 *            - if true, returns 0.0 instead of null, when Skill not found
	 *            in Agent's set of skills
	 * @param forceCreate
	 *            - if Agent don't possess this skill, but argument is set as
	 *            true, he will now have it, use with caution and reason
	 * @return Double - agent's experience in given skill
	 */
	public Double describeExperience(Skill skill, Boolean unknownSkillIsZero,
			Boolean forceCreate) {
		if (getCurrentSkills().get(skill.getName()) == null) {
			if (forceCreate) {
				AgentInternals result = (new AgentInternals(SkillFactory
						.getInstance().getSkill(skill.getName()),
						new Experience(true)));
				getCurrentSkills().put(skill.getName(), result);
			} else {
				if (unknownSkillIsZero) {
					return 0d;
				} else {
					return null;
				}
			}
		}
		return getCurrentSkills().get(skill.getName()).getExperience()
				.getDelta();
	}

	private Map<String, AgentInternals> getCurrentSkills() {
		return agentSkills.getSkills();
	}

	public AgentSkills getAgentSkills() {
		return agentSkills;
	}

	public void setAgentSkills(AgentSkills agentSkills) {
		this.agentSkills = agentSkills;
	}

	public int usesHomophyly() {
		return this.strategy.getTaskChoice().equals(TaskChoice.HOMOPHYLY)
				|| this.strategy.getTaskChoice().equals(
						TaskChoice.HOMOPHYLY_EXP) ? 1 : 0;
	}

	public int usesHeterophyly() {
		return this.strategy.getTaskChoice().equals(TaskChoice.HETEROPHYLY)
				|| this.strategy.getTaskChoice().equals(
						TaskChoice.HETEROPHYLY_EXP) ? 1 : 0;
	}

	public int usesPreferential() {
		return this.strategy.getTaskChoice().equals(TaskChoice.PREFERENTIAL) ? 1
				: 0;
	}

	@ProbeID()
	@Override
	public String toString() {
		return getNick();
	}

	@Override
	public int hashCode() {
		return nick.hashCode() * id;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Agent)) {
			return false;
		}
		if ((this.id == ((Agent) obj).id)
				&& (this.nick.toLowerCase().equals((((Agent) obj).nick
						.toLowerCase()))))
			return true;
		else
			return false;
	}

	@Override
	public Agent createNode(String label) {
		return createNode("Agent-" + getNick());
	}

	public boolean wasWorkingOnAnything() {
		return PersistJobDone.getJobDone().containsKey(this.getNick());
	}

	private void say(String s) {
		PjiitOutputter.say(s);
	}

	private void sanity(String s) {
		PjiitOutputter.sanity(s);
	}
}

class EnvironmentEquilibrium {

	private static boolean activity = false;

	public static synchronized boolean getActivity() {
		return activity;
	}

	public static synchronized void setActivity(boolean defineActivity) {
		activity = defineActivity;
	}

}