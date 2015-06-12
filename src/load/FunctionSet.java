package load;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import collaboration.Utility.UtilityType;

public enum FunctionSet {

	INSTANCE;

	private Set<UtilityType> functions = new HashSet<UtilityType>();
	private UtilityType chosen;
	
	public boolean isAgentOrientedUtility;
	public boolean isTaskOrientedUtility;

	public void setChosen(UtilityType utilityType) {
		chosen = utilityType;
		EnumSet<UtilityType> agentOriented = EnumSet.of(
				UtilityType.LearningSkills, UtilityType.LearningSkills,
				UtilityType.RightLearningSkills);
		isAgentOrientedUtility = agentOriented.contains(chosen) ? true : false;
		isTaskOrientedUtility = agentOriented.contains(chosen) ? false : true;
	}

	public UtilityType getChosen() {
		return chosen;
	}

	public void addFunction(UtilityType utilityType) {
		functions.add(utilityType);
	}

	public Set<UtilityType> getFunctions() {
		return functions;
	}

	public void setFunctions(Set<UtilityType> functions) {
		this.functions = functions;
	}

	public int size() {
		return functions.size();
	}

	@Override
	public String toString() {
		StringBuilder returnString = new StringBuilder();
		Iterator<UtilityType> iterator = functions.iterator();
		for (UtilityType utilityType = iterator.next(); iterator.hasNext();) {
			returnString.append(utilityType.name());
			returnString.append(" ");
		}
		return returnString.toString().trim();
	}

	public boolean isEmpty() {
		return functions.isEmpty();
	}
}
