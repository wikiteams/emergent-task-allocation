package intelligence;

import java.util.Comparator;

import collaboration.Agent;

public class AgentComparator implements Comparator<Agent> {
	@Override
	public int compare(Agent o1, Agent o2) {
		double diff = o1.getUtility() - o2.getUtility();
		if (diff == 0)
			return 0;
		else
			return (diff > 0 ? -1 : 1);
	}
}
