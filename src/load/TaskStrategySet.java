package load;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import strategies.Strategy;
import collaboration.SimulationParameters;

public enum TaskStrategySet {
	
	INSTANCE;

	private Set<Strategy.TaskChoice> strategies = new HashSet<Strategy.TaskChoice>();
	private Strategy.TaskChoice chosen;

	public void setChosen(Strategy.TaskChoice taskChoice) {
		chosen = taskChoice;
	}

	public Strategy.TaskChoice getChosen() {
		return chosen;
	}
	
	public String getChosenName(){
		return chosen.name();
	}

	public void addStrategy(Strategy.TaskChoice taskChoice) {
		strategies.add(taskChoice);
	}

	public Set<Strategy.TaskChoice> getStrategies() {
		return strategies;
	}

	public void setFunctions(Set<Strategy.TaskChoice> strategies) {
		this.strategies = strategies;
	}

	public int size() {
		return strategies.size();
	}

	@Override
	public String toString() {
		StringBuilder returnString = new StringBuilder();
		Iterator<Strategy.TaskChoice> iterator = strategies.iterator();
		for (Strategy.TaskChoice taskChoice = iterator.next(); iterator.hasNext();) {
			returnString.append(taskChoice.name());
			returnString.append(" ");
		}
		return returnString.toString().trim();
	}

	public boolean isEmpty() {
		return strategies.isEmpty();
	}

}
