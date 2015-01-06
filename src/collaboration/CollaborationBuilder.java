package collaboration;

import github.AgentModeling;
import github.DataSet;
import github.MyDatabaseConnector;
import github.TaskSkillFrequency;
import github.TaskSkillsPool;
import intelligence.ImpactFactor;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import logger.EndRunLogger;
import logger.PjiitLogger;
import logger.PjiitOutputter;
import logger.SanityLogger;
import logger.ValidationLogger;
import logger.ValidationOutputter;
import networking.CollaborationNetwork;
import networking.DynamicGexfGraph;

import org.apache.log4j.LogManager;

import repast.simphony.context.Context;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.ISchedulableAction;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.util.collections.IndexedIterable;
import strategies.CentralPlanning;
import strategies.StrategyDistribution;
import test.Model;
import utils.DescribeUniverseBulkLoad;
import utils.LaunchStatistics;
import utils.NamesGenerator;
import argonauts.PersistJobDone;
import argonauts.PersistRewiring;
import constants.Constraints;
import constants.LoadSet;
import constants.ModelFactory;

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
 * @version 2.0.6 Work Evolve
 * @category Agent-organised Social Simulations
 * @since 1.0
 * @author Oskar Jarczyk, Bla\zej Gruszka et.al.
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

	private Context<Object> currentContext;

	private StrategyDistribution strategyDistribution;
	private ModelFactory modelFactory;
	private SkillFactory skillFactory;
	private LaunchStatistics launchStatistics;
	private Schedule schedule = new Schedule();

	private LoadSet loadSet;
	private DataSet dataSet;
	private GameController gameController;

	private CentralPlanning centralPlanner;

	private boolean alreadyFlushed = false;

	public CollaborationBuilder() {
		try {
			initializeLoggers();
			// RandomHelper.setSeed(SimulationParameters.randomSeed);
			// RandomHelper.init();
			// clearStaticHeap();
			// moved to context builder build() method
			say("[RandomHelper] initialized and [static heap] cleared..");
		} catch (IOException e) {
			e.printStackTrace();
			say(Constraints.ERROR_INITIALIZING_PJIITLOGGER);
		} catch (Exception exc) {
			say(exc.toString());
			exc.printStackTrace();
			say(Constraints.ERROR_INITIALIZING_PJIITLOGGER_AO_PARAMETERS);
		} finally {
			say("[CollaborationBuilder constructor] finished execution");
			// this is where Repast waits for scenario lunch (contexts build)
		}
	}

	@SuppressWarnings("deprecation")
	private void prepareDataControllers() {
		try {
			/***
			 * LoadSet.EMPTY singleton is here filled with values
			 * LoadSet holds information about task numbers and agent numbers
			 * those values are used later for load control
			 */
			loadSet = LoadSet.EMPTY;

			say(Constraints.LOADING_PARAMETERS);
			SimulationParameters.init();
			// getting parameters of a simulation from current scenario
			
			RandomHelper.setSeed(SimulationParameters.randomSeed);
			RandomHelper.init();
			
			dataSet = DataSet.getInstance(SimulationParameters.dataSource);

			/***
			 * LaunchStatistics hold data for outputting results i.e. tick
			 * numbers, result strategy set, tasks left etc.
			 * they are all used later for outputting to batch.log
			 * at the very end of a simulation
			 */
			launchStatistics = LaunchStatistics.getInstance();

			/***
			 * ModelFactory tells whether we want to maximise verbose message
			 * and/or test all Task assignment STRATEGIES at once.
			 * Normal model means a typical execution, while i.e.
			 * validation means e.g. maximum message output to console etc.
			 */
			modelFactory = new ModelFactory(SimulationParameters.modelType);
			Model model = modelFactory.getFunctionality();

			say("Starting simulation with model: " + modelFactory.toString());
			if (model.isValidation())
				initializeValidationLogger();
			if (SimulationParameters.multipleAgentSets) {
				// here we decide if we want different sets of agent count /
				// task count i don't need this for evolution at this time,
				// neither for single computing, thus lets make sure we don't
				// enable this without a good reason
				loadSet = DescribeUniverseBulkLoad.init();
			} else {
				loadSet.AGENT_COUNT = SimulationParameters.agentCount;
				loadSet.TASK_COUNT = SimulationParameters.taskCount;
			}

			/***
			 * StrategyDistribution holds information on currently tested Task
			 * assignment strategy and Skill choice strategy
			 * Single distribution means evolution disabled, while
			 * multiple distribution enables evolutionary model
			 */
			strategyDistribution = new StrategyDistribution();

			// initialise skill pools - information on all known languages
			say("[SkillFactory] parsing skills (programing languages) from file");
			skillFactory = SkillFactory.getInstance();
			skillFactory.buildSkillsLibrary(model.isValidation());
			say("[SkillFactory] parsed all known [programming languages].");
		} catch (IOException e) {
			e.printStackTrace();
			say(Constraints.ERROR_INITIALIZING_PARAMETERS);
		} catch (Exception exc) {
			say(exc.toString());
			exc.printStackTrace();
			say(Constraints.ERROR_INITIALIZING_PJIITLOGGER_AO_PARAMETERS);
		}
	}

	@SuppressWarnings("deprecation")
	private void prepareWorkLoadData() {

		if (dataSet.isMockup()) {
			/***
			 * This is previous version of the dataset, already tested and
			 * described in our publication hence the deprecated flag
			 */
			AgentSkillsPool.instantiate(SimulationParameters.agentSkillPoolDataset);
			say("[Instatiated AgentSkillsPool]");
			TaskSkillsPool.instantiate(SimulationParameters.tasksDataset);
			say("[Instatied TaskSkillsPool]");
		} else if (dataSet.isDb()) {
			/***
			 * This is new dataset parsed from our 
			 * GitHub MongoDB database and specially
			 * created for the sake of [evolutionary model]
			 */
			AgentModeling.instantiate(SimulationParameters.agentSkillPoolDataset);
			say("[Sqlite engine] and resultset initialized, may take some time..");
			MyDatabaseConnector.init();
		} else if (dataSet.isContinuus()) {
			// TODO: implement rest (e.g. socketlistener etc.)
		}

		strategyDistribution.setType(SimulationParameters.strategyDistribution);

		assert ((strategyDistribution.getType() == StrategyDistribution.SINGULAR) || (strategyDistribution
				.getType() == StrategyDistribution.MULTIPLE));

		if (strategyDistribution.isSingle()) {
			strategyDistribution.setSkillChoice(modelFactory,
					SimulationParameters.skillChoiceAlgorithm);
			strategyDistribution.setTaskChoice(modelFactory,
					SimulationParameters.taskChoiceAlgorithm);
		} else if (strategyDistribution.isMultiple()) {
			strategyDistribution.setSkillChoice(modelFactory,
					SimulationParameters.skillChoiceAlgorithm);
			strategyDistribution
					.setTaskChoiceSet(SimulationParameters.planNumber);
		}
	}

	@Override
	public Context<Object> build(Context<Object> context) {
		context.setId("emergent-task-allocation");
		currentContext = context;
		
		clearStaticHeap();

		if (SimulationAdvancedParameters.enableNetwork) {
			NetworkBuilder<Object> builder = new NetworkBuilder<Object>(
					"TasksAndWorkers", context, false);
			CollaborationNetwork.collaborationNetwork = builder.buildNetwork();
			CollaborationNetwork.gephiEngine = new DynamicGexfGraph().init();
		}

		prepareDataControllers();
		prepareWorkLoadData();

		tasks = new Tasks(loadSet.TASK_COUNT);
		context.addSubContext(tasks);
		agents = new Agents(strategyDistribution, loadSet.AGENT_COUNT);
		context.addSubContext(agents);

		gameController = new GameController(strategyDistribution);
		context.add(gameController);

		say("Task choice [Strategy] is "
				+ (strategyDistribution.isDistributionLoaded() ? strategyDistribution
						.getStrategySet().describe()
						: SimulationParameters.taskChoiceAlgorithm));
		sanity("Number of [Tasks] created " + getTasks().size());
		sanity("Number of [Agents] created " + getAgents().size());
		sanity("[Algorithm] tested: "
				+ (strategyDistribution.isDistributionLoaded() ? "Distributed"
						: SimulationParameters.taskChoiceAlgorithm));

		if (SimulationParameters.forceStop)
			RunEnvironment.getInstance().endAt(SimulationParameters.numSteps);

		buildCentralPlanner();
		buildContinousTaskFlow();
		buildExperienceReassessment();
		buildAgentsWithdrawns();
		decideAboutGranularity();
		decideAboutCutPoint();

		List<ISchedulableAction> actions = schedule.schedule(this);
		say(actions.toString());

		context.add(this); // it will make sure ScheduledMethods are run

		return context;
	}

	private IndexedIterable<Object> getTasks() {
		Context<Object> context = getCurrentContext();
		return context.getObjects(Task.class);
	}

	private IndexedIterable<Object> getAgents() {
		Context<Object> context = getCurrentContext();
		return context.getObjects(Agent.class);
	}

	private void initializeLoggers() throws IOException {
		PjiitLogger.init();
		say(Constraints.LOGGER_INITIALIZED);
		SanityLogger.init();
		sanity(Constraints.LOGGER_INITIALIZED);
		EndRunLogger.init();
		EndRunLogger.buildHeaders(buildFinalMessageHeader());
	}

	private void initializeValidationLogger() {
		ValidationLogger.init();
		say(Constraints.VALIDATION_LOGGER_INITIALIZED);
		validation(Constraints.SEPERATOR);
	}

	@SuppressWarnings("deprecation")
	public void clearStaticHeap() {
		say("Clearing [static data] from previous simulation");
		sanity("Hence despite the fact there is a seperate JVM " + 
				"for every instance, a new run should clear previous results");
		PersistJobDone.clear();
		PersistRewiring.clear();
		TaskSkillsPool.clear();
		SkillFactory.skills.clear();
		NamesGenerator.clear();
		Tasks.clearTasks();
		AgentSkillsPool.clear();
		Agent.totalAgents = 0;
		TaskSkillsPool.static_frequency_counter = 0;
		TaskSkillFrequency.clear();
		AgentSkillsFrequency.clear();
		AgentModeling.clear();
		ImpactFactor.clear();
	}

	@ScheduledMethod(start = 2, interval = 1, priority = ScheduleParameters.FIRST_PRIORITY)
	/***
	 * Because of a continuous work in an evolutionary model, we don't finish
	 * simulation without a good reason. This method will be enabled but in
	 * second part of the game - calculating efficiency of a static set of
	 * strategies
	 */
	public void finishSimulation() {
		say("[finishSimulation() check launched] Checking if simulation can be ended...");
		EnvironmentEquilibrium.setActivity(false);
		if (((Tasks) tasks).getCount() < 1) {
			say("count of taskPool is < 1, finishing simulation");
			finalMessage(buildFinalMessage());
			RunEnvironment.getInstance().endRun();
			cleanAfter();
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
		say("[checkForActivity() check launched] Checking if there was any work at all in current Tick");
		if (EnvironmentEquilibrium.getActivity() == false) {
			say("EnvironmentEquilibrium.getActivity() returns false!");
			finalMessage(buildFinalMessage());
			RunEnvironment.getInstance().endRun();
			cleanAfter();
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
						.getTickCount() + "," + launchStatistics.agentCount
				+ "," + launchStatistics.taskCount + "," + getTaskLeft() + ","
				+ launchStatistics.expDecay + ","
				+ launchStatistics.fullyLearnedAgentsLeave + ","
				+ launchStatistics.experienceCutPoint + ","
				+ launchStatistics.granularity + ","
				+ launchStatistics.granularityType + ","
				+ SimulationParameters.granularityObstinacy + ","
				+ strategyDistribution.getTaskChoice() + ","
				+ SimulationParameters.fillAgentSkillsMethod + ","
				+ SimulationParameters.agentSkillPoolDataset + ","
				+ SimulationParameters.taskSkillPoolDataset + ","
				+ strategyDistribution.getSkillChoice() + ","
				// + strategyDistribution.getTaskMinMaxChoice() + ","
				+ TaskSkillFrequency.tasksCheckSum + ","
				+ AgentSkillsFrequency.tasksCheckSum;
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
		return "Batch Number" + "," + "Run Number" + "," + "Tick Count" + ","
				+ "Agents count" + "," + "Tasks count" + "," + "Tasks left"
				+ "," + "Experience decay" + "," + "Fully-learned agents leave"
				+ "," + "Exp cut point" + "," + "Granularity" + ","
				+ "Granularity type" + "," + "Granularity obstinancy" + ","
				+ "Task choice strategy" + "," + "fillAgentSkillsMethod" + ","
				+ "agentSkillPoolDataset" + "," + "taskSkillPoolDataset" + ","
				+ "Skill choice strategy" + "," + "Task MinMax choice" + ","
				+ "Task dataset checksum" + "," + "Agent dataset checksum";
	}

	private void cleanAfter() {
		if (!alreadyFlushed) {
			LogManager.shutdown();
			alreadyFlushed = true;
		}
	}

	private void say(String s) {
		PjiitOutputter.say(s);
	}

	private void validation(String s) {
		ValidationOutputter.say(s);
	}

	private void validationError(String s) {
		ValidationOutputter.error(s);
	}

	private void validationFatal(String s) {
		ValidationOutputter.fatal(s);
	}

	private void sanity(String s) {
		PjiitOutputter.sanity(s);
	}

	private void finalMessage(String s) {
		if (modelFactory.getFunctionality().isValidation()) {
			validation(s);
		}
		EndRunLogger.finalMessage(s);
	}

	/**
	 * This is the method scheduled (if at least 1 agent uses central planner)
	 * to execute every tick to make for giving orders, hence zeroing the orders
	 * first (clearing previous orders) and than making the math
	 */
	public void centralPlanning() {
		say("CentralPlanning scheduled method launched, listAgent.size(): "
				+ getAgents().size() + " taskPool.size(): "
				+ ((Tasks) tasks).getCount());
		say("Zeroing agents' orders");
		centralPlanner.zeroAgentsOrders(getAgents());
		centralPlanner.centralPlanningCalc(getAgents(), (Tasks) tasks);
	}

	/**
	 * Here I need to schedule method manually because. In first version of
	 * simulator the Central assignment strategy was non-evolutionary but now in
	 * hybrid model it can take work for a subset of Agents as well.
	 */
	public void buildCentralPlanner() {
		say("Method buildCentralPlanner lunched."
				+ "Checking now if central planer is needed at all.");
		if (strategyDistribution.isCentralPlannerEnabled()) {
			say("Creating a central planner instance.");
			centralPlanner = CentralPlanning.getSingletonInstance();
			say("Central planner is initiating schedule.");
			ISchedule schedule = RunEnvironment.getInstance()
					.getCurrentSchedule();
			ScheduleParameters params = ScheduleParameters.createRepeating(1,
					1, ScheduleParameters.FIRST_PRIORITY);
			schedule.schedule(params, this, "centralPlanning");
			say("Central planner initiated and awaiting for call.");
		}
	}

	public void buildContinousTaskFlow() {
		if (gameController.isEvolutionary()) {
			ISchedule schedule = RunEnvironment.getInstance()
					.getCurrentSchedule();
			ScheduleParameters params = ScheduleParameters.createRepeating(1,
					1, ScheduleParameters.LAST_PRIORITY);
			schedule.schedule(params, this, "provideSimulatorWithWork");
			say("[Continous Task Flow] initiated");
		}
	}

	public synchronized void provideSimulatorWithWork() {
		if (tasks.size() < loadSet.TASK_COUNT) {
			int minus = loadSet.TASK_COUNT - ((Tasks) tasks).getCount();
			int difference = Math.abs(minus);
			say("Adding more " + difference + " [Tasks] to simulator");
			try {
				List<Task> newTasks = MyDatabaseConnector.get(difference);
				for (Task newTask : newTasks) {
					say("[Continous Task Flow] adding a new task to context!");
					tasks.add(newTask);
				}
			} catch (SQLException e) {
				say("Error during providing simulator with a new [Task(s)]");
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

				say("Checking if I may have to decrease exp of " + agent);

				// Use PersistJobDone to check work history
				Map<Integer, List<Skill>> c = PersistJobDone
						.getSkillsWorkedOn(agent);
				if ((c == null) || (c.size() < 1)) { // agent didn't work on
														// anything yet !
					continue; // move on to next agent in pool
				}
				List<Skill> persistedJob = c.get(Integer
						.parseInt(gameController.getCurrentTick().toString()));
				List<Skill> inTickJobDone = persistedJob == null ? new ArrayList<Skill>()
						: persistedJob;

				Collection<AgentInternals> aic = (agent).getAgentInternals();
				CopyOnWriteArrayList<AgentInternals> aicconcurrent = new CopyOnWriteArrayList<AgentInternals>(
						aic);
				for (AgentInternals ai : aicconcurrent) {
					if (inTickJobDone.contains(ai.getSkill())) {
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
								say("Experience of agent "
										+ (agent.getNick())
										+ " wasn't decreased because it's already low");
							} else
								say("Experience of agent " + (agent.getNick())
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
			say("Regular method run for expDecay finished for this step.");
		}
	}

	/**
	 * Here I need to schedule method manually because I don't know if expDecay
	 * is enabled for the simulation whether not.
	 */
	public void buildExperienceReassessment() {
		say("buildExperienceReassessment lunched !");
		if (SimulationParameters.experienceDecay) {
			int reassess = RandomHelper.nextIntFromTo(0, 1);
			// I want in results both expDecay off and on!
			// thats why randomise to use both
			if (reassess == 0) {
				SimulationParameters.experienceDecay = false;
				launchStatistics.expDecay = false;
			} else if (reassess == 1) {
				SimulationParameters.experienceDecay = true;
				launchStatistics.expDecay = true;
				say("Exp decay initiating.....");
				ISchedule schedule = RunEnvironment.getInstance()
						.getCurrentSchedule();
				ScheduleParameters params = ScheduleParameters.createRepeating(
						1, 1, ScheduleParameters.LAST_PRIORITY);
				schedule.schedule(params, this, "experienceReassess");
				say("Experience decay initiated and awaiting for call !");
			} else
				assert false; // reassess is always 0 or 1
		}
	}

	/***
	 * Scheduled methods which checks for 
	 * fully learned agents - only when 'fll' parameter
	 * set as true in scenario parameters
	 * 
	 * @since 1.3
	 */
	public synchronized void agentsWithdrawns() {
		Context<Object> context = getCurrentContext();
		try {
			IndexedIterable<Object> agentObjects = context
					.getObjects(Agent.class);
			CopyOnWriteArrayList<Agent> acconcurrent = new CopyOnWriteArrayList<Agent>();
			for (Object object : agentObjects) {
				acconcurrent.add((Agent) object);
			}
			for (Object agent : acconcurrent) {
				if (agent.getClass().getName().equals("collaboration.Agent")) {
					say("Checking if I may have to force "
							+ (((Agent) agent).getNick()) + " to leave");
					Collection<AgentInternals> aic = ((Agent) agent)
							.getAgentInternals();

					CopyOnWriteArrayList<AgentInternals> aicconcurrent = new CopyOnWriteArrayList<AgentInternals>(
							aic);
					boolean removal = true;
					for (Object ai : aicconcurrent) {
						if (((AgentInternals) ai).getExperience().getDelta() < 1.) {
							say("Agent " + (((Agent) agent).getNick())
									+ " didn't reach maximum in skill "
									+ ((AgentInternals) ai).getSkill());
							removal = false;
						}
					}
					if (removal) {
						say("Agent " + (((Agent) agent).getNick())
								+ " don't have any more skills. Removing agent");
						context.remove(agent);
					}
				}
			}
		} catch (Exception exc) {
			validationFatal(exc.toString());
			validationError(exc.getMessage());
			exc.printStackTrace();
		} finally {
			say("Eventual forcing agents to leave check finished!");
		}
	}

	private Context<Object> getCurrentContext() {
		return currentContext;
	}

	/**
	 * Here I need to schedule method manually because I don't know if
	 * fullyLearnedAgentsLeave is enabled for the simulation whether not.
	 */
	public void buildAgentsWithdrawns() {
		say("buildAgentsWithdrawns lunched !");
		if (SimulationParameters.fullyLearnedAgentsLeave) {
			int reassess = RandomHelper.nextIntFromTo(0, 1);
			// I want in results both expDecay off and on!
			// thats why randomise to use both
			if (reassess == 0) {
				SimulationParameters.fullyLearnedAgentsLeave = false;
				launchStatistics.fullyLearnedAgentsLeave = false;
			} else if (reassess == 1) {
				SimulationParameters.fullyLearnedAgentsLeave = true;
				launchStatistics.fullyLearnedAgentsLeave = true;
				say("Agents withdrawns initiating.....");
				ISchedule schedule = RunEnvironment.getInstance()
						.getCurrentSchedule();
				ScheduleParameters params = ScheduleParameters.createRepeating(
						1, 1, ScheduleParameters.LAST_PRIORITY + 1);
				schedule.schedule(params, this, "agentsWithdrawns");
				say("Agents withdrawns initiated and awaiting for call !");
			} else
				assert false; // reassess is always 0 or 1
		}
	}

	private void decideAboutGranularity() {
		if (SimulationParameters.granularity) {
			if (SimulationParameters.granularityType.equals("DISTRIBUTED")) {
				int threePossibilities = RandomHelper.nextIntFromTo(1, 2);
				switch (threePossibilities) {
				case 1:
					SimulationParameters.granularity = false;
					launchStatistics.granularity = false;
					launchStatistics.granularityType = "OFF";
					break;
				// case 2:
				// SimulationParameters.granularity = true;
				// launchStatistics.granularity = true;
				// SimulationParameters.granularityType = "TASKANDSKILL";
				// launchStatistics.granularityType = "TASKANDSKILL";
				// TODO: i need to think it over more
				case 2:
					SimulationParameters.granularity = true;
					launchStatistics.granularity = true;
					SimulationParameters.granularityType = "TASKONLY";
					launchStatistics.granularityType = "TASKONLY";
					break;
				// case 3:
				// SimulationParameters.granularity = true;
				// launchStatistics.granularity = true;
				// SimulationParameters.granularityType = "TASKONLY";
				// launchStatistics.granularityType = "TASKONLY";
				// break;
				default:
					break;
				}
			}
		} else {
			launchStatistics.granularity = false;
			launchStatistics.granularityType = "OFF";
		}
	}

	private void decideAboutCutPoint() {
		if (SimulationParameters.experienceCutPoint) {
			int twoPossibilities = RandomHelper.nextIntFromTo(0, 1);
			switch (twoPossibilities) {
			case 0:
				SimulationParameters.experienceCutPoint = false;
				launchStatistics.experienceCutPoint = false;
				break;
			case 1:
				SimulationParameters.experienceCutPoint = true;
				launchStatistics.experienceCutPoint = true;
				break;
			default:
				break;
			}
		} else {
			launchStatistics.experienceCutPoint = false;
		}
	}

}
