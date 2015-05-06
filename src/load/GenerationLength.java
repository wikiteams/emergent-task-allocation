package load;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public enum GenerationLength {
	
	INSTANCE;
	
	private Set<Integer> lengths = new HashSet<Integer>();

	public void addLength(Integer length){
		lengths.add(length);
	}
	
	public Set<Integer> getLengths() {
		return lengths;
	}

	public void setCounts(Set<Integer> lengths) {
		this.lengths = lengths;
	}
	
	public int size(){
		return lengths.size();
	}
	
	@Override
	public String toString(){
		StringBuilder returnString = new StringBuilder();
		Iterator<Integer> iterator = lengths.iterator();
		for (Integer integer = iterator.next() ; iterator.hasNext() ; ){
			returnString.append(integer);
			returnString.append(" ");
		}
		return returnString.toString().trim();
	}
}
