package test;

import java.util.StringTokenizer;
import repast.simphony.parameter.StringConverter;

/**
 * Recognises a model type from String translation,
 * The model can be a concatenation of one or more options
 * and determines the behaviour of the simulator
 * 
 * - Normal
 * 
 * Normal execution, result-oriented;
 * 
 * - Validation
 * 
 * All assertions enabled, maximum logging and verbose messaging;
 * 
 * - Multiple validation
 * 
 * It assigns different strategies at once;
 * 
 * - Stress
 * 
 * It multiplies the "work left" and "experience left" by x10000;
 * 
 * @author Oskar Jarczyk
 * @since 2.0
 * @version 2.0.9
 */
public class ModelConverter implements StringConverter<Model> {

	/**
	 * Converts the specified object to a String representation and returns that
	 * representation. The representation should be such that
	 * <code>fromString</code> can recreate the Object.
	 * 
	 * @param obj
	 *            the Object to convert.
	 * @return a String representation of the Object.
	 */
	public String toString(Model obj) {
		return obj.getName();
	}

	/**
	 * Creates an Object from a String representation.
	 * 
	 * @param strRep
	 *            the string representation
	 * @return the created Object.
	 */
	public Model fromString(String strRep) {
		Model model = new Model(strRep);
		StringTokenizer st = new StringTokenizer(strRep, "+");
		while (st.hasMoreElements()) {
			String element = (String) st.nextElement();
			if ((element).toLowerCase().equals("normal")) {
				model.setNormal(true);
			}
			if ((element).toLowerCase().equals("validation")) {
				model.setValidation(true);
			}
			if ((element).toLowerCase().equals("multiplevalidation")) {
				model.setMultipleValidation(true);
			}
			if ((element).toLowerCase().equals("stress")) {
				model.setStress(true);
			}
		}
		return model;
	}
}