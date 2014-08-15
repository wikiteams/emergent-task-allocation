package test;

public class Model {

	private String name;
	
	private boolean normal;
	private boolean validation;
	private boolean multipleValidation;
	private boolean stress;

	private int hashCode;

	public Model(String name) {
		this.name = name;
		hashCode = 37 * name.hashCode();
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
		return validation || multipleValidation;
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
					&& (other.hashCode == this.hashCode);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.hashCode;
	}

	@Override
	public String toString() {
		return "Model(" + name + ")";
	}

}