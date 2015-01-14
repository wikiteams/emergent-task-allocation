package strategies;

public interface StrategyInterface {
	
	public abstract Strategy copy();
	public abstract void copyStrategy(Strategy copyFrom);
	public abstract Strategy.TaskChoice getTaskChoice();

}
