package strategies;

import java.util.HashMap;
import java.util.Map;

import repast.simphony.random.RandomHelper;
import utils.ObjectsHelper;

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
public class ComplexStrategy extends Strategy implements StrategyInterface {

	/**
	 * This value is used to automatically generate agent identifiers.
	 * 
	 * The Intel 8088 ("eighty-eighty-eight", also called iAPX 88)
	 * microprocessor is a variant of the Intel 8086. Introduced on July 1,
	 * 1979, the 8088 had an 8-bit external data bus instead of the 16-bit bus
	 * of the 8086. The 16-bit registers and the one megabyte address range were
	 * unchanged, however. In fact, according to the Intel documentation, the
	 * 8086 and 8088 have the same execution unit (EU)-only the bus interface
	 * unit (BIU) is different. The original IBM PC was based on the 8088.
	 * 
	 * @field serialVersionUID
	 */
	public static final long serialVersionUID = 8088L;

	private Map<TaskChoice, Double> probability;

	public ComplexStrategy(TaskChoice taskChoice, SkillChoice skillChoice,
			Map<TaskChoice, Double> probability) {
		super(taskChoice, skillChoice);
		this.probability = probability;
	}

	public ComplexStrategy(SkillChoice skillChoice,
			Map<TaskChoice, Double> probability) {
		super(TaskChoice.RANDOM, skillChoice);
		probability = new HashMap<TaskChoice, Double>();
	}

	@Override
	public String toString() {
		// outputs HashMap in a format like:
		// {key1: "value1", key2: "value2", } etc.
		return this.probability.toString() + " ," + this.skillChoice.name();
	}

	@Override
	public Strategy copy() {
		return new ComplexStrategy(getTaskChoice(), this.skillChoice,
				this.probability);
	}

	@Override
	public void copyStrategy(Strategy copyFrom) {
		// this.taskChoice = copyFrom.getTaskChoice();
		this.skillChoice = ((ComplexStrategy) copyFrom).skillChoice;
		this.probability = ((ComplexStrategy) copyFrom).probability;
	}

	@Override
	public TaskChoice getTaskChoice() {
		return ObjectsHelper.getProbKey(RandomHelper.nextDoubleFromTo(0, 1),
				probability);
	}

}
