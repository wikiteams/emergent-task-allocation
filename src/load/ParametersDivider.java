package load;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import utils.Combinatorics;

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
		return Combinatorics.permutations(parameters);
	}
	
	public static Integer countSettings(){
		return getSettings().size();
	}

}
