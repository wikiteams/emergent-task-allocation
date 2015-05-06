package collaboration;

import load.AgentCount;
import load.AgentCountConverter;
import load.ExpDecayOption;
import load.ExpDecayOptionConverter;
import load.FunctionSet;
import load.FunctionSetConverter;
import load.GenerationLength;
import load.GenerationLengthConverter;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;

/**
 * Basically stores parameters from repast file to a holder Simulation
 * Parameters holds static all execution parameters in memory for more
 * convenient access to them
 * 
 * @author Oskar Jarczyk
 * @since 1.0
 * @version 2.0.11
 */
public class SimulationParameters {
	
	public static double mutateChances;
	public static boolean allowSkillDeath;

	public static int evolutionEnabled;
	public static int planNumber;
	public static int equilibriumDetectionSensitivity;
	
	public static GenerationLength generationLength;
	public static ExpDecayOption expDecayOption;
	public static AgentCount agentCount;
	private static FunctionSet functionSet;
	
	public static int taskCount;
	public static int numSteps;
	public static boolean allwaysChooseTask;

	public static String taskChoiceAlgorithm;
	public static String skillChoiceAlgorithm;

	public static boolean granularity;
	public static int granularityObstinacy;
	public static String granularityType;
	public static boolean experienceCutPoint;
	
	public static int randomSeed;

	public static void init() {
		Parameters param = RunEnvironment.getInstance().getParameters();
		
		AgentCountConverter agentCountConverter = new AgentCountConverter();
		GenerationLengthConverter generationLengthConverter = new GenerationLengthConverter();
		ExpDecayOptionConverter expDecayOptionConverter = new ExpDecayOptionConverter();
		FunctionSetConverter functionSetConverter = new FunctionSetConverter();
		
		planNumber = (Integer) param.getValue("planNumber");
		generationLength = (GenerationLength) generationLengthConverter.fromString((String) param
				.getValue("generationLength"));
		agentCount = (AgentCount) agentCountConverter.fromString((String) param
				.getValue("agentCount"));
		expDecayOption = (ExpDecayOption) expDecayOptionConverter.fromString((String) param
				.getValue("experienceDecay"));
		functionSet = (FunctionSet) functionSetConverter.fromString((String) param
				.getValue("utilityFunction"));
		
		equilibriumDetectionSensitivity = (Integer) param.getValue("equilibriumDetectionSensitivity");
		mutateChances = ( (Integer) param.getValue("mutateChances") ) * 0.01;
		
		taskCount = (Integer) param.getValue("numTasks");
		numSteps = (Integer) param.getValue("numSteps");
		allwaysChooseTask = (Boolean) param.getValue("allwaysChooseTask");

		taskChoiceAlgorithm = (String) param.getValue("taskChoiceAlgorithm");
		skillChoiceAlgorithm = (String) param.getValue("skillChoiceAlgorithm");

		evolutionEnabled = (Integer) param.getValue("evolutionEnabled");

		randomSeed = (Integer) param.getValue("randomSeed");

		allowSkillDeath = (Boolean) param.getValue("allowSkillDeath");
		experienceCutPoint = (Boolean) param.getValue("experienceCutPoint");
		
		granularity = (Boolean) param.getValue("granularity");
		granularityObstinacy = (Integer) param.getValue("granularityObstinacy");
		granularityType = (String) param.getValue("granularityType");

	}
}
