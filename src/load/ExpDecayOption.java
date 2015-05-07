package load;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public enum ExpDecayOption {
	
	INSTANCE;
	
	private Set<Boolean> options = new HashSet<Boolean>();
	private Boolean chosen;

	public Boolean getChosen() {
		return chosen;
	}

	public void setChosen(Boolean chosen) {
		this.chosen = chosen;
	}

	public void addOption(Boolean option){
		options.add(option);
	}
	
	public Set<Boolean> getOptions() {
		return options;
	}

	public void setOptions(Set<Boolean> options) {
		this.options = options;
	}
	
	public int size(){
		return options.size();
	}
	
	@Override
	public String toString(){
		StringBuilder returnString = new StringBuilder();
		Iterator<Boolean> iterator = options.iterator();
		for (Boolean bool = iterator.next() ; iterator.hasNext() ; ){
			returnString.append(bool);
			returnString.append(" ");
		}
		return returnString.toString().trim();
	}
}
