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
import load.ParametersDivider;
import load.ParametrizedSigmoidOption;
import load.SigmoidParameter;
import load.SkillStrategySet;
import load.TaskCount;
import load.TaskStrategySet;
import logger.EndRunLogger;

import org.ini4j.InvalidFileFormatException;

import repast.simphony.context.Context;
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
 * @version 3.0
 * @category Agent-organized social simulations
 * @since 1.0
 * @author Oskar Jarczyk
 * @see 1) GitHub markdown 2) "On The Effectiveness of Emergent Task Allocation"
 */
public class CollaborationBuilder implements ContextBuilder<Object> {

	public static Context<Task> tasks;
	public static Context<Agent> agents;
	public static Context<Skill> skills;

	private StrategyDistribution strategyDistribution;
	private Schedule schedule = new Schedule();

	private GameController gameController;
	private RunInfo runInfo;
	private CentralPlanning centralPlanner;

	public CollaborationBuilder() {
		try {
			initializeLoggers();
			System.out.println("[Loggers] initialized...");
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println(Constraints.ERROR_INITIALIZING_PJIITLOGGER);
		} catch (Exception exc) {
			System.err.println(exc.toString());
			exc.printStackTrace();
			System.err.println(Constraints.ERROR_INITIALIZING_PJIITLOGGER_AO_PARAMETERS);
		} finally {
			System.out.println("[CollaborationBuilder constructor] finished execution");
			// this is where Repast waits for scenario lunch
			// (context builds up and build() method executes)
		}
	}

	private void prepareDataControllers(Context<Object> context) throws InvalidFileFormatException,
			IOException {
		System.out.println(Constraints.LOADING_PARAMETERS);
		SimulationParameters.init();
		// getting parameters of a simulation from current scenario

		RandomHelper.setSeed(SimulationParameters.randomSeed);
		RandomHelper.init();
		System.out.println("[RandomHelper] initialized...");

		/***
		 * StrategyDistribution holds information on currently tested Task
		 * assignment strategy and Skill choice strategy Single distribution
		 * means evolution disabled, while multiple distribution enables
		 * evolutionary model
		 */
		strategyDistribution = new StrategyDistribution();
		
		skills = new Skills();
		context.addSubContext(skills);
	}

	private void prepareWorkLoadData() {

		/***
		 * This is new dataset parsed from our GitHub MongoDB database and
		 * specially created for the sake of evolutionary model
		 */
		AgentModeling.instantiate();
		System.out.println("[Sqlite engine] and resultset"
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
		System.out.println("[Static heap] cleared..");

		try {
			// prepare e.g. skill factory
			prepareDataControllers(context);
			// divide set of parameters into subscenarios
			ParametersDivider.findMatch(runNumber, SimulationParameters.sweepRuns);
			// prepare sqlite and other factories
			prepareWorkLoadData();
		} catch (InvalidFileFormatException e) {
			System.err.println(e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println(e.toString());
			e.printStackTrace();
		}

		tasks = new Tasks(TaskCount.INSTANCE.getChosen());
		context.addSubContext(tasks);
		agents = new Agents(strategyDistribution,
				AgentCount.INSTANCE.getChosen());
		context.addSubContext(agents);
		
		prepareGameController(context);

		assert context.getObjects(GameController.class).size() > 0;
		System.out.println("[Game Controller] initialized and added to context.");

		System.out.println("Task choice [Strategy] is "
						+ (strategyDistribution.isDistributionLoaded() ? strategyDistribution
								.getStrategySet().describe()
								: TaskStrategySet.INSTANCE.getChosenName()));
		System.out.println("Number of [Tasks] created " + getTasks().size());
		System.out.println("Number of [Agents] created " + getAgents().size());
		System.out.println("[Algorithm] tested: "
				+ (strategyDistribution.isDistributionLoaded() ? "Distributed"
						: TaskStrategySet.INSTANCE.getChosenName()));

		buildCentralPlanner();
		buildContinousTaskFlow();
		buildExperienceReassessment();

		List<ISchedulableAction> actions = schedule.schedule(this);
		System.out.println(actions.toString());

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
	
	private IndexedIterable<Skill> getSkills() {
		Context<Skill> context = skills;
		return context.getObjects(Skill.class);
	}

	private void initializeLoggers() throws IOException {
		EndRunLogger.init();
		EndRunLogger.buildHeaders(buildFinalMessageHeader());
	}

	@ScheduledMethod(start = 2, interval = 1, priority = ScheduleParameters.FIRST_PRIORITY)
	/***
	 * Because of a continuous work in an evolutionary model, we don't finish
	 * simulation without a good reason. This method will be enabled but in
	 * second part of the game - calculating efficiency of a static set of
	 * strategies
	 */
	public void finishSimulation() {
		//System.out.println("[finishSimulation() check launched] Checking if simulation can be ended...");
		EnvironmentEquilibrium.setActivity(false);
		if (gameController.isEvolutionary()) {
			if (EquilibriumDetector.evaluate(gameController)) {
				//System.out.println("[Stable set of Strategies] detected, finishing simulation");
				EndRunLogger.finalMessage((buildFinalMessage()));
				RunEnvironment.getInstance().endRun();
				// cleanAfter();
			}
		} else if (((Tasks) tasks).getCount() < 1) {
			//System.out.println("Count of [Task Pool] is < 1, finishing simulation");
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
		//System.out.println("[checkForActivity() check launched] Checking if there was any work at all in current Tick");
		if (EnvironmentEquilibrium.getActivity() == false) {
			//System.out.println("EnvironmentEquilibrium.getActivity() returns false!");
			EndRunLogger.finalMessage((buildFinalMessage()));
			RunEnvironment.getInstance().endRun();
			// cleanAfter();
		}
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
				+ GranularityOption.INSTANCE.getChosen()
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
				+ gameController.getCurrentGeneration()
				+ ","
				+ GenerationLength.INSTANCE.getChosen()
				+ ","
				+ gameController
						.countHomophilyDistribution(getCurrentContext())
				+ ","
				+ gameController
						.countHeterophilyDistribution(getCurrentContext())
				+ ","
				+ gameController
						.countPreferentialDistribution(getCurrentContext())
				+ ","
				+ ParametrizedSigmoidOption.INSTANCE.getChosen()
				+ ","
				+ SigmoidParameter.INSTANCE.getChosen();
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
				+ "Preferential_Count" + "Parametrized_Sigmoid" + "Sigmoid_Parameter";
	}

	/**
	 * This is the method scheduled (if at least 1 agent uses central planner)
	 * to execute every tick to make for giving orders, hence zeroing the orders
	 * first (clearing previous orders) and than making the math
	 */
	public void centralPlanning() {
		/*System.out.println("CentralPlanning scheduled method launched, listAgent.size(): "
						+ getAgents().size()
						+ " taskPool.size(): "
						+ ((Tasks) tasks).getCount());*/
		/*System.out.println("Zeroing agents' orders");*/
		centralPlanner.zeroAgentsOrders(getAgents());
		centralPlanner.centralPlanningCalc(getAgents(), (Tasks) tasks);
	}

	/**
	 * Here I need to schedule method manually because. In first version of
	 * simulator the Central assignment strategy was non-evolutionary but now in
	 * hybrid model it can take work for a subset of Agents as well.
	 */
	public void buildCentralPlanner() {
		/*System.out.println("Method buildCentralPlanner lunched."
				+ "Checking now if central planer is needed at all.");*/
		if (strategyDistribution.isCentralPlannerEnabled()) {
			/*System.out.println("Creating a central planner instance.");*/
			centralPlanner = CentralPlanning.getSingletonInstance();
			/*System.out.println("Central planner is initiating schedule.");*/
			ISchedule schedule = RunEnvironment.getInstance()
					.getCurrentSchedule();
			ScheduleParameters params = ScheduleParameters.createRepeating(1,
					1, ScheduleParameters.FIRST_PRIORITY);
			schedule.schedule(params, this, "centralPlanning");
			System.out.println("Central planner initiated and awaiting for call.");
		}
	}

	public void buildContinousTaskFlow() {
		if (gameController.isEvolutionary()) {
			ISchedule schedule = RunEnvironment.getInstance()
					.getCurrentSchedule();
			ScheduleParameters params = ScheduleParameters.createRepeating(1,
					1, ScheduleParameters.LAST_PRIORITY);
			schedule.schedule(params, this, "provideSimulatorWithWork");
			System.out.println("[Continous Task Flow] initiated");
		} else {
			System.out.println("Task number is static, task flow builder skipped");
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
			System.out.println("Adding more " + difference
					+ " [Tasks] to simulator");
			try {
				List<Task> newTasks = MyDatabaseConnector.get(difference);
				for (Task newTask : newTasks) {
					System.out.println("[Continous Task Flow] adding a new task to context!");
					tasks.add(newTask);
				}
			} catch (SQLException e) {
				System.err.println("Error during providing simulator with a new [Task(s)]");
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
				System.out.println("Checking if I may have to [decrease exp] of "
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
						// decay this experience
							ai.decayExperience();
					}
				}
			}
		} catch (Exception exc) {
			System.err.println(exc.toString());
			System.err.println(exc.getMessage());
			exc.printStackTrace();
		} finally {
			System.out.println("Regular method run for [expDecay] finished for this step.");
		}
	}

	/**
	 * Here I need to schedule method manually because I don't know if expDecay
	 * is enabled for the simulation whether not.
	 */
	public void buildExperienceReassessment() {
		System.out.println("buildExperienceReassessment() lunched !");
		if (ExpDecayOption.INSTANCE.getChosen()) {
			System.out.println("[Exp decay] is enabled for this run");
			ISchedule schedule = RunEnvironment.getInstance()
					.getCurrentSchedule();
			ScheduleParameters params = ScheduleParameters.createRepeating(1,
					1, ScheduleParameters.LAST_PRIORITY);
			schedule.schedule(params, this, "experienceReassess");
			System.out.println("Experience decay initiated and awaiting for call !");
		} else {
			System.out.println("[Exp decay] is disabled for this run");
		}
	}

	private Context<Object> getCurrentContext() {
		return ContextUtils.getContext(this);
	}

}
