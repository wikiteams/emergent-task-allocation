package collaboration;

/**
 * Learning Process represented by Sigmoid function
 * 
 * @author Oskar Jarczyk
 * @since 1.0
 * @version 1.4
 * 
 */
public class SigmoidCurve {

	private static final double limes = 6;

	// stackoverflow.com/questions/3599579/
	// for-any-finite-floating-point-value-is-it-guaranteed-that-x-x-0
	public static double getDelta(double k) {
		double result = 0;
		double base = 0;
		if (k > 1) {
			return 1.0;
		}
		
		base = (-limes) + (k * (2 * limes));
		result = 1d / (1d + Math.pow(Math.E, -base));

		assert result >= 0.0;
		assert result <= 1.0;

		return result;
	}

	public static double getCustomDelta(double k, double parameterD) {
		double result = 0;
		double base = 0;
		if (k > 1) {
			return 1.0;
		}
		
		base = (-limes) + (k * (2 * limes));
		result = 1d / (1d + parameterD * Math.pow(Math.E, -base));

		assert result >= 0.0;
		assert result <= 1.0;

		return result;
	}
}
