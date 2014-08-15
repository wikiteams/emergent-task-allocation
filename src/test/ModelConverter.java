package test;

import java.util.StringTokenizer;

import repast.simphony.parameter.StringConverter;

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
			Object element = st.nextElement();
			if (((String) element).toLowerCase().equals("normal")) {
				model.setNormal(true);
			}
			if (((String) element).toLowerCase().equals("validation")) {
				model.setValidation(true);
			}
			if (((String) element).toLowerCase().equals("multiplevalidation")) {
				model.setMultipleValidation(true);
			}
			if (((String) element).toLowerCase().equals("stress")) {
				model.setStress(true);
			}
		}
		return model;
	}
}