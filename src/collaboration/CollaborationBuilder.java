package collaboration;

import github.DataSetProvider;
import github.TaskSkillFrequency;
import github.TaskSkillsPool;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import logger.EndRunLogger;
import logger.PjiitLogger;
import logger.PjiitOutputter;
import logger.SanityLogger;
import logger.ValidationLogger;
import logger.ValidationOutputter;

import org.apache.log4j.LogManager;

import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
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
import strategies.Strategy;
import strategies.StrategyDistribution;
import test.AgentTestUniverse;
import test.Model;
import test.TaskTestUniverse;
import utils.DescribeUniverseBulkLoad;
import utils.LaunchStatistics;
import utils.NamesGenerator;
import EDU.oswego.cs.dl.util.concurrent.CopyOnWriteArrayList;
import argonauts.PersistAdvancement;
import argonauts.PersistJobDone;
import argonauts.PersistRewiring;
import au.com.bytecode.opencsv.CSVWriter;
import constants.Constraints;
import constants.ModelFactory;

/**
 * COIN network emergence simulator, a Repast Simphony 2.2 multi-agent social
 * simulation for modeling task allocation techniques and behavior of
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
 * @version 2.0 "Trenzalore"
 * @category Agent-organized Social Simulations
 * @since 1.0
 * @author Oskar Jarczyk (since 1.0+), Blazej Gruszka (1.3+)
 * @see 1) github markdown 2) "On the effectiveness of emergent task allocation"
 */
public class CollaborationBuilder implements ContextBuilder<Object> {

	private StrategyDistribution strategyDistribution;
	private ModelFactory modelFactory;
	private SkillFactory skillFactory;
	private LaunchStatistics launchStatistics;
	private Schedule schedule = new Schedule();
	private String[] universe = null;

	private TaskPool taskPool = new TaskPool();

	private List<Agent> listAgent;
	private CentralPlanning centralPlanningHq;

	private boolean shutdownInitiated = false;
	private boolean alreadyFlushed = false;

	public CollaborationBuilder() {

		try {
			initializeLoggers();
			RandomHelper.setSeed(SimulationParameters.randomSeed);
			RandomHelper.init();
			clearStaticHeap();

			say("CollaborationBuilder constructor loaded");
			// getting parameters of simulation
			say(Constraints.LOADING_PARAMETERS);

			SimulationParameters.init();
			if (SimulationParameters.multipleAgentSets) {
				universe = DescribeUniverseBulkLoad.init();
			}

			launchStatistics = new LaunchStatistics();
			modelFactory = new ModelFactory(SimulationParameters.model_type);
			say("Starting simulation with model: " + modelFactory.toString());

			if (modelFactory.getFunctionality().isValidation())
				initializeValidationLogger();

			// TODO: implement mixed strategy distribution
			strategyDistribution = new StrategyDistribution();

			// initialize skill pools
			skillFactory = new SkillFactory();
			skillFactory.buildSkillsLibrary();

			say("SkillFactory parsed all skills from CSV file");
		} catch (IOException e) {
			e.printStackTrace();
			say(Constraints.ERROR_INITIALIZING_PJIITLOGGER);
		} catch (Exception exc) {
			say(exc.toString());
			exc.printStackTrace();
			say(Constraints.ERROR_INITIALIZING_PJIITLOGGER_AO_PARAMETERS);
		}

		try {
			DataSetProvider dsp = new DataSetProvider(
					SimulationParameters.dataSetAll);

			AgentSkillsPool.instantiate(dsp.getAgentSkillDataset());
			say("Instatiated AgentSkillsPool");
			TaskSkillsPool.instantiate(dsp.getTaskSkillDataset());
			say("Instatied TaskSkillsPool");

			strategyDistribution
					.setType(SimulationParameters.strategyDistribution);
			strategyDistribution.setSkillChoice(modelFactory,
					SimulationParameters.skillChoiceAlgorithm);
			strategyDistribution.setTaskChoice(modelFactory,
					SimulationParameters.taskChoiceAlgorithm);
			strategyDistribution.setTaskMinMaxChoice(modelFactory,
					SimulationParameters.taskMinMaxChoiceAlgorithm);
		} catch (Exception exc) {
			exc.printStackTrace();
			say(Constraints.UNKNOWN_EXCEPTION);
		}
	}

	@Override
	public Context build(Context<Object> context) {
		context.setId("emergent-task-allocation");

		initializeTasks(context);
		initializeAgents(context);

		say("Task choice algorithm is "
				+ SimulationParameters.taskChoiceAlgorithm);
		sanity("Number of teams created " + context.getObjects(Task.class).size());
		sanity("Number of agents created "
				+ context.getObjects(Agent.class).size());
		sanity("Algorithm tested: " + SimulationParameters.taskChoiceAlgorithm);

		try {
			outputAgentSkillMatrix();
		} catch (IOException e) {
			say(Constraints.IO_EXCEPTION);
			e.printStackTrace();
		} catch (NullPointerException nexc) {
			say(Constraints.UNKNOWN_EXCEPTION);
			nexc.printStackTrace();
		}

		if (SimulationParameters.forceStop)
			RunEnvironment.getInstance().endAt(SimulationParameters.numSteps);

		buildCentralPlanner();
		buildExperienceReassessment();
		buildAgentsWithdrawns();
		decideAboutGranularity();
		decideAboutCutPoint();

		PersistAdvancement.calculateAll(taskPool);
		// TODO: later add req that if at least 1 agent uses Preferential...

		List<ISchedulableAction> actions = schedule.schedule(this);
		say(actions.toString());

		return context;
	}

	private void initializeLoggers() throws IOException {
		// System.setErr(new PrintStream(new
		// FileOutputStream("error_console.log")));
		// actually this little bastard is not working, find out why ?

		PjiitLogger.init();
		say(Constraints.LOGGER_INITIALIZED);
		SanityLogger.init();
		sanity(Constraints.LOGGER_INITIALIZED);
		EndRunLogger.init();
		EndRunLogger.buildHeaders(buildFinalMessageHeader());
	}

	private void initializeAgents(Context context) {
		Model model = modelFactory.getFunctionality();
		if (model.isNormal() && model.isValidation()) {
			throw new UnsupportedOperationException();
		} else if (model.isNormal()) {
			addAgents(context);
		} else if (model.isSingleValidation()) {
			listAgent = new ArrayList<Agent>();
			AgentTestUniverse.init();
			initializeValidationAgents(context);
		} else if (model.isValidation()) {
			listAgent = new ArrayList<Agent>();
			AgentTestUniverse.init();
			initializeValidationAgents(context);
		}
	}

	private void initializeValidationAgents(Context<Object> context) {
		for (Agent agent : AgentTestUniverse.DATASET) {
			say("Adding validation agent to pool..");
			Strategy strategy = new Strategy(
					strategyDistribution.getTaskStrategy(agent),
					strategyDistribution.getTaskMaxMinStrategy(agent),
					strategyDistribution.getSkillStrategy(agent));

			agent.setStrategy(strategy);
			listAgent.add(agent);
			say(agent.toString() + " added to pool.");
			// Required adding agent to context
			// this.add(agent);
			context.add(agent);
		}
	}

	protected void initializeTasks(Context<Object> context) {
		Model model = modelFactory.getFunctionality();
		if (model.isNormal() && model.isValidation()) {
			throw new UnsupportedOperationException();
		} else if (model.isNormal()) {
			initializeTasksNormally(context);
		} else if (model.isSingleValidation()) {
			TaskTestUniverse.init();
			initalizeValidationTasks(context);
		} else if (model.isValidation()) {
			TaskTestUniverse.init();
			initalizeValidationTasks(context);
		} else {
			assert false; // should never happen
		}
	}

	private void initalizeValidationTasks(Context<Object> context) {
		for (Task task : TaskTestUniverse.DATASET) {
			say("Adding validation task to pool..");
			taskPool.addTask(task.getName(), task);
			context.add(task);
			//agentPool.add(task);
		}
	}

	private void initializeTasksNormally(Context<Object> context) {
		Integer howMany = SimulationParameters.multipleAgentSets ? Integer
				.parseInt(universe[1]) : SimulationParameters.taskCount;
		for (int i = 0; i < howMany; i++) {
			Task task = new Task();
			say("Creating task..");
			taskPool.addTask(task.getName(), task);
			say("Initializing task..");
			task.initialize(howMany);
			context.add(task);
			//agentPool.add(task);
		}

		launchStatistics.taskCount = taskPool.getCount();
	}

	private void initializeValidationLogger() {
		ValidationLogger.init();
		say(Constraints.VALIDATION_LOGGER_INITIALIZED);
		validation("---------------------------------------------------------");
	}

	private void outputAgentSkillMatrix() throws IOException {
		CSVWriter writer = new CSVWriter(new FileWriter("input_a1.csv"), ',',
				CSVWriter.NO_QUOTE_CHARACTER);
		for (Agent agent : listAgent) {
			for (AgentInternals __agentInternal : agent.getAgentInternals()) {
				ArrayList<String> entries = new ArrayList<String>();
				entries.add(agent.getNick());
				entries.add(__agentInternal.getExperience().getValue() + "");
				entries.add(__agentInternal.getSkill().getName());
				String[] stockArr = new String[entries.size()];
				stockArr = entries.toArray(stockArr);
				writer.writeNext(stockArr);
			}
		}
		writer.close();
	}

	private void addAgents(Context<Object> context) {
		Integer agentCnt = SimulationParameters.multipleAgentSets ? Integer
				.parseInt(universe[0]) : SimulationParameters.agentCount;

		listAgent = NamesGenerator.getnames(agentCnt);
		for (int i = 0; i < agentCnt; i++) {
			Agent agent = listAgent.get(i);

			Strategy strategy = new Strategy(
					strategyDistribution.getTaskStrategy(agent),
					strategyDistribution.getTaskMaxMinStrategy(agent),
					strategyDistribution.getSkillStrategy(agent));

			agent.setStrategy(strategy);
			say(agent.toString());
			say("in add aggent i: " + i);
			// Required adding agent to context
			// this.add(agent);

			for (AgentInternals ai : agent.getAgentInternals()) {
				assert ai.getExperience().getValue() > 0;
				say("For a=" + agent.toString() + " delta is "
						+ ai.getExperience().getDelta());
				say("For a=" + agent.toString() + " value is "
						+ ai.getExperience().getValue());
				say("For a=" + agent.toString() + " top is "
						+ ai.getExperience().getTop());
			}

			context.add(agent);
		}

//		launchStatistics.agentCount = agentPool.size()
//				- launchStatistics.taskCount;
		
		launchStatistics.agentCount = agentCnt;
	}

	public void clearStaticHeap() {
		say("Clearing static data from previous simulation");
		PersistJobDone.clear();
		PersistAdvancement.clear();
		PersistRewiring.clear();
		TaskSkillsPool.clear();
		SkillFactory.skills.clear();
		NamesGenerator.clear();
		TaskPool.clearTasks();
		AgentSkillsPool.clear();
		Agent.totalAgents = 0;
		TaskSkillsPool.static_frequency_counter = 0;
		TaskSkillFrequency.clear();
		AgentSkillsFrequency.clear();
	}

	@ScheduledMethod(start = 1, interval = 1, priority = ScheduleParameters.FIRST_PRIORITY)
	public void finishSimulation() {
		say("finishSimulation() check launched");
		EnvironmentEquilibrium.setActivity(false);
		if (taskPool.getCount() < 1) {
			say("count of taskPool is < 1, finishing simulation");
			finalMessage(buildFinalMessage());
			shutdownInitiated = true;
			RunEnvironment.getInstance().endRun();
			cleanAfter();
		}
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
				+ strategyDistribution.getTaskMinMaxChoice() + ","
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

	@ScheduledMethod(start = 1, interval = 1, priority = ScheduleParameters.LAST_PRIORITY)
	public void checkForActivity() {
		say("checkForActivity() check launched");
		if (EnvironmentEquilibrium.getActivity() == false) {
			say("EnvironmentEquilibrium.getActivity() returns false!");
			finalMessage(buildFinalMessage());
			shutdownInitiated = true;
			RunEnvironment.getInstance().endRun();
			cleanAfter();
		}
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

	public void centralPlanning() {
		say("CentralPlanning scheduled method launched, listAgent.size(): "
				+ listAgent.size() + " taskPool.size(): " + taskPool.getCount());
		// Zeroing agents orders
		centralPlanningHq.zeroAgentsOrders(listAgent);
		centralPlanningHq.centralPlanningCalc(listAgent, taskPool);
	}

	/**
	 * Here I need to schedule method manually because i don't know if central
	 * planer is enabled for the simulation whether not.
	 */
	public void buildCentralPlanner() {
		say("buildCentralPlanner lunched !");
		if (strategyDistribution.getTaskChoice().equals("central")) {
			centralPlanningHq = new CentralPlanning();

			say("Central planner initiating.....");
			ISchedule schedule = RunEnvironment.getInstance()
					.getCurrentSchedule();
			ScheduleParameters params = ScheduleParameters.createRepeating(1,
					1, ScheduleParameters.FIRST_PRIORITY);
			schedule.schedule(params, this, "centralPlanning");
			say("Central planner initiated and awaiting for call !");
		}
	}

	public synchronized void experienceReassess() {
		Context<Object> context = getCurrentContext();
		try {
			IndexedIterable<Object> agentObjects = context
					.getObjects(Agent.class);
			for (Object agent : agentObjects) {
				String type = agent.getClass().getName();
				if (type.equals("collaboration.Agent")) {
					say("Checking if I may have to decrease exp of "
							+ (((Agent) agent).getNick()));
					// use persist job done
					Map<Integer, List<Skill>> c = PersistJobDone
							.getSkillsWorkedOn(((Agent) agent).getNick());
					if ((c == null) || (c.size() < 1)) { // agent didn't work on
															// anything yet !
						continue; // move on to next agent in pool
					}
					List<Skill> __s = c.get(Integer
							.valueOf((int) RunEnvironment.getInstance()
									.getCurrentSchedule().getTickCount()));
					List<Skill> s = __s == null ? new ArrayList<Skill>() : __s;

					Collection<AgentInternals> aic = ((Agent) agent)
							.getAgentInternals();
					CopyOnWriteArrayList aicconcurrent = new CopyOnWriteArrayList(
							aic);
					for (Object ai : aicconcurrent) {
						if (s.contains(((AgentInternals) ai).getSkill())) {
							// was working on this, don't decay
						} else {
							// decay this experience by beta < 1
							if (SimulationParameters.allowSkillDeath) {
								boolean result = ((AgentInternals) ai)
										.decayExperienceWithDeath();
								if (result) {
									((Agent) agent).removeSkill(
											((AgentInternals) ai).getSkill(),
											false);
								}
							} else {
								double value = ((AgentInternals) ai)
										.decayExperience();
								if (value == 0) {
									say("Experience of agent "
											+ (((Agent) agent).getNick())
											+ " wasn't decreased because it's already low");
								} else
									say("Experience of agent "
											+ (((Agent) agent).getNick())
											+ " decreased and is now " + value);
							}
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
			// thats why randomize to use both
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

	public synchronized void agentsWithdrawns() {
		Context<Object> context = getCurrentContext();
		try {
			IndexedIterable<Object> agentObjects = context
					.getObjects(Agent.class);
			CopyOnWriteArrayList acconcurrent = new CopyOnWriteArrayList();
			for (Object object : agentObjects) {
				acconcurrent.add(object);
			}
			for (Object agent : acconcurrent) {
				if (agent.getClass().getName().equals("collaboration.Agent")) {
					say("Checking if I may have to force "
							+ (((Agent) agent).getNick()) + " to leave");
					Collection<AgentInternals> aic = ((Agent) agent)
							.getAgentInternals();

					CopyOnWriteArrayList aicconcurrent = new CopyOnWriteArrayList(
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
		return ContextUtils.getContext(this);
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
			// thats why randomize to use both
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
