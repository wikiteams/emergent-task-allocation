package test;

/***
 * Possible values:
 * 
 * Normal - self-explanatory, for conducting the experiment :) 
 * 
 * Validation - all assertions enabled, maximum logging and verbose messaging
 * 
 * Multiple validation - checks in one batch run all important parameters (through randomising)
 * 
 * Stress - Creates hilariously high values in test tasks 
 * and test agents, it multiplies the "work left" and
 * "experience left" by x10000
 * 
 * @author Oskar Jarczyk
 * @since 2.0
 * @version 2.0.9
 */
public class Model {

	private String name;

	private boolean normal;
	private boolean validation;
	private boolean multipleValidation;
	private boolean stress;

	public Model(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isNormal() {
		return normal;
	}

	public void setNormal(boolean normal) {
		this.normal = normal;
	}

	public boolean isValidation() {
		return validation;
	}

	public void setValidation(boolean validation) {
		this.validation = validation;
	}

	public boolean isMultipleValidation() {
		return multipleValidation;
	}

	public boolean isSingleValidation() {
		return validation;
	}

	public void setMultipleValidation(boolean multipleValidation) {
		this.multipleValidation = multipleValidation;
	}

	public boolean isStress() {
		return stress;
	}

	public void setStress(boolean stress) {
		this.stress = stress;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Model) {
			Model other = (Model) obj;
			return other.name.equals(this.name)
					&& (other.hashCode() == this.hashCode());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 37 * this.name.hashCode();
	}

	@Override
	public String toString() {
		return "Model(" + name + ")";
	}

}