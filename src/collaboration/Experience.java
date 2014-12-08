package collaboration;

import java.text.DecimalFormat;

import logger.PjiitOutputter;
import cern.jet.random.ChiSquare;

/**
 * Class describing the learning process of a human for simulation purpose. 
 * Uses sigmoid approximation or alternatively ChiSquare CDF.
 * 
 * Allows for simulating experience decay, and cut-point E.
 * 
 * @author Oskar Jarczyk
 * @since 1.0
 * @version 2.0.6
 * 
 */
public class Experience {

	private LearningCurve lc = null;
	private SigmoidCurve sc = null;

	private double value; // plain experience
	private int top; // hipothetical overlearning

	private static final double expStub = 0.03; // as it is 0.03
	private static final double decayLevel = 0.0005; // 0,05%
	private static final double stupidityLevel = 0.03; // 3%

	private static final double cutPoint = 1 - calculateCutPoint(6.0);

	protected final static ExperienceSanityCheck esc = new ExperienceSanityCheck();

	private enum ApproximationMethod {
		SIGMOID, CHI_SQUARE
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
			int maxx = SimulationParameters.agentSkillsMaximumExperience;
			this.value = maxx * expStub;
			this.top = maxx;
		} else {
			this.value = value;
			this.top = top;
		}
		createMathematicalCurves();
		say("Creating Experience object with value: " + this.value
				+ " and top: " + this.top);
	}

	private void createMathematicalCurves() {
		this.lc = new LearningCurve();
		this.sc = new SigmoidCurve();
	}

	private static double calculateCutPoint(double k) {
		return 1d / (1d + Math.pow(Math.E, -k));
	}

	public double getDelta() {
		return getDelta(ApproximationMethod.SIGMOID);
	}

	/***
	 * Decays experience by going back on sigmoig function
	 * Hence that despite the fact that only double this.value
	 * is modified, it does changes the result of sigmoid(this.value)
	 * that's why it is possible that we do decrease the experience
	 * by going back on sigmoig function
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
		case CHI_SQUARE:
			return lc.getDelta((value / top) > 1. ? 1. : (value / top));
		default:
			break;
		}
		return lc.getDelta((value / top));
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
		DecimalFormat df = new DecimalFormat("#.######");
		sanity("Experience incremented by: " + df.format(how_much));
	}

	private void say(String s) {
		PjiitOutputter.say(s);
	}

	private void sanity(String s) {
		PjiitOutputter.sanity(s);
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

		private double limes = 6;

		SigmoidCurve() {
			say("Object SigmoidCurve created, ref: " + this);
		}

		protected double getDelta(double k) {
			double result = 0;
			if (!SimulationParameters.experienceCutPoint) {
				double base = 0;
				if (k == 1.){
					result = 1;
				} else
				if ((k < 0.5) && (k >= 0.)) {
					base = (-limes) + (k * (2 * limes));
					result = 1d / (1d + Math.pow(Math.E, -base));
					result = result - (Experience.cutPoint * (Math.abs(1-(2*k))));
					if (result < 0.) result = 0.; // because of possible precision issues
				} else if ((k < 1.001) && (k > 0.5)) {
					base = (-limes) + (k * (2 * limes));
					result = 1d / (1d + Math.pow(Math.E, -base));
					result = result + (Experience.cutPoint * (Math.abs(1-(2*k))));
					if (result > 1.) result = 1.; // possible precision issues
				} else if (k == 0.5){
					base = (-limes) + (k * (2 * limes));
					result = 1d / (1d + Math.pow(Math.E, -base));
				}
				else {
					assert false;
					// if not, smth would be wrong
				}
			} else {
				throw new UnsupportedOperationException();
				// TODO: finish implementation
			}
			
			assert result >= 0.;
			assert result <= 1.;
			
			return result;
		}
	}

	/**
	 * 
	 * To jest nasza funkcja delty! delta(E) Ta klasa nie ma nic wspolnego ze
	 * zmienna E (doswiadczenia) a sluzy jedyni otrzymaniu wartosci delta z E
	 * 
	 * @author Oskar
	 * @since 1.1
	 */
	class LearningCurve {

		cern.jet.random.ChiSquare chi = null;

		double xLearningAxis = 15; // osi x
		int freedom = 6;

		LearningCurve() {
			say("Object LearningCurve created, with ref: " + this);
			chi = new ChiSquare(freedom,
					cern.jet.random.ChiSquare.makeDefaultGenerator());
		}

		private double getDelta(double k) {
			double x = chi.cdf(k * xLearningAxis);
			DecimalFormat df = new DecimalFormat("#.######");
			// NOTE: freedom (x axis of CDF) should be between 0 and 4
			say("getDelta for k: " + df.format(k) + " returned x:"
					+ df.format(x));
			return x;
		}

	}
}

class ExperienceSanityCheck {

	private ChiSquare chi;
	private int freedom;
	private int k;
	// private SigmoidCurve sigmoidCurve;
	public static double EpsilonCutValue;

	ExperienceSanityCheck() {
		freedom = 6; // osi x
		k = 15;

		chi = new ChiSquare(freedom,
				cern.jet.random.ChiSquare.makeDefaultGenerator());
		// sigmoidCurve = new SigmoidCurve();

		checkChi();
		checkSigmoid();

		EpsilonCutValue = checkEpsilonFromChi();
	}

	public void checkChi() {
		say("chi.cdf(0.1): " + chi.cdf(0.1 * k));
		say("chi.cdf(0.2): " + chi.cdf(0.2 * k));
		say("chi.cdf(0.3): " + chi.cdf(0.3 * k));
		say("chi.cdf(0.6): " + chi.cdf(0.6 * k));
		say("chi.cdf(0.8): " + chi.cdf(0.8 * k));
		say("chi.cdf(0.9): " + chi.cdf(0.9 * k));
		say("chi.cdf(0.95): " + chi.cdf(0.95 * k));
		say("chi.cdf(0.9): " + chi.cdf(0.999 * k));
		say("chi.nextDouble(): " + chi.nextDouble());
	}

	public void checkSigmoid() {
		say("sigmoid(-10.000): " + sigmoidGetDelta(-10d));
		say("sigmoid(-8.000): " + sigmoidGetDelta(-8d));
		say("sigmoid(-6.000): " + sigmoidGetDelta(-6d));
		say("sigmoid(-3.000): " + sigmoidGetDelta(-3d));
		say("sigmoid(0.000): " + sigmoidGetDelta(0d));
		say("sigmoid(0.005): " + sigmoidGetDelta(0.005d));
		say("sigmoid(0.505): " + sigmoidGetDelta(0.505d));
		say("sigmoid(0.995): " + sigmoidGetDelta(0.995d));
		say("sigmoid(1.000): " + sigmoidGetDelta(1d));
		say("sigmoid(3.000): " + sigmoidGetDelta(3d));
		say("sigmoid(6.000): " + sigmoidGetDelta(6d));
		say("sigmoid(8.000): " + sigmoidGetDelta(8d));
		say("sigmoid(10.000): " + sigmoidGetDelta(10d));
	}

	public double checkEpsilonFromChi() {
		double e = chi.cdf(1 * k);
		say("chi.cdf(1): " + e);
		return 1 - e;
	}

	private double sigmoidGetDelta(double k) {
		return 1d / (1d + Math.pow(Math.E, -k));
	}

	private void say(String s) {
		PjiitOutputter.say(s);
	}

}
