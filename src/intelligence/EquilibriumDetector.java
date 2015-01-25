package intelligence;

import java.util.Iterator;
import java.util.Map;

import strategies.Strategy.TaskChoice;
import utils.ObjectsHelper;
import collaboration.GameController;
import collaboration.SimulationParameters;

/**
 * Class responsible for finding a stable set of strategies
 * 
 * @author Oskar Jarczyk
 * @since 2.0.09
 */
public class EquilibriumDetector {

	public static CircularFifoQueue<Map<TaskChoice, Integer>> possiblyStableSet;

	public static void init() {
		possiblyStableSet = new CircularFifoQueue<Map<TaskChoice, Integer>>(
				SimulationParameters.equilibriumDetectionSensitivity);
	}
	
	public static void clear(){
		if (possiblyStableSet != null)
			possiblyStableSet.clear();
	}

	public static void report(Map<TaskChoice, Integer> newRecord) {
		possiblyStableSet.add(newRecord);
	}

	public static boolean evaluate(GameController gameController) {
		boolean result = true;
		if (gameController.getCurrentIteration() <= 1){
			// the only reasonable moment to evaluate stop problem
			// is before starting a new generation
			if (possiblyStableSet.isFull()){
				// further more, wait for equilibriumDetectionSensitivity
				// (minimum number of past generation to start an evaluation)
				Iterator<Map<TaskChoice, Integer>> iterator = possiblyStableSet.iterator();
				Map<TaskChoice, Integer> previous = null;
				while (iterator.hasNext()){
					Map<TaskChoice, Integer> current = iterator.next();
					if (! ObjectsHelper.isSecondEqual(current, previous)){
						return false;
					}
					previous = current;
				}
			}
		}
		return result;
	}

}
