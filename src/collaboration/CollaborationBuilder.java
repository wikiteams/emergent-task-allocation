package collaboration;

import github.AgentModeling;
import github.MyDatabaseConnector;
import intelligence.EquilibriumDetector;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import load.AgentCount;
import load.ExpDecayOption;
import load.FunctionSet;
import load.GenerationLength;
import load.GranularityOption;
import load.LeftFunctionSet;
import load.ParametersDivider;
import load.SkillStrategySet;
import load.TaskCount;
import load.TaskStrategySet;
import logger.EndRunLogger;
import logger.PjiitLogger;
import logger.SanityLogger;
import logger.ValidationOutputter;
import logger.VerboseLogger;
import networking.CollaborationNetwork;
import networking.DynamicGexfGraph;

import org.ini4j.InvalidFileFormatException;

import repast.simphony.context.Context;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.environment.RunInfo;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.ISchedulableAction;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.collections.IndexedIterable;
import strategies.CentralPlanning;
import strategies.StrategyDistribution;
import utils.ObjectsHelper;
import argonauts.PersistJobDone;
import constants.Constraints;

/**
 * COIN network emergence simulator, a Repast Simphony 2.2 multi-agent social
 * simulation for modelling task allocation techniques and behaviour of
 * collaborators in websites like GitHub and Wikipedia. Works on both Windows
 * and Linux environments.
 * 
 * Code repository https://github.com/wikiteams/emergent-task-allocation
 * 
 * Repast License: The Repast suite software and documentation is licensed under
 * a "New BSD" style license. Please note that Repast Simphony uses a variety of
 * tools and third party external libraries each having its own compatible
 * license, including software released under the Eclipse Public License, the
 * Common Public License, the GNU Library General Public License and other
 * licenses.
 * 
 * Simulation (Project) uses library "Common Beanutils" which is licensed under
 * Apache License
 * 
 * Project uses ini4j library which is licensed under Apache License.
 * 
 * @version 2.0.11 'Turanga Leela'
 * @category Agent-organised Social Simulations
 * @since 1.0
 * @author Oskar Jarczyk, Blazej Gruszka et al.
 * @see 1) GitHub markdown 2) "On The Effectiveness of Emergent Task Allocation"
 */
public class CollaborationBuilder implements ContextBuilder<Object> {

	/**
	 * This value is used to automatically generate agent identifiers.
	 * 
	 * The number 9,223,372,036,854,775,807 is an integer equal to 2^63 - 1.
	 * Although of the form 2^n - 1, it is not a Mersenne prime. It has a
	 * factorization of 72 ; 73 ; 127 ; 337 ; 92737 ; 649657, which is equal to
	 * fi_1(2) ; fi_3(2) ; fi_7(2) ; fi_9(2) ; fi_21(2) ; fi_63(2). Equivalent
	 * to the hexadecimal value 7FFF,FFFF,FFFF,FFFF16, is the maximum value for
	 * a 64-bit signed integer in computing.
	 * 
	 * @field serialVersionUID
	 */
	public static final long serialVersionUID = 9223372036854775807L;
	public static Context<Task> tasks;
	public static Context<Agent> agents;

	private StrategyDistribution strategyDistribution;
	private SkillFactory skillFactory;
	private Schedule schedule = new Schedule();

	private GameController gameController;
	private RunInfo runInfo;
	private CentralPlanning centralPlanner;

	public CollaborationBuilder() {
		try {
			initializeLoggers();
			VerboseLogger.say("[Loggers] initialized...");
		} catch (IOException e) {
			e.printStackTrace();
			VerboseLogger.say(Constraints.ERROR_INITIALIZING_PJIITLOGGER);
		} catch (Exception exc) {
			VerboseLogger.say(exc.toString());
			exc.printStackTrace();
			VerboseLogger
					.say(Constraints.ERROR_INITIALIZING_PJIITLOGGER_AO_PARAMETERS);
		} finally {
			VerboseLogger
					.say("[CollaborationBuilder constructor] finished execution");
			// this is where Repast waits for scenario lunch
			// (context builds up and build() method executes)
		}
	}

	private void prepareDataControllers() throws InvalidFileFormatException,
			IOException {
		VerboseLogger.say(Constraints.LOADING_PARAMETERS);
		SimulationParameters.init();
		// getting parameters of a simulation from current scenario

		RandomHelper.setSeed(SimulationParameters.randomSeed);
		RandomHelper.init();
		VerboseLogger.say("[RandomHelper] initialized...");

		/***
		 * StrategyDistribution holds information on currently tested Task
		 * assignment strategy and Skill choice strategy Single distribution
		 * means evolution disabled, while multiple distribution enables
		 * evolutionary model
		 */
		strategyDistribution = new StrategyDistribution();

		// initialise skill pools - information on all known languages
		VerboseLogger.say("[SkillFactory] parsing skills"
				+ " (programing languages) from file");
		skillFactory = SkillFactory.getInstance();
		skillFactory.buildSkillsLibrary(false);
		VerboseLogger.say("[SkillFactory] parsed all known"
				+ " [programming languages].");
	}

	private void prepareWorkLoadData() {

		/***
		 * This is new dataset parsed from our GitHub MongoDB database and
		 * specially created for the sake of evolutionary model
		 */
		AgentModeling.instantiate();
		VerboseLogger.say("[Sqlite engine] and resultset"
				+ " initialized, may take some time..");
		MyDatabaseConnector.init();

		strategyDistribution.setType(SimulationParameters.evolutionEnabled);

		if (strategyDistribution.isSingle()) {
			strategyDistribution
					.setSkillChoice(SkillStrategySet.INSTANCE.getChosenName());
			strategyDistribution
					.setTaskChoice(TaskStrategySet.INSTANCE.getChosenName());
		} else if (strategyDistribution.isMultiple()) {
			strategyDistribution
					.setSkillChoice(SkillStrategySet.INSTANCE.getChosenName());
			strategyDistribution
					.setTaskChoiceSet(SimulationParameters.planNumber);
		}
	}

	public void prepareGameController(Context<Object> context) {
		if (SimulationParameters.evolutionEnabled < 1){
			gameController = new GameController(strategyDistribution, true);
		} else {
			// Sir, evolution is enabled, enable game controller
			gameController = new GameController(strategyDistribution, false);
			EquilibriumDetector.init();
		}
		context.add(gameController);
	}

	@Override
	public Context<Object> build(Context<Object> context) {
		runInfo = RunState.getInstance().getRunInfo();
		Integer batchNumber = runInfo.getBatchNumber();
		Integer runNumber = RunState.getInstance().getRunInfo().getRunNumber();
		System.out
				.println("CollaborationBuilder is building [context], sweep run no: "
						+ batchNumber
						+ ","
						+ runNumber
						+ " is_batch: "
						+ runInfo.isBatch());

		context.setId("emergent-task-allocation");

		Preprocess.clearStaticHeap();
		VerboseLogger.say("[Static heap] cleared..");

		if (SimulationAdvancedParameters.enableNetwork) {
			NetworkBuilder<Object> builder = new NetworkBuilder<Object>(
					"TasksAndWorkers", context, false);
			CollaborationNetwork.collaborationNetwork = builder.buildNetwork();
			CollaborationNetwork.gephiEngine = new DynamicGexfGraph().init();
		}

		try {
			// prepare e.g. skill factory
			prepareDataControllers();
			// divide set of parameters into subscenarios
			ParametersDivider.findMatch(runNumber, SimulationParameters.sweepRuns);
			// prepare sqlite and other factories
			prepareWorkLoadData();
		} catch (InvalidFileFormatException e) {
			ValidationOutputter.error(e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			ValidationOutputter.error(e.toString());
			e.printStackTrace();
		}

		tasks = new Tasks(TaskCount.INSTANCE.getChosen());
		context.addSubContext(tasks);
		agents = new Agents(strategyDistribution,
				AgentCount.INSTANCE.getChosen());
		context.addSubContext(agents);

		prepareGameController(context);

		assert context.getObjects(GameController.class).size() > 0;
		VerboseLogger
				.say("[Game Controller] initialized and added to context.");

		VerboseLogger
				.say("Task choice [Strategy] is "
						+ (strategyDistribution.isDistributionLoaded() ? strategyDistribution
								.getStrategySet().describe()
								: TaskStrategySet.INSTANCE.getChosenName()));
		sanity("Number of [Tasks] created " + getTasks().size());
		sanity("Number of [Agents] created " + getAgents().size());
		sanity("[Algorithm] tested: "
				+ (strategyDistribution.isDistributionLoaded() ? "Distributed"
						: TaskStrategySet.INSTANCE.getChosenName()));

		buildCentralPlanner();
		buildContinousTaskFlow();
		buildExperienceReassessment();

		List<ISchedulableAction> actions = schedule.schedule(this);
		VerboseLogger.say(actions.toString());

		context.add(this); // it will make sure ScheduledMethods are run

		return context;
	}

	private IndexedIterable<Task> getTasks() {
		Context<Task> context = tasks;
		return context.getObjects(Task.class);
	}

	private IndexedIterable<Agent> getAgents() {
		Context<Agent> context = agents;
		return context.getObjects(Agent.class);
	}

	private void initializeLoggers() throws IOException {
		PjiitLogger.init();
		VerboseLogger.say(Constraints.LOGGER_INITIALIZED);
		SanityLogger.init();
		sanity(Constraints.LOGGER_INITIALIZED);
		EndRunLogger.init();
		EndRunLogger.buildHeaders(buildFinalMessageHeader());
	}
	
	@ScheduledMethod(start = 25000, interval = 25000, priority = ScheduleParameters.LAST_PRIORITY)
	public void makeMoreMemory() {
		System.gc(); // I realise there is no guarantee of vacuum cleaning,
		// but I want to make a try...
		// Memory is sometimes a problem when tick count > 200k
	}

	@ScheduledMethod(start = 2, interval = 1, priority = ScheduleParameters.FIRST_PRIORITY)
	/***
	 * Because of a continuous work in an evolutionary model, we don't finish
	 * simulation without a good reason. This method will be enabled but in
	 * second part of the game - calculating efficiency of a static set of
	 * strategies
	 */
	public void finishSimulation() {
		VerboseLogger
				.say("[finishSimulation() check launched] Checking if simulation can be ended...");
		EnvironmentEquilibrium.setActivity(false);
		if (gameController.isEvolutionary()) {
			if (EquilibriumDetector.evaluate(gameController)) {
				VerboseLogger
						.say("[Stable set of Strategies] detected, finishing simulation");
				EndRunLogger.finalMessage((buildFinalMessage()));
				RunEnvironment.getInstance().endRun();
				// cleanAfter();
			}
		} else if (((Tasks) tasks).getCount() < 1) {
			VerboseLogger
					.say("Count of [Task Pool] is < 1, finishing simulation");
			EndRunLogger.finalMessage((buildFinalMessage()));
			RunEnvironment.getInstance().endRun();
			// cleanAfter();
		}
	}

	@ScheduledMethod(start = 1, interval = 1, priority = ScheduleParameters.LAST_PRIORITY)
	/***
	 * Because of a continuous work in an evolutionary model, we don't finish
	 * simulation without a good reason. This method will be enabled but in
	 * second part of the game - calculating efficiency of a static set of
	 * strategies
	 */
	public void checkForActivity() {
		VerboseLogger
				.say("[checkForActivity() check launched] Checking if there was any work at all in current Tick");
		if (EnvironmentEquilibrium.getActivity() == false) {
			VerboseLogger
					.say("EnvironmentEquilibrium.getActivity() returns false!");
			EndRunLogger.finalMessage((buildFinalMessage()));
			RunEnvironment.getInstance().endRun();
			// cleanAfter();
		}
	}

	@ScheduledMethod(start = 1, interval = 1, priority = ScheduleParameters.FIRST_PRIORITY)
	public void clearCollaborationNetwork() {
		CollaborationNetwork.clear();
	}

	private String buildFinalMessage() {
		return RunState.getInstance().getRunInfo().getBatchNumber()
				+ ","
				+ RunState.getInstance().getRunInfo().getRunNumber()
				+ ","
				+ RunEnvironment.getInstance().getCurrentSchedule()
						.getTickCount()
				+ ","
				+ AgentCount.INSTANCE.getChosen()
				+ ","
				+ TaskCount.INSTANCE.getChosen()
				+ ","
				+ getTaskLeft()
				+ ","
				+ ExpDecayOption.INSTANCE.getChosen()
				+ ","
				+ SimulationParameters.allowSkillDeath
				+ ","
				+ SimulationParameters.experienceCutPoint
				+ ","
				+ GranularityOption.INSTANCE.getChosen()
				+ ","
				+ SimulationParameters.granularityType
				+ ","
				+ SimulationParameters.granularityObstinacy
				+ ","
				+ strategyDistribution.getTaskChoice()
				+ ","
				+ strategyDistribution.getSkillChoice()
				+ ","
				+ gameController.isEvolutionary()
				+ ","
				+ SimulationParameters.planNumber
				+ ","
				+ FunctionSet.INSTANCE.getChosen()
				+ ","
				+ LeftFunctionSet.INSTANCE.getChosen()
				+ ","
				+ gameController.getCurrentGeneration()
				+ ","
				+ GenerationLength.INSTANCE.getChosen()
				+ ","
				+ SimulationParameters.allwaysChooseTask
				+ ","
				+ gameController
						.countHomophilyDistribution(getCurrentContext())
				+ ","
				+ gameController
						.countHeterophilyDistribution(getCurrentContext())
				+ ","
				+ gameController
						.countPreferentialDistribution(getCurrentContext());
	}

	private int getTaskLeft() {
		Context<Object> context = getCurrentContext();
		int left = 0;
		for (Object task : context.getObjects(Task.class)) {
			if (task.getClass().getName().equals("collaboration.Task")) {
				if ((((Task) task).getTaskInternals().size() > 0)
						&& (((Task) task).getGeneralAdvance() < 1.)) {
					left++;
				}
			}
		}
		return left;
	}

	private String buildFinalMessageHeader() {
		return "Batch_Number" + "," + "Run_Number" + "," + "Tick_Count" + ","
				+ "Agents_Count" + "," + "Tasks_Count" + "," + "Tasks_Left"
				+ "," + "Experience_Decay" + "," + "Allow_Skill_Death" + ","
				+ "Exp_cut_point" + "," + "Granularity" + ","
				+ "Granularity_type" + "," + "Granularity_obstinancy" + ","
				+ "Task_choice_strategy" + "," + "Skill_choice_strategy" + ","
				+ "Is_Evolutionary" + "," + "Plan_Number" + ","
				+ "Utility_Type" + "," + "UtilityLeftValue" + ","
				+ "Generation" + ","
				+ "GenerationLength" + "," + "Allways_Choose_Task" + ","
				+ "Homophily_Count" + "," + "Heterophily_Count" + ","
				+ "Preferential_Count";
	}

	private void validationError(String s) {
		ValidationOutputter.error(s);
	}

	private void validationFatal(String s) {
		ValidationOutputter.fatal(s);
	}

	private void sanity(String s) {
		VerboseLogger.sanity(s);
	}

	/**
	 * This is the method scheduled (if at least 1 agent uses central planner)
	 * to execute every tick to make for giving orders, hence zeroing the orders
	 * first (clearing previous orders) and than making the math
	 */
	public void centralPlanning() {
		VerboseLogger
				.say("CentralPlanning scheduled method launched, listAgent.size(): "
						+ getAgents().size()
						+ " taskPool.size(): "
						+ ((Tasks) tasks).getCount());
		VerboseLogger.say("Zeroing agents' orders");
		centralPlanner.zeroAgentsOrders(getAgents());
		centralPlanner.centralPlanningCalc(getAgents(), (Tasks) tasks);
	}

	/**
	 * Here I need to schedule method manually because. In first version of
	 * simulator the Central assignment strategy was non-evolutionary but now in
	 * hybrid model it can take work for a subset of Agents as well.
	 */
	public void buildCentralPlanner() {
		VerboseLogger.say("Method buildCentralPlanner lunched."
				+ "Checking now if central planer is needed at all.");
		if (strategyDistribution.isCentralPlannerEnabled()) {
			VerboseLogger.say("Creating a central planner instance.");
			centralPlanner = CentralPlanning.getSingletonInstance();
			VerboseLogger.say("Central planner is initiating schedule.");
			ISchedule schedule = RunEnvironment.getInstance()
					.getCurrentSchedule();
			ScheduleParameters params = ScheduleParameters.createRepeating(1,
					1, ScheduleParameters.FIRST_PRIORITY);
			schedule.schedule(params, this, "centralPlanning");
			VerboseLogger
					.say("Central planner initiated and awaiting for call.");
		}
	}

	public void buildContinousTaskFlow() {
		if (gameController.isEvolutionary()) {
			ISchedule schedule = RunEnvironment.getInstance()
					.getCurrentSchedule();
			ScheduleParameters params = ScheduleParameters.createRepeating(1,
					1, ScheduleParameters.LAST_PRIORITY);
			schedule.schedule(params, this, "provideSimulatorWithWork");
			VerboseLogger.say("[Continous Task Flow] initiated");
		} else {
			VerboseLogger.say("Task number is static, task flow builder skipped");
		}
	}

	/***
	 * In case of non-evolutionary simulation, launched once at start
	 * Otherwise, method simply checks whether we should add more work
	 * (new task) to the simulator or not
	 */
	public synchronized void provideSimulatorWithWork() {
		if (tasks.size() < TaskCount.INSTANCE.getChosen()) {
			int minus = TaskCount.INSTANCE.getChosen() - ((Tasks) tasks).getCount();
			int difference = Math.abs(minus);
			VerboseLogger.say("Adding more " + difference
					+ " [Tasks] to simulator");
			try {
				List<Task> newTasks = MyDatabaseConnector.get(difference);
				for (Task newTask : newTasks) {
					VerboseLogger
							.say("[Continous Task Flow] adding a new task to context!");
					tasks.add(newTask);
				}
			} catch (SQLException e) {
				VerboseLogger
						.say("Error during providing simulator with a new [Task(s)]");
				e.printStackTrace();
			}
		}
	}

	/***
	 * Implemented skill - forgetting, which can be enabled through parameters a
	 * scheduled method if set in the scenario parameters when needed, lunched
	 * AgentInternalls.decayExperience()
	 */
	public synchronized void experienceReassess() {
		try {
			IndexedIterable<Agent> agentObjects = agents
					.getObjects(Agent.class);
			for (Agent agent : agentObjects) {
				VerboseLogger
						.say("Checking if I may have to [decrease exp] of "
								+ agent);
				if (!agent.getAgentSkills().hasAny()) {
					continue;
				}

				// Use PersistJobDone to check work history
				Map<Integer, List<Skill>> c = PersistJobDone
						.getSkillsWorkedOn(agent);

				List<Skill> persistedJob = c != null ? c.get(ObjectsHelper
						.fromDouble(gameController.getCurrentTick())) : null;
				if (persistedJob == null) {
					persistedJob = new ArrayList<Skill>();
				}

				Collection<AgentInternals> aic = (agent).getAgentInternals();
				CopyOnWriteArrayList<AgentInternals> aicconcurrent = 
						new CopyOnWriteArrayList<AgentInternals>(
						aic);
				for (AgentInternals ai : aicconcurrent) {
					if (persistedJob.contains(ai.getSkill())) {
						// was working on a task, don't decay this skill
					} else {
						// decay this experience by beta < 1
						if (SimulationParameters.allowSkillDeath) {
							boolean result = ai.decayExperienceWithDeath();
							if (result) {
								(agent).removeSkill(ai.getSkill(), false);
							}
						} else {
							double value = ai.decayExperience();
							if (value == -1) {
								VerboseLogger
										.say("[Experience] of [Agent] "
												+ (agent.getNick())
												+ " wasn't decreased because it's already low");
							} else
								VerboseLogger.say("[Experience] of [Agent] "
										+ (agent.getNick())
										+ " decreased and is now " + value);
						}
					}
				}
			}
		} catch (Exception exc) {
			validationFatal(exc.toString());
			validationError(exc.getMessage());
			exc.printStackTrace();
		} finally {
			VerboseLogger
					.say("Regular method run for [expDecay] finished for this step.");
		}
	}

	/**
	 * Here I need to schedule method manually because I don't know if expDecay
	 * is enabled for the simulation whether not.
	 */
	public void buildExperienceReassessment() {
		VerboseLogger.say("buildExperienceReassessment() lunched !");
		if (ExpDecayOption.INSTANCE.getChosen()) {
			if (SimulationParameters.allowSkillDeath == true) {
				VerboseLogger
						.say("[Allow skill abandon] is enabled for this run");
			}
			VerboseLogger.say("[Exp decay] is enabled for this run");
			ISchedule schedule = RunEnvironment.getInstance()
					.getCurrentSchedule();
			ScheduleParameters params = ScheduleParameters.createRepeating(1,
					1, ScheduleParameters.LAST_PRIORITY);
			schedule.schedule(params, this, "experienceReassess");
			VerboseLogger
					.say("Experience decay initiated and awaiting for call !");
		} else {
			VerboseLogger.say("[Exp decay] is disabled for this run");
		}
	}

	private Context<Object> getCurrentContext() {
		return ContextUtils.getContext(this);
	}

}
