package constants;

public class LoadSet {
	
	public static final LoadSet EMPTY = new LoadSet();
	
	private LoadSet(){
		AGENT_COUNT = 0;
		TASK_COUNT = 0;
	}
	
	public int AGENT_COUNT;
	public int TASK_COUNT;

}
