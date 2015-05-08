package intelligence;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import utils.MathHelper;
import collaboration.Agent;
import collaboration.SimulationAdvancedParameters;

public class ImpactFactor {

	public static Map<Agent, CircularFifoQueue<Double>> impacts = new HashMap<>();
	public static Map<Agent, CircularFifoQueue<Double>> highestImpacts = new HashMap<>();

	public static void update(Agent agent, Double newImpactFactor) {
		if (impacts.containsKey(agent)) {
			CircularFifoQueue<Double> queue = impacts.get(agent);
			assert MathHelper.isBetweenInc(newImpactFactor, 0.0, 1.0);
			queue.add(newImpactFactor);
		} else {
			CircularFifoQueue<Double> newQueue = new CircularFifoQueue<Double>(
					SimulationAdvancedParameters.IMPACT_MEMORY); // it is size - e.i. 3
			newQueue.add(newImpactFactor);
			assert MathHelper.isBetweenInc(newImpactFactor, 0.0, 1.0);
			impacts.put(agent, newQueue);
		}
		checkIfHighest(agent, newImpactFactor);
	}

	private static void checkIfHighest(Agent agent, Double newImpactFactor) {
		if (highestImpacts.containsKey(agent)) {
			CircularFifoQueue<Double> queue = impacts.get(agent);
			Iterator<Double> elements = queue.iterator(); // iterates, doesn't delete
			boolean thereAreHigher = false;
			while (elements.hasNext()) {
				Double next = elements.next();
				if (next > newImpactFactor) {
					thereAreHigher = true;
				}
			}
			if (!thereAreHigher) {
				queue.add(newImpactFactor);
			}
		} else {
			CircularFifoQueue<Double> newQueue = new CircularFifoQueue<Double>(
					SimulationAdvancedParameters.IMPACT_MEMORY);
			newQueue.add(newImpactFactor);
			highestImpacts.put(agent, newQueue);
		}
	}

	public static Double[] get(Agent agent) {
		// returns array of 3 last registered [Impact Factors]
		CircularFifoQueue<Double> c = impacts.get(agent);
		Double[] result;
		if (c != null){
			result = new Double[c.size()];
			c.toArray(result);
		}
		else
			result = new Double[]{0.0};
		
		assert (c == null) || (c.size() == result.length);
		
		return result;
	}
	
	public static Double[] getHighest(Agent agent) {
		CircularFifoQueue<Double> c = highestImpacts.get(agent);
		Double[] result;
		if (c != null){
			result = new Double[c.size()];
			c.toArray(result);
		}
		else
			result = new Double[]{0.0};
		
		assert (c == null) || (c.size() == result.length);
		
		return result;
	}

	public static void clear() {
		impacts.clear();
		highestImpacts.clear();
	}

}
