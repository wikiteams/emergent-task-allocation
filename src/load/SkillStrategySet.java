package load;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import strategies.Strategy;

public enum SkillStrategySet {
	
	INSTANCE;

	private Set<Strategy.SkillChoice> strategies = new HashSet<Strategy.SkillChoice>();
	private Strategy.SkillChoice chosen;

	public void setChosen(Strategy.SkillChoice skillChoice) {
		chosen = skillChoice;
	}

	public Strategy.SkillChoice getChosen() {
		return chosen;
	}
	
	public String getChosenName(){
		return chosen.name();
	}

	public void addStrategy(Strategy.SkillChoice skillChoice) {
		strategies.add(skillChoice);
	}

	public Set<Strategy.SkillChoice> getStrategies() {
		return strategies;
	}

	public void setStrategies(Set<Strategy.SkillChoice> strategies) {
		this.strategies = strategies;
	}

	public int size() {
		return strategies.size();
	}

	@Override
	public String toString() {
		StringBuilder returnString = new StringBuilder();
		Iterator<Strategy.SkillChoice> iterator = strategies.iterator();
		for (Strategy.SkillChoice skillChoice = iterator.next(); iterator.hasNext();) {
			returnString.append(skillChoice.name());
			returnString.append(" ");
		}
		return returnString.toString().trim();
	}

	public boolean isEmpty() {
		return strategies.isEmpty();
	}

}
