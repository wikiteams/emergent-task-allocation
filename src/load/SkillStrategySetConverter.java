package load;

import java.util.StringTokenizer;

import strategies.Strategy;

public class SkillStrategySetConverter {
	
	/**
	 * Converts the specified object to a String representation and returns that
	 * representation. The representation should be such that
	 * <code>fromString</code> can recreate the Object.
	 * 
	 * @param obj
	 *            the Object to convert.
	 * @return a String representation of the Object.
	 */
	public String toString(SkillStrategySet obj) {
		return obj.toString();
	}

	/**
	 * Creates an Object from a String representation.
	 * 
	 * @param strRep
	 *            the string representation
	 * @return the created Object.
	 */
	public SkillStrategySet fromString(String strRep) {
		StringTokenizer st = new StringTokenizer(strRep, " ");
		while (st.hasMoreElements()) {
			String element = (String) st.nextElement();
			SkillStrategySet.INSTANCE.addStrategy(Strategy.SkillChoice
					.valueOf(element));
		}
		return SkillStrategySet.INSTANCE;
	}

}
