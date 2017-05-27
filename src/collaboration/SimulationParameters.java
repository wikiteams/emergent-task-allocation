package collaboration;

import load.AgentCountConverter;
import load.ExpDecayOptionConverter;
import load.FunctionSetConverter;
import load.GenerationLengthConverter;
import load.GranularityOptionConverter;
import load.ParametrizedSigmoidOptionConverter;
import load.SigmoidParameterConverter;
import load.SkillStrategySetConverter;
import load.TaskCountConverter;
import load.TaskStrategySetConverter;
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

	public static int evolutionEnabled;
	public static int planNumber;

	public static int randomSeed;
	public static int sweepRuns;

	public static void init() {
		Parameters param = RunEnvironment.getInstance().getParameters();

		AgentCountConverter agentCountConverter = new AgentCountConverter();
		GenerationLengthConverter generationLengthConverter = new GenerationLengthConverter();
		ExpDecayOptionConverter expDecayOptionConverter = new ExpDecayOptionConverter();
		FunctionSetConverter functionSetConverter = new FunctionSetConverter();
		
		GranularityOptionConverter granularityOptionConverter = new GranularityOptionConverter();
		SkillStrategySetConverter skillStrategySetConverter = new SkillStrategySetConverter();
		TaskStrategySetConverter taskStrategySetConverter = new TaskStrategySetConverter();
		
		ParametrizedSigmoidOptionConverter parametrizedSigmoidOptionConverter = new ParametrizedSigmoidOptionConverter();
		SigmoidParameterConverter sigmoidParameterConverter = new SigmoidParameterConverter();
		
		TaskCountConverter taskCountConverter = new TaskCountConverter();

		planNumber = (Integer) param.getValue("planNumber");

		generationLengthConverter.fromString((String) param
				.getValue("generationLength"));
		agentCountConverter.fromString((String) param.getValue("agentCount"));
		expDecayOptionConverter.fromString((String) param
				.getValue("experienceDecay"));
		functionSetConverter.fromString((String) param
				.getValue("utilityFunction"));
		
		granularityOptionConverter.fromString((String) param
				.getValue("granularity"));
		skillStrategySetConverter.fromString(((String) param
				.getValue("skillChoiceAlgorithm")).toUpperCase());
		taskStrategySetConverter.fromString(((String) param
				.getValue("taskChoiceAlgorithm")).toUpperCase());
		
		parametrizedSigmoidOptionConverter.fromString((String) param
				.getValue("parametrizedSigmoid"));
		sigmoidParameterConverter.fromString((String) param
				.getValue("sigmoidParameter"));
		
		taskCountConverter.fromString(((String) param
				.getValue("numTasks")).toUpperCase());

		evolutionEnabled = (Integer) param.getValue("evolutionEnabled");

		randomSeed = (Integer) param.getValue("randomSeed");
		sweepRuns = (Integer) param.getValue("sweepRuns");
	}
}
