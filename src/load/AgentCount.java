package load;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public enum AgentCount {
	
	INSTANCE;
	
	private Set<Integer> counts = new HashSet<Integer>();

	public void addCount(Integer count){
		counts.add(count);
	}
	
	public Set<Integer> getCounts() {
		return counts;
	}

	public void setCounts(Set<Integer> counts) {
		this.counts = counts;
	}
	
	public int size(){
		return counts.size();
	}
	
	@Override
	public String toString(){
		StringBuilder returnString = new StringBuilder();
		Iterator<Integer> iterator = counts.iterator();
		for (Integer integer = iterator.next() ; iterator.hasNext() ; ){
			returnString.append(integer);
			returnString.append(" ");
		}
		return returnString.toString().trim();
	}
}
