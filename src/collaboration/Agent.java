package collaboration;

import repast.simphony.annotate.AgentAnnot;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.ui.probe.ProbeID;

@AgentAnnot(displayName = "Agent")
public class Agent {

	/**
	 * 
	 * This value is used to automatically generate agent identifiers.
	 * 
	 * @field serialVersionUID
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * This value is used to automatically generate agent identifiers.
	 * 
	 * @field agentIDCounter
	 * 
	 */
	protected static long agentIDCounter = 1;

	/**
	 * 
	 * This value is the agent's identifier.
	 * 
	 * @field agentID
	 * 
	 */
	protected String agentID = "Agent " + (agentIDCounter++);
	
	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		System.out.println("Step() of agent: " + this.toString());
	}

	/**
	 * 
	 * This method provides a human-readable name for the agent.
	 * 
	 * @method toString
	 * 
	 */
	@ProbeID()
	public String toString() {
		// Return the results.
		return this.agentID;

	}
}
