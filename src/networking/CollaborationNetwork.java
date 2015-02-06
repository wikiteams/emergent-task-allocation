package networking;

import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import collaboration.Agent;
import collaboration.Task;

/***
 * Mostly for viewing network from Repast window
 * - rewiring collaboration
 * 
 * @author Oskar Jarczyk
 * @since 2.0.6
 * @version 2.0.6
 */
public class CollaborationNetwork {
	
	public static Network<Object> collaborationNetwork;
	public static DynamicGexfGraph gephiEngine;
	
	public static void clear(){
		if (collaborationNetwork != null)
			collaborationNetwork.removeEdges();
	}
	
	public static void addEdge(Agent agent, Task task){
		RepastEdge<Object> edge = new RepastEdge<Object>(agent, task, false);
		collaborationNetwork.addEdge(edge);
		persistEdge(agent, task);
	}
	
	public static void persistEdge(Agent agent, Task task){
		gephiEngine.addEdge(agent, task);
	}

}
