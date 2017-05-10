package load;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public enum LeftFunctionSet {

	INSTANCE;

	private Set<String> leftParts = new HashSet<String>();
	private String chosen;

	public void setChosen(String leftPart) {
		chosen = leftPart;
	}

	public String getChosen() {
		return chosen;
	}
	
	public Boolean isChosenMinimum(){
		return chosen.equals("min") ? true : false;
	}
	
	public Boolean isChosenAverage(){
		return chosen.equals("avg") || chosen.equals("mean") ? true : false;
	}
	
	public Boolean isChosenAverageAll(){
		return chosen.equals("avg-all") || chosen.equals("mean-all") ? true : false;
	}

	public void addLeftPart(String leftPart) {
		leftParts.add(leftPart);
	}

	public Set<String> getLeftParts() {
		return leftParts;
	}

	public void setLeftParts(Set<String> leftParts) {
		this.leftParts = leftParts;
	}

	public int size() {
		return leftParts.size();
	}

	@Override
	public String toString() {
		StringBuilder returnString = new StringBuilder();
		Iterator<String> iterator = leftParts.iterator();
		for (String leftPart = iterator.next(); iterator.hasNext();) {
			returnString.append(leftPart);
			returnString.append(" ");
		}
		return returnString.toString().trim();
	}

	public boolean isEmpty() {
		return leftParts.isEmpty();
	}
}
