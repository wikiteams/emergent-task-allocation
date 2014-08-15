package constants;

import test.Model;

/**
 * Tells whether we want to make model validations or just move to main
 * execution of simulation universe
 * 
 * @author Oskar Jarczyk
 * @since 1.3
 * 
 */
public class ModelFactory {

	private Model functionality;

	public ModelFactory(Model model) {
		this.functionality = model;
	}

	public Model getFunctionality() {
		return functionality;
	}

	public void setFunctionality(Model functionality) {
		this.functionality = functionality;
	}

	@Override
	public String toString() {
		return functionality.getName();
	}
	
	@Override
	public int hashCode() {
		return functionality.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ModelFactory) {
			ModelFactory other = (ModelFactory) obj;
			return other.functionality.equals(this.functionality);
		}
		return false;
	}
}
