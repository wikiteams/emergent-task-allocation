package load;

import java.util.HashSet;
import java.util.Set;

public enum SigmoidParameter {
	
	INSTANCE;
	
	private Set<Integer> parameters = new HashSet<Integer>();
	private Integer chosen;
	
	public boolean isEmpty() {
		return parameters.isEmpty();
	}
	
	public void addParameter(Integer parameter) {
		this.parameters.add(parameter);
	}
	
	public Set<Integer> getParameters() {
		return parameters;
	}
	
	public void setChosen(Integer chosen) {
		this.chosen = chosen;
	}
	
	public Integer getChosen() {
		return this.chosen;
	}

}
