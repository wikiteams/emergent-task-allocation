package collaboration;

import repast.simphony.context.DefaultContext;
import repast.simphony.context.space.graph.NetworkBuilder;

public class AgentPool extends DefaultContext<Object> {
	
	public AgentPool() {
		super("Agents");
		new NetworkBuilder<Object>("agentsToTasks", this, false).buildNetwork();
	}
	
}
