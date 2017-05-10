package load;

import java.util.HashSet;
import java.util.Set;

public enum ParametrizedSigmoidOption {
	
	INSTANCE;
	
	private Set<Boolean> options = new HashSet<Boolean>();
	private Boolean chosen;
	
	public boolean isEmpty() {
		return options.isEmpty();
	}
	
	public Set<Boolean> getOptions() {
		return options;
	}
	
	public void addOption(Boolean option) {
		options.add(option);
	}
	
	public void setChosen(Boolean chosen) {
		this.chosen = chosen;
	}
	
	public Boolean getChosen() {
		return this.chosen;
	}

}
