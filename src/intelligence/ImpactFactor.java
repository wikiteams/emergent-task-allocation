package intelligence;

import java.util.HashMap;
import java.util.Map;

import collaboration.Agent;
import collaboration.SimulationParameters;

public class ImpactFactor {

	public static Map<Agent, CircularFifoQueue<Double>> impacts = new HashMap<>();

	public static void update(Agent agent, Double newImpactFactor) {
		if (impacts.containsKey(agent)) {
			CircularFifoQueue<Double> queue = impacts.get(agent);
			queue.add(newImpactFactor);
		} else {
			CircularFifoQueue<Double> newQueue = new CircularFifoQueue<Double>(
					SimulationParameters.IMPACT_MEMORY);
			impacts.put(agent, newQueue);
		}
	}
	
	public static Double[] get(Agent agent){
		Double[] result = new Double[3];
		result = impacts.get(agent).toArray(result);
		return result;
	}
	
	public static void clear(){
		impacts.clear();
	}

}
