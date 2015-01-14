package strategies;

import java.util.HashMap;
import java.util.Map;

import strategies.Strategy.TaskChoice;

/***
 * Complex Strategies used in the simulation
 * 
 * Strategy for Agent {strategy for choosing tasks}
 * 
 * @author Oskar Jarczyk
 * @since 1.0
 * @version 2.0.8
 * 
 */
public class ComplexStrategy extends Strategy implements StrategyInterface{
	
	private Map<Strategy.TaskChoice, Double> probability;

	public ComplexStrategy(TaskChoice taskChoice, SkillChoice skillChoice) {
		super(taskChoice, skillChoice);
		probability = new HashMap<Strategy.TaskChoice, Double>();
	}
	
	@Override
	public String toString() {
		return this.probability.keySet() + "," + this.skillChoice.name();
	}

	@Override
	public Strategy copy() {
		return new Strategy(this.taskChoice, this.skillChoice);
	}

	@Override
	public void copyStrategy(Strategy copyFrom) {
		this.taskChoice = copyFrom.taskChoice;
		this.skillChoice = copyFrom.skillChoice;
	}
	
	@Override
	public TaskChoice getTaskChoice() {
		return this.taskChoice;
	}

}
