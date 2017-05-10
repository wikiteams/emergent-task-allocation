package collaboration;

import java.text.DecimalFormat;

import load.ParametrizedSigmoidOption;
import load.SigmoidParameter;

/**
 * Class describing the learning process of a human for simulation purpose. Uses
 * sigmoid approximation or alternatively ChiSquare CDF.
 * 
 * Allows for simulating experience decay, and cut-point E.
 * 
 * @author Oskar Jarczyk
 * @since 1.0
 * @version 2.0.11
 * 
 */
public class Experience {

	private SigmoidCurve sc = null;

	private double value; // plain experience
	private int top; // hipothetical overlearning

	private static final double expStub = 0.03; // initial knowledge is 3%
	private static final double decayLevel = 0.0005; // 0,05%
	private static final double stupidityLevel = 0.03; // 3%
	
	private double learningParameter;

	private static final double sigmaBorderValue = 1 - calculateCutPoint(6.0);

	protected final static ExperienceSanityCheck esc = new ExperienceSanityCheck();
	public static final double epsilon = 0.00000000001;

	private enum ApproximationMethod {
		SIGMOID, PARAMETRIZED_SIGMOID, CHI_SQUARE
	};

	public Experience() {
		this(0d, 0, false);
	}

	public Experience(boolean passionStub) {
		this(0d, 0, passionStub);
	}

	public Experience(double value, int top) {
		this(value, top, false);
	}

	public Experience(double value, int top, boolean passionStub) {
		if (passionStub) {
			int maxx = SimulationAdvancedParameters.agentSkillsMaximumExperience;
			this.value = maxx * expStub;
			this.top = maxx;
		} else {
			this.value = value;
			this.top = top;
		}
		this.learningParameter = SigmoidParameter.INSTANCE.getChosen();
		createMathematicalCurves();
		System.out.println("Creating Experience object with value: " + this.value
				+ " and top: " + this.top);
	}

	private void createMathematicalCurves() {
		this.sc = new SigmoidCurve();
	}

	private static double calculateCutPoint(double k) {
		return 1d / (1d + Math.pow(Math.E, -k));
	}

	public double getDelta() {
		if (ParametrizedSigmoidOption.INSTANCE.getChosen()) {
			return getDelta(ApproximationMethod.PARAMETRIZED_SIGMOID);
		} else {
			return getDelta(ApproximationMethod.SIGMOID);
		}
	}

	/***
	 * Decays experience by going back on sigmoid function Hence that despite
	 * the fact that only double this.value is modified, it does changes the
	 * result of sigmoid(this.value) that's why it is possible that we do
	 * decrease the experience by going back on sigmoid function
	 * 
	 * Experience is always used together with sigmoid(this.value)
	 * 
	 * @return double - a new value of experience
	 */
	public double decay() {
		if (((this.value) / this.top) <= stupidityLevel) {
			// don't decay
			return -1;
		}
		double howMuch = this.top * decayLevel;
		if (((this.value - howMuch) / this.top) <= stupidityLevel) {
			// never make less than 3%
			this.value = stupidityLevel * this.top;
		} else {
			this.value = this.value - howMuch;
		}
		return this.value / this.top;
	}

	public Boolean decayWithDeath() {
		boolean dies = false;
		double howMuch = this.top * decayLevel;
		if (value - howMuch <= 0) {
			dies = true;
			this.value = 0;
		} else {
			this.value = value - howMuch;
		}
		return dies;
	}

	public double getDelta(ApproximationMethod method) {
		switch (method) {
			case SIGMOID:
				return sc.getDelta((value / top) > 1. ? 1. : (value / top));
			case PARAMETRIZED_SIGMOID:
				return sc.getCustomDelta((value / top) > 1. ? 1. : (value / top), learningParameter);
			default:
				return sc.getDelta((value / top) > 1. ? 1. : (value / top)); // use standard sigmoid
		}
	}

	public double getValue() {
		return value;
	}

	public double getTop() {
		return top;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public void increment(double how_much) {
		this.value += how_much;
		System.out.println("Experience incremented by: "
				+ new DecimalFormat("#.######").format(how_much));
	}
	
	public void incrementAbsolutly(double how_much) {
		assert how_much >= 0.0;
		assert how_much <= 1.0;
		this.value += this.top * how_much;
		System.out.println("Experience incremented by: "
				+ new DecimalFormat("#.######").format(how_much));
	}

	/**
	 * Learning Process represented by Sigmoid function
	 * 
	 * @author Oskar Jarczyk
	 * @since 1.0
	 * @version 1.4
	 * 
	 */
	class SigmoidCurve {

		private final double limes = 6;

		SigmoidCurve() {
			System.out.println("Object SigmoidCurve created, ref: " + this);
		}

		// stackoverflow.com/questions/3599579/
		// for-any-finite-floating-point-value-is-it-guaranteed-that-x-x-0
		protected double getDelta(double k) {
			double result = 0;
			if (!SimulationParameters.experienceCutPoint) {
				double base = 0;
				if (k == 1) {
					result = 1;
				} else if ((k < 0.5) && (k >= 0)) {
					base = (-limes) + (k * (2 * limes));
					result = 1d / (1d + Math.pow(Math.E, -base));
					result = result
							- (Experience.sigmaBorderValue * (Math.abs(1 - (2 * k))));
					if (result < 0)
						result = 0;
				} else if ((k < (1 + epsilon)) && (k > 0.5)) {
					base = (-limes) + (k * (2 * limes));
					result = 1d / (1d + Math.pow(Math.E, -base));
					result = result
							+ (Experience.sigmaBorderValue * (Math.abs(1 - (2 * k))));
					if (result > 1)
						result = 1;
				} else if (k == 0.5) {
					base = (-limes) + (k * (2 * limes));
					result = 1d / (1d + Math.pow(Math.E, -base));
				} else {
					assert false;
					// if not, smth would be wrong
				}
			} else {
				throw new UnsupportedOperationException();
				// TODO: finish implementation
			}

			assert result >= 0.0;
			assert result <= 1.0;

			return result;
		}
		
		protected double getCustomDelta(double k, double parameterD) {
			double result = 0;
			if (!SimulationParameters.experienceCutPoint) {
				double base = 0;
				if (k == 1) {
					result = 1;
				} else if ((k < 0.5) && (k >= 0)) {
					base = (-limes) + (k * (2 * limes));
					result = 1d / (1d + parameterD*Math.pow(Math.E, -base));
					result = result
							- (Experience.sigmaBorderValue * (Math.abs(1 - (2 * k))));
					if (result < 0)
						result = 0;
				} else if ((k < (1 + epsilon)) && (k > 0.5)) {
					base = (-limes) + (k * (2 * limes));
					result = 1d / (1d + parameterD*Math.pow(Math.E, -base));
					result = result
							+ (Experience.sigmaBorderValue * (Math.abs(1 - (2 * k))));
					if (result > 1)
						result = 1;
				} else if (k == 0.5) {
					base = (-limes) + (k * (2 * limes));
					result = 1d / (1d + parameterD*Math.pow(Math.E, -base));
				} else {
					assert false;
					// if not, smth would be wrong
				}
			} else {
				throw new UnsupportedOperationException();
				// TODO: finish implementation
			}

			assert result >= 0.0;
			assert result <= 1.0;

			return result;
		}
	}

}

class ExperienceSanityCheck {

	public static double EpsilonCutValue;

	ExperienceSanityCheck() {
		checkSigmoid();
	}

	public void checkSigmoid() {
		System.out.println("sigmoid(-10.000): " + sigmoidGetDelta(-10d));
		System.out.println("sigmoid(-8.000): " + sigmoidGetDelta(-8d));
		System.out.println("sigmoid(-6.000): " + sigmoidGetDelta(-6d));
		System.out.println("sigmoid(-3.000): " + sigmoidGetDelta(-3d));
		System.out.println("sigmoid(0.000): " + sigmoidGetDelta(0d));
		System.out.println("sigmoid(0.005): " + sigmoidGetDelta(0.005d));
		System.out.println("sigmoid(0.505): " + sigmoidGetDelta(0.505d));
		System.out.println("sigmoid(0.995): " + sigmoidGetDelta(0.995d));
		System.out.println("sigmoid(1.000): " + sigmoidGetDelta(1d));
		System.out.println("sigmoid(3.000): " + sigmoidGetDelta(3d));
		System.out.println("sigmoid(6.000): " + sigmoidGetDelta(6d));
		System.out.println("sigmoid(8.000): " + sigmoidGetDelta(8d));
		System.out.println("sigmoid(10.000): " + sigmoidGetDelta(10d));
	}

	private double sigmoidGetDelta(double k) {
		return 1d / (1d + Math.pow(Math.E, -k));
	}

}
