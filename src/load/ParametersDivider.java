package load;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import strategies.Strategy;
import utils.Combinatorics;
import collaboration.Utility.UtilityType;

import com.google.common.collect.Iterables;

public class ParametersDivider {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static <T> Collection<List<T>> getSettings() {
		List parameters = new ArrayList();
		if (!AgentCount.INSTANCE.isEmpty())
			parameters.add(AgentCount.INSTANCE.getCounts());
		if (!GenerationLength.INSTANCE.isEmpty())
			parameters.add(GenerationLength.INSTANCE.getLengths());
		if (!ExpDecayOption.INSTANCE.isEmpty())
			parameters.add(ExpDecayOption.INSTANCE.getOptions());
		if (!FunctionSet.INSTANCE.isEmpty())
			parameters.add(FunctionSet.INSTANCE.getFunctions());
		if (!GranularityOption.INSTANCE.isEmpty())
			parameters.add(GranularityOption.INSTANCE.getOptions());
		if (!SkillStrategySet.INSTANCE.isEmpty())
			parameters.add(SkillStrategySet.INSTANCE.getStrategies());
		if (!TaskStrategySet.INSTANCE.isEmpty())
			parameters.add(TaskStrategySet.INSTANCE.getStrategies());
		if (!TaskCount.INSTANCE.isEmpty())
			parameters.add(TaskCount.INSTANCE.getCounts());
		
		if (!ParametrizedSigmoidOption.INSTANCE.isEmpty())
			parameters.add(ParametrizedSigmoidOption.INSTANCE.getOptions());
		if (!SigmoidParameter.INSTANCE.isEmpty())
			parameters.add(SigmoidParameter.INSTANCE.getParameters());
		
		return Combinatorics.permutations(parameters);
	}
	
	public static Integer countSettings(){
		return getSettings().size();
	}
	
	public static void findMatch(int currentRun, int allRunCount){
		double step = Math.ceil(allRunCount / countSettings());
		for(int i = 0 ; i < countSettings() ; i++){
			if ((currentRun > i * step) && (currentRun <= (i+1)*step)){
				List<Object> optionsSet = Iterables.get(getSettings(), i);
				AgentCount.INSTANCE.setChosen((Integer) optionsSet.get(0));
				GenerationLength.INSTANCE.setChosen((Integer) optionsSet.get(1));
				ExpDecayOption.INSTANCE.setChosen((Boolean) optionsSet.get(2));
				FunctionSet.INSTANCE.setChosen((UtilityType) optionsSet.get(3));
				GranularityOption.INSTANCE.setChosen((Boolean) optionsSet.get(4));
				SkillStrategySet.INSTANCE.setChosen((Strategy.SkillChoice) optionsSet.get(5));
				TaskStrategySet.INSTANCE.setChosen((Strategy.TaskChoice) optionsSet.get(6));
				TaskCount.INSTANCE.setChosen((Integer) optionsSet.get(7));
				ParametrizedSigmoidOption.INSTANCE.setChosen((Boolean) optionsSet.get(8));
				SigmoidParameter.INSTANCE.setChosen((Integer) optionsSet.get(9));
				break;
			}
		}
	}

}
