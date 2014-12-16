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
			resultSet.addFrequency(0.2, Strategy.TaskChoice.HETEROPHYLY);
			resultSet.addFrequency(0.8, Strategy.TaskChoice.HOMOPHYLY);
			break;
		case 2:
			resultSet = new StrategySet();
			resultSet.addFrequency(0.2, Strategy.TaskChoice.PREFERENTIAL);
			resultSet.addFrequency(0.8, Strategy.TaskChoice.HOMOPHYLY);
			break;
		case 3:
			resultSet = new StrategySet();
			resultSet.addFrequency(0.2, Strategy.TaskChoice.HOMOPHYLY);
			resultSet.addFrequency(0.8, Strategy.TaskChoice.HETEROPHYLY);
			break;
		case 4:
			resultSet = new StrategySet();
			resultSet.addFrequency(0.2, Strategy.TaskChoice.PREFERENTIAL);
			resultSet.addFrequency(0.8, Strategy.TaskChoice.HETEROPHYLY);
			break;
		case 5:
			resultSet = new StrategySet();
			resultSet.addFrequency(0.2, Strategy.TaskChoice.HOMOPHYLY);
			resultSet.addFrequency(0.8, Strategy.TaskChoice.PREFERENTIAL);
			break;
		case 6:
			resultSet = new StrategySet();
			resultSet.addFrequency(0.2, Strategy.TaskChoice.HETEROPHYLY);
			resultSet.addFrequency(0.8, Strategy.TaskChoice.PREFERENTIAL);
			break;
		case 7:
			resultSet = new StrategySet();
			resultSet.addFrequency(0.333, Strategy.TaskChoice.HETEROPHYLY);
			resultSet.addFrequency(0.333, Strategy.TaskChoice.HOMOPHYLY);
			resultSet.addFrequency(0.334, Strategy.TaskChoice.PREFERENTIAL);
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
