package strategies;

import java.util.LinkedList;

import com.google.common.collect.Lists;

public class StrategySet {

	private static LinkedList<StrategyFrequency> strategies;
	
	public String describe(){
		StringBuilder str = new StringBuilder();
		for(StrategyFrequency s : Lists.newArrayList(strategies.iterator())){
			str.append(s.getDistribution());
			str.append(' ');
			str.append(s.getTaskChoice().name());
			str.append(' ');
		}
		return str.toString();
	}

	private StrategySet() {
		strategies = new LinkedList<StrategyFrequency>();
	}

	private void addFrequency(Double occurency, Strategy.TaskChoice taskChoice) {
		strategies.add(new StrategyFrequency(occurency, taskChoice));
	}

	public static StrategySet get(Integer planNumber) {
		StrategySet resultSet = null;
		switch (planNumber) {
		case 1:
			resultSet = new StrategySet();
			resultSet.addFrequency(0.1, Strategy.TaskChoice.HETEROPHYLY);
			resultSet.addFrequency(0.9 + 0.10001, Strategy.TaskChoice.HOMOPHYLY);
			// sum of frequencies must always be 1.0
			break;
		case 2:
			resultSet = new StrategySet();
			resultSet.addFrequency(0.1, Strategy.TaskChoice.PREFERENTIAL);
			resultSet.addFrequency(0.9 + 0.10001, Strategy.TaskChoice.HOMOPHYLY);
			// sum of frequencies must always be 1.0
			break;
		case 3:
			resultSet = new StrategySet();
			resultSet.addFrequency(0.1, Strategy.TaskChoice.HOMOPHYLY);
			resultSet.addFrequency(0.9 + 0.10001, Strategy.TaskChoice.HETEROPHYLY);
			// sum of frequencies must always be 1.0
			break;
		case 4:
			resultSet = new StrategySet();
			resultSet.addFrequency(0.1, Strategy.TaskChoice.PREFERENTIAL);
			resultSet.addFrequency(0.9 + 0.10001, Strategy.TaskChoice.HETEROPHYLY);
			// sum of frequencies must always be 1.0
			break;
		case 5:
			resultSet = new StrategySet();
			resultSet.addFrequency(0.1, Strategy.TaskChoice.HOMOPHYLY);
			resultSet.addFrequency(0.9 + 0.10001, Strategy.TaskChoice.PREFERENTIAL);
			// sum of frequencies must always be 1.0
			break;
		case 6:
			resultSet = new StrategySet();
			resultSet.addFrequency(0.1, Strategy.TaskChoice.HETEROPHYLY);
			resultSet.addFrequency(0.9 + 0.10001, Strategy.TaskChoice.PREFERENTIAL);
			// sum of frequencies must always be 1.0
			break;
		case 7:
			resultSet = new StrategySet();
			resultSet.addFrequency(0.333, Strategy.TaskChoice.HETEROPHYLY);
			resultSet.addFrequency(0.333 + 0.333, Strategy.TaskChoice.HOMOPHYLY);
			resultSet.addFrequency(0.334 + 0.333 + 0.333, Strategy.TaskChoice.PREFERENTIAL);
			// sum of frequencies must always be 1.0
			break;
		}
		return resultSet;
	}

	public static LinkedList<StrategyFrequency> getStrategies() {
		return strategies;
	}

	public static void setStrategies(LinkedList<StrategyFrequency> strategies) {
		StrategySet.strategies = strategies;
	}

}

class StrategyFrequency {

	private Double distribution;
	private Strategy.TaskChoice taskChoice;

	protected StrategyFrequency(Double distribution,
			Strategy.TaskChoice taskChoice) {
		this.distribution = distribution;
		this.taskChoice = taskChoice;
	}

	public Strategy.TaskChoice getTaskChoice() {
		return taskChoice;
	}

	public void setTaskChoice(Strategy.TaskChoice taskChoice) {
		this.taskChoice = taskChoice;
	}

	public Double getDistribution() {
		return distribution;
	}

	public void setDistribution(Double distribution) {
		this.distribution = distribution;
	}

}
